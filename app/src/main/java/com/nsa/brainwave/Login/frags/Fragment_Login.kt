package com.nsa.brainwave.Login.frags

import android.R.attr.password
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.App
import com.nsa.brainwave.MainActivity
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentLoginBinding
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast
import com.nsa.myApp.SharedPref


class Fragment_Login : Fragment() {


    private lateinit var binding: FragmentLoginBinding
   private val db = Firebase.firestore
    private val auth=Firebase.auth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val args: Fragment_LoginArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        return binding.root;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.webClient))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }
    val startGoogleSignIn = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        Util.showProgress(findNavController())
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    val user=auth.currentUser
                                    val profileUpdates = userProfileChangeRequest {
                                        displayName = credential.displayName
                                        photoUri=credential.profilePictureUri
                                    }
                                    user!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                checkForExam(auth.currentUser!!)
                                                Log.e("TAG", "User profile updated.")
                                            }else{
                                                Util.hideProgress(findNavController(),R.id.fragment_Login)
                                                showToast(requireContext(),"Profile update error")
                                            }
                                        }
                                }else{
                                    Util.hideProgress(findNavController(),R.id.fragment_Login)
                                    showToast(requireContext(),"GoogleSignInError")
                                }
                            }
                        Log.e("TAG", "Got ID token.")
                    }
                    else -> {
                        showToast(requireContext(),"error1")
                        Util.hideProgress(findNavController(),R.id.fragment_Login)
                        // Shouldn't happen.
                        Log.e("TAG", "No ID token!")
                    }
                }
            } catch (e: ApiException) {
                showToast(requireContext(),"error2")
                Util.hideProgress(findNavController(),R.id.fragment_Login)
               e.printStackTrace()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.googleLoginBtn.setOnClickListener {
           startGoogleLogin()
        }
        if(args.isGoogleSign){
            startGoogleLogin()
        }

        //for removing spaces while typing in edit_text
        binding.pwdEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().contains(" ")) {
                    val newText = s.toString().replace(" ", "")
                    binding.pwdEd.setText(newText)
                    binding.pwdEd.setSelection(newText.length)
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
        binding.emailEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().contains(" ")) {
                    val newText = s.toString().replace(" ", "")
                    binding.emailEd.setText(newText)
                    binding.emailEd.setSelection(newText.length)
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        //for moving to sign up page
        binding.gotoSignUp.setOnClickListener {
            findNavController().navigate(
                Fragment_LoginDirections.actionFragmentLoginToFragmentSignUp()
            )
        }
        binding.loginBtn.setOnClickListener {
            Util.showProgress(findNavController())
            val email=binding.emailEd.text.toString().trim()
            val password=binding.pwdEd.text.toString().trim()
            if(validate(email,password)){
                startWithSignIn(email,password)
            }else{
                Util.hideProgress(findNavController(),R.id.fragment_Login)
            }
        }
        binding.forgetPwd.setOnClickListener {
            val email=binding.emailEd.text.toString().trim()
            if(!Util.isValidEmail(email)){
                showToast(requireContext(),"Email is not valid")
            }else{
                Util.showProgress(findNavController())
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        Util.hideProgress(findNavController(),R.id.fragment_Login)
                        if (task.isSuccessful) {
                            Util.showMessageSheet("Reset password","We have sent you a email to reset your password\n\n\"please check your spam folder as well\" ",requireActivity())
                        } else {
                            Util.showMessageSheet("Reset password error!",task.exception?.message!!,requireActivity())
                        }
                    }
            }
        }

    }

    private fun startGoogleLogin() {
        Util.showProgress(findNavController())
        oneTapClient.beginSignIn(signInRequest)
            .addOnCompleteListener {
                Util.hideProgress(findNavController(),R.id.fragment_Login)
                if (it.isSuccessful) {
                    Log.d("TAG","SignInSuccess launching activity result")
                    startGoogleSignIn.launch(IntentSenderRequest.Builder(
                        it.result.pendingIntent.intentSender
                    ).build())
                } else {
                    Log.d("SignInError", it.exception!!.localizedMessage)
                }
            }
    }

    private fun validate( email: String, password: String): Boolean {

        if(!Util.isValidEmail(email)){
            showToast(requireContext(),"Email is not valid")
            return false
        }

        if(password.length<8){
            showToast(requireContext(),"Password must contain 6 characters")
            return false
        }

        return true

    }

    private fun startWithSignIn( email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    if(user!!.isEmailVerified){
                        showToast(requireContext(),"Sign In Success.")
                       checkForExam(user)
                    }else{
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener {
                                Util.showMessageSheet("Email not verified!","Please verify you email address to continue we have sent a verification link to your email\n\n" +
                                        "\"please also check your spam folder\"",requireActivity())

                                auth.signOut()
                                Util.hideProgress(findNavController(),R.id.fragment_Login)
                            }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("TAG", "signInWithEmail:failure", task.exception)
                    Util.showMessageSheet("Sign in failed!",task.exception?.message!!,requireActivity())
                  //Util.showMessageSheet("Sign in failed!","Sorry we couldn't found a account with this email.\nIf you are a new user try signup",requireActivity())
                    Util.hideProgress(findNavController(),R.id.fragment_Login)
                }
            }.addOnFailureListener {
                Log.e("TAG", "signInWithEmail:failure ${it.message}")
                Util.hideProgress(findNavController(),R.id.fragment_Login)
            }


    }


    private fun checkForExam(user: FirebaseUser) {
        App.prefs.push(SharedPref.userLoggedIn,true)
        val docRef = db.collection("users").document(user.uid)

        docRef.get().addOnCompleteListener { task ->
            Util.hideProgress(findNavController(),R.id.fragment_Login)
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    App.prefs.push(SharedPref.examSelected,true)
                    (requireActivity() as MainActivity).goToHome()
                } else {
                    findNavController().navigate(
                        Fragment_LoginDirections.actionFragmentLoginToFragmentChooseExam()
                    )
                }
            } else {
               showToast(requireContext(),"error, try again later!")
            }
        }
    }
}