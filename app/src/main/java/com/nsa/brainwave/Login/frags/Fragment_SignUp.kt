package com.nsa.brainwave.Login.frags

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentSignUpBinding
import com.nsa.brainwave.util.Util


class Fragment_SignUp : Fragment() {


    private lateinit var binding: FragmentSignUpBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.googleSignUpBtn.setOnClickListener {
            findNavController().navigate(
                Fragment_SignUpDirections.actionFragmentSignUpToFragmentLogin(isGoogleSign = true)
            )
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

        //for moving back to sign in page
        binding.gotoSignIn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.signUpBtn.setOnClickListener {
            Util.showProgress(findNavController())
            val name=binding.nameEd.text.toString().trim()
            val email=binding.emailEd.text.toString().trim()
            val password=binding.pwdEd.text.toString().trim()
            if(validate(name,email,password)){
                startWithSignIn(name,email,password)
            }else{
                Util.hideProgress(findNavController(),R.id.fragment_SignUp)
            }
        }
    }

    private fun startWithSignIn(name: String, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("TAG", "signInWithEmail:success")
                    showToast("User Already Registered, kindly sign in.")
                    Util.hideProgress(findNavController(),R.id.fragment_SignUp)
                    auth.signOut()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("TAG", "signInWithEmail:failure", task.exception)
                    startSignUpProcess(name,email,password)
                }
            }


    }

    private fun startSignUpProcess(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                user?.sendEmailVerification()
                                    ?.addOnCompleteListener {
                                        showToast("Registered Successfully!")
                                        showSnack("we have sent a verfication link to your email\n" +
                                                "please verify to login.(please also check your spam folder)")

                                        Util.hideProgress(findNavController(),R.id.fragment_SignUp)
                                        auth.signOut()
                                    }


                                Log.e("TAG", "User profile updated.")
                            }else{
                                Util.hideProgress(findNavController(),R.id.fragment_SignUp)
                                showToast("Profile update error")
                            }
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Util.hideProgress(findNavController(),R.id.fragment_SignUp)
                    Log.e("TAG", "createUserWithEmail:failure", task.exception)
                    Util.showMessageSheet("Sign up failed!",task.exception?.message!!,requireActivity())


                }

            }

    }
    private fun showSnack(message: String){
       Util.showMessageSheet("Sign up",message,requireActivity())
    }

    private fun showToast(message:String){
        Util.showToast(requireContext(),message)
    }

    private fun validate(name: String, email: String, password: String): Boolean {
        if(name.isEmpty()){
            showToast("Name can't be empty")
            return false
        }

        if(!Util.isValidEmail(email)){
            showToast("Email is not valid")
            return false
        }

        if(password.length<8){
            showToast("Password must contain 6 characters")
            return false
        }

        return true

    }

}