package com.nsa.brainwave.home.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ExamChipItemBinding
import com.nsa.brainwave.databinding.ScoreCardActionItemBinding
import com.nsa.brainwave.databinding.ScoreDataRecyclerItemBinding
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.home.models.Level
import com.nsa.brainwave.home.models.score.ScoreActionModel
import com.nsa.brainwave.home.models.score.ScoreTextModel

//using this adapter to display list of genres as chips layout
class ScoreActionAdapter (private val mList: List<ScoreActionModel>,
                          private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<ScoreActionAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ScoreCardActionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: ScoreCardActionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: ScoreActionModel) {
            binding.apply {
                fabBtn.setImageDrawable(ContextCompat.getDrawable(root.context,item.drawable))
                titleTv.text=item.title
                fabBtn.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(root.context,item.color))
                root.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
                fabBtn.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
            }
        }
    }
}