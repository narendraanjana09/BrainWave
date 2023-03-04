package com.nsa.brainwave.home.ui

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentLogoBinding
import com.nsa.brainwave.databinding.FragmentProfileBinding
import com.nsa.brainwave.home.HomeActivity
import com.nsa.brainwave.home.viewmodels.HomeViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast
import com.nsa.brainwave.util.Util.smoothProgress
import com.nsa.myApp.SharedPref


class FragmentProfile : Fragment() {


    private lateinit var binding: FragmentProfileBinding

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity() as HomeActivity).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                viewModel.uploadUserImage(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Log.e("TAG",  ImagePicker.getError(data))
            } else {
                Log.e("TAG", "Task Cancelled")
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentProfile)
            }

        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getUserData()
        }
        viewModel.message.observe(viewLifecycleOwner){
            showToast(requireContext(),it)
        }
        binding.editProfileBtn.setOnClickListener {
            ImagePicker.with(requireActivity())
                .cropSquare()
                .galleryOnly()//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        viewModel.userModel.observe(viewLifecycleOwner){
            binding.swipeRefresh.isRefreshing=false
            binding.apply {
                nameTv.text= it.name!![0].uppercase()+it.name.substring(1,it.name.length)
                Util.loadImage(it.photoUrl!!,profileImageView)
                examTv.text=it.exam
            }
        }
        viewModel.userDataModel.observe(viewLifecycleOwner){
            binding.apply {
                val progress=Util.calculateProgress(it.totalAttended!!.toDouble(),it.correctSolved!!.toDouble(),it.missed!!.toDouble()).toInt()
                progressBar.smoothProgress(if(progress==0) 1 else progress)
                progressTv.text="$progress%"

                totalQuestionItem.apply {
                    dataTv.text="${it.totalAttended}"
                    titleTv.text="Total Solved"
                }
                correctQuestionItem.apply {
                    titleTv.text="Correct"
                    dataTv.apply {
                        text="${it.correctSolved}"
                        setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                    }
                }
                wrongQuestionItem.apply {
                    titleTv.text="Wrong"
                    dataTv.apply {
                        text="${(it.totalAttended!!-it.correctSolved!!-it.missed!!)}"
                        setTextColor(ContextCompat.getColor(requireContext(),R.color.red_light))
                    }
                }


            }
        }


    }

}