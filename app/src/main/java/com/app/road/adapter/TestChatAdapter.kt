package com.app.road.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.TestMessage

class TestChatAdapter: RecyclerView.Adapter<TestChatAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.findViewById<ImageView>(R.id.icon)
        val message = itemView.findViewById<TextView>(R.id.message)
        val answer = itemView.findViewById<TextView>(R.id.answer)
    }

    private var list = ArrayList<TestMessage>()

    fun setList(l: ArrayList<TestMessage>){
        list.clear()
        list.addAll(l)
        list.reverse()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.test_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(list != null && list!!.size != 0){
            val message = list!![position]
            when(message.style){
                0 -> {
                    holder.icon.visibility = View.VISIBLE
                    holder.message.visibility = View.VISIBLE
                    holder.answer.visibility = View.GONE
                    holder.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
                    holder.message.text = message.text
                }
                1 -> {
                    holder.icon.visibility = View.VISIBLE
                    holder.message.visibility = View.VISIBLE
                    holder.answer.visibility = View.GONE
                    holder.message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f)
                    holder.message.text = message.text
                }
                2 -> {
                    holder.icon.visibility = View.GONE
                    holder.message.visibility = View.GONE
                    holder.answer.visibility = View.VISIBLE
                    holder.answer.text = message.text
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }

}