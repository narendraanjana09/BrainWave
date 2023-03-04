package com.nsa.brainwave.home.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nsa.brainwave.R
import com.nsa.brainwave.databinding.OptionRecyclerItemBinding

//using this adapter to display list of genres as chips layout
class OptionsAdapter(
    private val isTest: Boolean,
    private val mList: List<String>,
    private val clickCallback: ClickCallBack
) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClicked(position: Int)
    }
    private var currentSelected=-1
    private var correctOption=-1
    fun setOptionSelected(index:Int){
        this.currentSelected=index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=OptionRecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun correctOption(correctOption: Int) {
        this.correctOption=correctOption
    }

    inner class ViewHolder(private val binding: OptionRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {


        fun setData(item: String) {



            binding.apply {
              optionTv.text=item
                if(isTest){
                    if(currentSelected==adapterPosition){
                        val color=ContextCompat.getColor(root.context,R.color.blue1)
                        optionTv.setTextColor(color)
                        radioBtn.isChecked=true
                        cardView.strokeColor=color
                        radioBtn.buttonTintList=ColorStateList.valueOf(color)
                    }else{
                        val color=ContextCompat.getColor(root.context,R.color.black_light)
                        optionTv.setTextColor(color)
                        radioBtn.isChecked=false
                        cardView.strokeColor=color
                        radioBtn.buttonTintList=ColorStateList.valueOf(color)
                    }
                    root.setOnClickListener {
                        if(currentSelected!=adapterPosition){
                            clickCallback.onClicked(adapterPosition)
                        }
                    }
                }else{
                    var color: Int? =null
                    var isChecked=false
                    if(adapterPosition==currentSelected){
                        isChecked=true
                        if(currentSelected==correctOption){
                            color=ContextCompat.getColor(root.context,R.color.green)
                        }else{
                        color=ContextCompat.getColor(root.context,R.color.red_light)
                        }
                    }else if(adapterPosition==correctOption){
                        isChecked=true
                        color=ContextCompat.getColor(root.context,R.color.green)
                    }else{
                        color=ContextCompat.getColor(root.context,R.color.black_light)
                    }

                    optionTv.setTextColor(color)
                    radioBtn.isChecked=isChecked
                    cardView.strokeColor=color
                    radioBtn.buttonTintList=ColorStateList.valueOf(color)
                }

            }
        }
    }
}