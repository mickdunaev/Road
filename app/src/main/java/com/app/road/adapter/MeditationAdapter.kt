package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Meditation
import java.util.*

class MeditationAdapter: RecyclerView.Adapter<MeditationAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val duration = itemView.findViewById<TextView>(R.id.duration)
        val title = itemView.findViewById<TextView>(R.id.title)
        val play = itemView.findViewById<View>(R.id.play)
        val play2 = itemView.findViewById<View>(R.id.play2)
    }
    lateinit var meditationSelect:(meditation: Meditation) -> Unit
    private var list = ArrayList<Meditation>()
    fun setList(l: ArrayList<Meditation>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_meditation, parent, false)
        return MeditationAdapter.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: MeditationAdapter.MyViewHolder, position: Int) {
        val meditation = list[position]
        holder.duration.text = meditation.duration
        holder.title.text = meditation.title
        holder.play.setOnClickListener {
            meditationSelect(meditation)
        }
        holder.play2.setOnClickListener {
            meditationSelect(meditation)
        }
    }

}