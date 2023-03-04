package com.nsa.brainwave.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.adapters.ExamsAdapter
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ExamChipItemBinding
import com.nsa.brainwave.databinding.SubjectRecyclerItemBinding
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.smoothProgress

//using this adapter to display list of genres as chips layout
class SubjectsAdapter (private val mList: List<SubjectModel>,
                       private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<SubjectsAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=SubjectRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: SubjectRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: SubjectModel) {
            var  progress=Util.calculateProgress(item.totalAttended!!.toDouble(),item.correctSolved!!.toDouble(),item.missed!!.toDouble()).toInt()

            binding.apply {
                subjectNameTv.text=item.name
                progressTv.text="$progress %"
                progressBar.smoothProgress(if(progress==0) 1 else progress)
                cardView.setCardBackgroundColor(this.root.resources.getIntArray(R.array.subjects).random())

                root.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
            }
        }
    }
}