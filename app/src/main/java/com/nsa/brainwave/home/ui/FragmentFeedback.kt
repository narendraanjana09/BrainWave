package com.nsa.brainwave.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.FragmentFeedbackBinding
import com.nsa.brainwave.databinding.FragmentSubjectBinding
import com.nsa.brainwave.home.HomeActivity
import com.nsa.brainwave.home.adapters.EmojiAdapter
import com.nsa.brainwave.home.adapters.PrevTestsAdapter
import com.nsa.brainwave.home.models.Report.TestReport
import com.nsa.brainwave.home.models.rating.Emoji
import com.nsa.brainwave.home.viewmodels.HomeViewModel
import com.nsa.brainwave.home.viewmodels.TestViewModel
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.showToast


class FragmentFeedback : BottomSheetDialogFragment() {


    private lateinit var binding:FragmentFeedbackBinding
    private val emojiList= listOf(
        Emoji(R.drawable.emoji_worst,"Worst"),
        Emoji(R.drawable.emoji_dislike,"Dislike"),
        Emoji(R.drawable.emoji_neutral,"Neutral"),
        Emoji(R.drawable.emoji_like,"Like"),
        Emoji(R.drawable.emoji_loved,"Loved")
    )
    private var currentSelected=-1

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ratingRecyclerView.layoutManager=GridLayoutManager(requireContext(),5)
        binding.ratingRecyclerView.adapter=EmojiAdapter(emojiList,object :EmojiAdapter.ClickCallBack{
            override fun onClicked(position: Int) {
                binding.ratingTextTv.apply {
                    text=emojiList[position].name
                    visibility=View.VISIBLE
                }
                (binding.ratingRecyclerView.adapter as EmojiAdapter).apply {
                    setSelected(position)
                    if (currentSelected!=-1){
                        notifyItemChanged(currentSelected)
                    }
                    notifyItemChanged(position)
                    currentSelected=position
                }

            }
        })
        binding.feedbackEd.addTextChangedListener {
            if(it.isNullOrEmpty()){
                binding.feedbackTextCountTv.text="0/500"
            }else{
                binding.feedbackTextCountTv.text="${it.length}/500"
            }
        }
        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                Util.showProgress(findNavController())
            }else{
                Util.hideProgress(findNavController(),R.id.fragmentFeedback)
            }

        }
        viewModel.message.observe(viewLifecycleOwner){
            showToast(requireContext(), it)
        }
        binding.submitBtn.setOnClickListener {
            if(currentSelected==-1){
                showToast(requireContext(),"Please give a rating!");
            }else{
                viewModel.submitFeedback(emojiList[currentSelected].name,binding.feedbackEd.text.toString().trim())
            }
        }
        viewModel.feedbackSubmitted.observe(viewLifecycleOwner){
            dismiss()
            Util.showMessageSheet("Feedback Submitted",
                "Thanks for giving us feedback, we are working hard to improve your experience.",requireActivity())
        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }




}