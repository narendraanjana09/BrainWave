package com.nsa.brainwave.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.Login.models.exams.Exam
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.ExamChipItemBinding
import com.nsa.brainwave.databinding.RatingRecyclerItemBinding
import com.nsa.brainwave.home.adapters.SubjectsAdapter
import com.nsa.brainwave.home.models.Level
import com.nsa.brainwave.home.models.rating.Emoji

//using this adapter to display list of genres as chips layout
class EmojiAdapter (private val mList: List<Emoji>,
                    private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }
    private var currentSelected=-1
    fun setSelected(index:Int){
        this.currentSelected=index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=RatingRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: RatingRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Emoji) {
            binding.apply {
               imageView.setImageResource(item.emoji)
                if (currentSelected==adapterPosition){
                    imageView.setBackgroundResource(R.drawable.emoji_selected_bg)
                }else{
                    imageView.setBackgroundResource(R.drawable.emoji_unselected_bg)
                }
                imageView.setOnClickListener {
                    clickCallback.onClicked(adapterPosition)
                }
            }
        }
    }
}