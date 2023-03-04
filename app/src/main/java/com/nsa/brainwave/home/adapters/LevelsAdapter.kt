package com.nsa.brainwave.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ExamChipItemBinding
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.home.models.Level

//using this adapter to display list of genres as chips layout
class LevelsAdapter (private val mList: List<Level>,
                     private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<LevelsAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ExamChipItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: ExamChipItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Level) {
            binding.apply {
               chip.text=item.level
                if(item.selected!=null && item.selected!!){
                    chip.setChipBackgroundColorResource( R.color.green)
                    chip.setTextColor(ContextCompat.getColor(chip.context, R.color.white))
                }else{
                    chip.setChipBackgroundColorResource( R.color.green_light)
                    chip.setTextColor(ContextCompat.getColor(chip.context, R.color.black))
                }
                chip.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
            }
        }
    }
}