package com.nsa.brainwave.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ExamChipItemBinding
import com.nsa.brainwave.databinding.ScoreDataRecyclerItemBinding
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.home.models.Level
import com.nsa.brainwave.home.models.score.ScoreTextModel

//using this adapter to display list of genres as chips layout
class ScoreCardActionAdapter (private val mList: List<ScoreTextModel>,
                              private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<ScoreCardActionAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ScoreDataRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: ScoreDataRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: ScoreTextModel) {
            binding.apply {
               dataTv.text="• ${item.text}"
                titleTv.text=item.title
                dataTv.setTextColor(ContextCompat.getColor(root.context,item.color))
            }
        }
    }
}