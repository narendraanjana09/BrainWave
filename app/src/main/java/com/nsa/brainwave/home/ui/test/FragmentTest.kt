package com.nsa.brainwave.home.ui.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentTestBinding
import com.nsa.brainwave.home.adapters.OptionsAdapter
import com.nsa.brainwave.home.models.Test.Test
import com.nsa.brainwave.home.viewmodels.TestViewModel
import com.nsa.brainwave.home.viewmodels.TimerViewModel
import com.nsa.brainwave.util.Util


class FragmentTest : Fragment() {


    private lateinit var binding: FragmentTestBinding
    private val auth= Firebase.auth
    private val args:FragmentTestArgs by navArgs()

    private val viewModel:TestViewModel by lazy {
        ViewModelProvider(this).get(TestViewModel::class.java)
    }

    private val timerViewModel:TimerViewModel by lazy {
        ViewModelProvider(this).get(TimerViewModel::class.java)
    }

    private lateinit var optionsAdapter: OptionsAdapter
    private val optionsList= arrayListOf<String>()

    private val maxTime=5*60 // 5 min converting to seconds



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_test, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.subjectNameTv.text=args.TestModel.subject

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        binding.optionsRv.layoutManager = layoutManager
        binding.optionsRv.adapter=optionsAdapter

        binding.backBtn.setOnClickListener {
           if (args.isTest){
               MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialogThemse)
                   .setTitle("Close Test?")
                   .setMessage("Are you sure you want to close test?")
                   .setNegativeButton("No") { dialog, which ->
                       dialog.dismiss()
                   }
                   .setPositiveButton("Yes") { dialog, which ->
                       timerViewModel.stop()
                       findNavController().popBackStack()
                   }
                   .show()
           }else{
               findNavController().popBackStack()
           }

        }

        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentTest)
            }

        }
        viewModel.message.observe(viewLifecycleOwner){
            Util.showToast(requireContext(), it)
        }


        viewModel.currentQuestion.observe(viewLifecycleOwner){
            binding.apply {
                questionTv.text=it.question
                if(it.selectedOption!=null){
                    optionsAdapter.setOptionSelected(it.selectedOption!!)
                }else{
                    optionsAdapter.setOptionSelected(-1)
                }

                optionsList.clear()
                optionsList.addAll(it.options!!)
                optionsAdapter.correctOption(it.correctOption!!)
                optionsAdapter.notifyItemRangeChanged(0,it.options.size)
            }
        }
        binding.nextQuestionBtn.setOnClickListener {
            viewModel.showQuetion(viewModel.questionNo.value!!+1)
        }
        binding.prevQuestionBtn.setOnClickListener {
            viewModel.showQuetion(viewModel.questionNo.value!!-1)
        }
        binding.submitTestBtn.setOnClickListener {
            if(args.isTest){

                val options_selected_counter=viewModel.countOptionsSelected()

                MaterialAlertDialogBuilder(requireContext(),R.style.MaterialAlertDialogThemse)
                    .setTitle("Submit Test")
                    .setMessage("You have selected mcq for $options_selected_counter questions. Are you sure you want to submit test?  ")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Submit") { dialog, which ->
                        timerViewModel.stop()
                        finishTest(maxTime-binding.timerBar.progress,viewModel.testModel.value)
                    }
                    .show()
            }else{
                findNavController().popBackStack()
            }

        }
        viewModel.questionNo.observe(viewLifecycleOwner){
            binding.apply {
            questionCountTv.text="Question ${it+1}/${args.TestModel.questions!!.size}"
            if(it>0){
                prevQuestionBtn.visibility=View.VISIBLE
            }else{
                prevQuestionBtn.visibility=View.GONE
            }
            if(it>=args.TestModel.questions!!.size-1){
                submitTestBtn.visibility=View.VISIBLE
                nextQuestionBtn.visibility=View.GONE
            }else{
                submitTestBtn.visibility=View.GONE
                nextQuestionBtn.visibility=View.VISIBLE
            }
            }

        }
        viewModel.setTest(args.TestModel)
        viewModel.showQuetion(0)


        if(args.isTest){
            startTimer()
        }else{
            binding.submitTestBtn.text="Finish"
            binding.timerBar.visibility=View.INVISIBLE
            binding.timeTv.visibility=View.INVISIBLE
        }
        viewModel.reportModel.observe(viewLifecycleOwner){
                findNavController().navigate(
                FragmentTestDirections.actionFragmentTestToFragmentScoreCard(it!!)
            )
        }
    }

    private fun startTimer() {
        timerViewModel.progress.observe(viewLifecycleOwner){
            binding.timerBar.progress=it
            if(it==0){
                finishTest(maxTime-it,viewModel.testModel.value)
            }

        }
        timerViewModel.countDown.observe(viewLifecycleOwner){
            binding.timeTv.text=it
        }
        timerViewModel.start(maxTime)
        binding.timerBar.apply {
            max=maxTime
        }
    }

    private fun finishTest(time: Int, test: Test?) {
        viewModel.finishTest(time,test!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        optionsAdapter= OptionsAdapter(args.isTest,optionsList,object :OptionsAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                viewModel.optionSelected(position)
            }
        })
    }

}