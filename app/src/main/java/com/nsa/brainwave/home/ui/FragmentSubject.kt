package com.nsa.brainwave.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentSubjectBinding
import com.nsa.brainwave.home.adapters.PrevTestsAdapter
import com.nsa.brainwave.home.models.Report.TestReport
import com.nsa.brainwave.home.viewmodels.TestViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast


class FragmentSubject : Fragment() {


    private lateinit var binding:FragmentSubjectBinding
    private val testViewModel: TestViewModel by lazy {
        ViewModelProvider(this).get(TestViewModel::class.java)
    }

    private val args:FragmentSubjectArgs by navArgs()
    private val prevTestList= arrayListOf<TestReport>()
    private lateinit var prevTestsAdapter: PrevTestsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_subject, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.subjectNameTv.text=args.subject
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.startTestBtn.setOnClickListener {
            findNavController().navigate(
                FragmentSubjectDirections.actionFragmentSubjectToFragmentSelectLevel(args.exam,args.subject)
            )
        }
        binding.recyclerView.adapter=prevTestsAdapter

        testViewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentSubject)
            }

        }
        testViewModel.message.observe(viewLifecycleOwner){
           showToast(requireContext(), it)
        }
        testViewModel.previousTestList.observe(viewLifecycleOwner){ test->
            binding.swipeRefresh.isRefreshing=false
            if(test.isNullOrEmpty()){
                binding.previousTestTv.text=HtmlCompat.fromHtml(
                    "Previous Tests<br><br>${Util.getColoredText("No Test Found","#FF6D6D")}"
                , HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }else{
                binding.previousTestTv.text="Previous Tests"
            prevTestList.clear()
            prevTestList.addAll(test)
            prevTestsAdapter.notifyItemRangeChanged(0,prevTestList.size)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            testViewModel.getPreviousTests(args.subject)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prevTestsAdapter= PrevTestsAdapter(prevTestList,object :PrevTestsAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                findNavController().navigate(
                    FragmentSubjectDirections.actionFragmentSubjectToFragmentScoreCard(prevTestList[position])
                )
            }
        })
        testViewModel.getPreviousTests(args.subject)
    }




}