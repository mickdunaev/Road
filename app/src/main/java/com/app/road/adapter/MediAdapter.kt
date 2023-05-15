package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Meditation
import java.util.*

class MediAdapter: RecyclerView.Adapter<MediAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val duration = itemView.findViewById<TextView>(R.id.duration)
        val title = itemView.findViewById<TextView>(R.id.title)
        val play = itemView.findViewById<View>(R.id.play)
        val pay = itemView.findViewById<CheckBox>(R.id.pay)
        val price = itemView.findViewById<TextView>(R.id.textView100)
    }
    lateinit var meditationSelect:(meditation: Meditation) -> Unit
    lateinit var paySelect:(medis: ArrayList<Meditation>) -> Unit
    private var list = ArrayList<Meditation>()
    fun setList(l: ArrayList<Meditation>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_medi, parent, false)
        return MediAdapter.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: MediAdapter.MyViewHolder, position: Int) {
        val meditation = list[position]
        holder.duration.text = meditation.duration
        holder.title.text = meditation.title
        holder.play.setOnClickListener {
            meditationSelect(meditation)
        }
        if(meditation.isKuplena){
            holder.pay.visibility = View.INVISIBLE
            holder.price.visibility = View.INVISIBLE
        } else {
            holder.pay.visibility = View.VISIBLE
            holder.price.visibility = View.VISIBLE
        }
        if(list[position].pay){
            holder.pay.isChecked = true
        } else {
            holder.pay.isChecked = false
        }
        holder.pay.setOnClickListener {
            if(holder.pay.isChecked) {
                list[position].pay = true
                paySelect(list)
            } else {
                list[position].pay = false
                paySelect(list)
            }
        }
    }

}