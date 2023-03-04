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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentScoreCardBinding
import com.nsa.brainwave.home.adapters.ScoreActionAdapter
import com.nsa.brainwave.home.adapters.ScoresTextAdapter
import com.nsa.brainwave.home.models.score.ScoreActionModel
import com.nsa.brainwave.home.models.score.ScoreTextModel
import com.nsa.brainwave.home.viewmodels.TestViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast
import com.nsa.brainwave.util.Util.smoothProgress

class FragmentScoreCard : Fragment() {


    private lateinit var binding: FragmentScoreCardBinding
    private val auth= Firebase.auth
    private val args:FragmentScoreCardArgs by navArgs()


    private val scoresList= arrayListOf<ScoreTextModel>()
    private val scoreActionsList= arrayListOf(
        ScoreActionModel(R.drawable.review_svg,"Review Answer",R.color.blue1),
        ScoreActionModel(R.drawable.feedback_svg,"Feedback",R.color.green1),
        ScoreActionModel(R.drawable.share_svg,"Share",R.color.yellow_dark),
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_score_card, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoresList.clear()
        scoresList.addAll(listOf(
            ScoreTextModel("${Util.getTimeAsTest(args.testReport.reportModel!!.timeTaken!!)}","Time",R.color.black),
            ScoreTextModel("${args.testReport.reportModel!!.totalQuestion}","Total Question",R.color.black),
            ScoreTextModel("${args.testReport.reportModel!!.correct}","Correct",R.color.green),
            ScoreTextModel("${args.testReport.reportModel!!.wrong}","Wrong",R.color.red)
        ))

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val progress=Util.calculateProgress(
            totalAttended = args.testReport.reportModel!!.totalQuestion!!.toDouble(),
            correctSolved = args.testReport.reportModel!!.correct!!.toDouble(),
            missed = args.testReport.reportModel!!.missed!!.toDouble()
        ).toInt()

        binding.progressBar.smoothProgress(if(progress==0) 1 else progress)
        binding.scoreTv.text="$progress%"
        binding.subjectNameTv.text=args.testReport.test!!.subject

        binding.dataRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding.dataRecyclerView.adapter=ScoresTextAdapter(scoresList,object :ScoresTextAdapter.ClickCallBack{
            override fun onClicked(position: Int) {

            }
        })

        binding.actionsRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)

        binding.actionsRecyclerView.adapter=ScoreActionAdapter(scoreActionsList,object :ScoreActionAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                when(position){
                    0->{
                        findNavController().navigate(
                            FragmentScoreCardDirections.actionFragmentScoreCardToFragmentTest(false,args.testReport.test!!)
                        )
                    }
                    1->{

                    }
                    else->{

                    }

                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}