package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick

class DicSearchAdapter (var data:List<Dic>,var callback:OnCallBack,var context: Context): RecyclerView.Adapter<DicSearchAdapter.ViewHolder>(){
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view=LayoutInflater.from(context).inflate(R.layout.item_popwindow,parent,false);
        return ViewHolder(view,callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item=data.get(position)
        holder.bind(item)
    }


    class ViewHolder(private var view: View, private var callback: OnCallBack):RecyclerView.ViewHolder(view){
        var textview:TextView
        init {
            textview=view.findViewById<TextView>(R.id.tv)
        }

        fun bind(dic: Dic?) {
            textview.text=dic!!.name
            textview.onClick { callback.onResult(dic!!.name)}
        }
    }

}


private class DicDiffCallback : DiffUtil.ItemCallback<Dic>() {

    override fun areItemsTheSame(oldItem: Dic, newItem: Dic): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Dic, newItem: Dic): Boolean {
        return oldItem == newItem
    }
}

public interface OnCallBack{
    fun onResult(name:String)
}
