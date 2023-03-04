package com.nsa.brainwave.Login.frags

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.App
import com.nsa.brainwave.MainActivity
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentLogoBinding
import com.nsa.brainwave.util.Util
import com.nsa.myApp.SharedPref


class FragmentLogo : Fragment() {


    private lateinit var binding: FragmentLogoBinding

    private val auth= Firebase.auth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_logo, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object:CountDownTimer(1000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {

                val user=Firebase.auth.currentUser
                if(user!=null && App.prefs.pull(SharedPref.userLoggedIn,fallback = false)){
                    if(App.prefs.pull(SharedPref.examSelected)){
                        (requireActivity() as MainActivity).goToHome()
                    }else{
                        findNavController().navigate(
                            FragmentLogoDirections.actionFragmentLogoToFragmentChooseExam()
                        )
                    }
                }else{
                    findNavController().navigate(
                        FragmentLogoDirections.actionFragmentLogoToFragmentLogin()
                    )
                }

            }
        }.start()
    }

}