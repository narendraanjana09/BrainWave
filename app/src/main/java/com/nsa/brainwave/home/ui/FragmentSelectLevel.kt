package com.nsa.brainwave.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentSelectLevelBinding
import com.nsa.brainwave.home.HomeActivity
import com.nsa.brainwave.home.models.Test.Test
import com.nsa.brainwave.home.viewmodels.HomeViewModel
import com.nsa.brainwave.home.viewmodels.PassDataViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast


class FragmentSelectLevel : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentSelectLevelBinding

    private val auth= Firebase.auth
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val passDataViewModel: PassDataViewModel by lazy {
        ViewModelProvider(requireActivity() as HomeActivity).get(PassDataViewModel::class.java)
    }

    private val args:FragmentSelectLevelArgs by navArgs()
    private  var testModel:Test?=null
    var gotTestData=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_select_level, container, false)
        return binding.root;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }


    private fun navigateToTest() {
        findNavController().navigate(
            FragmentSelectLevelDirections.actionFragmentSelectLevelToFragmentTest(true,testModel!!)
        )
        dialog?.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentSelectLevel)
            }

        }
        viewModel.message.observe(viewLifecycleOwner){
            showToast(requireContext(),it)
        }

        viewModel.testModel.observe(viewLifecycleOwner){
            if(it!=null){
                testModel=it
                gotTestData=true
                Log.e("TAG", "onTestCreated: $it ", )
                if(passDataViewModel.ad_dismissed.value==true){
                    navigateToTest()
                }
            }
        }
        passDataViewModel.ad_dismissed.observe(viewLifecycleOwner){
            if(testModel!=null){
                navigateToTest()
            }
        }
        binding.levelBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.levelTv.text="Level: $p1"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        binding.startTestBtn.setOnClickListener {
            viewModel.getTestData(args.exam,args.subject,binding.levelBar.progress)
            (requireActivity() as HomeActivity).showAd()
        }


    }

}