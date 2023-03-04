package com.nsa.brainwave.home.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.models.SubjectModel
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.PrevTestRecyclerItemBinding
import com.nsa.brainwave.home.models.Report.TestReport
import com.nsa.brainwave.util.Util
import com.nsa.brainwave.util.Util.smoothProgress

//using this adapter to display list of genres as chips layout
class PrevTestsAdapter(private val mList: ArrayList<TestReport>,
                       private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<PrevTestsAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=PrevTestRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: PrevTestRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: TestReport) {
            var  progress=Util.calculateProgress(item.reportModel!!.totalQuestion!!.toDouble(),item.reportModel.correct!!.toDouble(),item.reportModel.missed!!.toDouble()).toInt()

            binding.apply {

                dateTv.text=Util.getDate(item.reportModel!!.date!!)
                levelTv.text="Level ${item.test!!.level}"
                progressTv.text="$progress%"
                progressBar.smoothProgress(if(progress==0) 1 else progress)
              // cardView.strokeColor=this.root.resources.getIntArray(R.array.subjects).random()
                val color=this.root.resources.getIntArray(R.array.subjects).random()
                progressBar.setIndicatorColor(color)
                cardView.rippleColor=
                    ColorStateList.valueOf(color)

                root.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
            }
        }
    }
}