package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Meditation
import com.app.road.model.Notify
import java.util.*

class NotifyAdapter: RecyclerView.Adapter<NotifyAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message = itemView.findViewById<TextView>(R.id.message)
        val time = itemView.findViewById<TextView>(R.id.time)
        val container = itemView.findViewById<View>(R.id.container)
    }
    lateinit var notifySelect:(notify: Notify) -> Unit
    lateinit var containerSelect:(notify: Notify) -> Unit
    private var list = ArrayList<Notify>()
    fun setList(l: ArrayList<Notify>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notify, parent, false)
        return NotifyAdapter.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: NotifyAdapter.MyViewHolder, position: Int) {
        val notify = list[position]
        holder.message.text = notify.message
        holder.time.text = notify.time
        holder.container.setOnClickListener {
            containerSelect(notify)
        }
     }

}