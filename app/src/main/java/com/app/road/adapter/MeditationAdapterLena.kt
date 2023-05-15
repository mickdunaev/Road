package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.log
import com.app.road.model.Meditation
import java.util.*

class MeditationAdapterLena: RecyclerView.Adapter<MeditationAdapterLena.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val duration = itemView.findViewById<TextView>(R.id.duration)
        val title = itemView.findViewById<TextView>(R.id.title)
        val play = itemView.findViewById<View>(R.id.play)
        val play2 = itemView.findViewById<View>(R.id.play2)
        val work = itemView.findViewById<Button>(R.id.work)
    }
    lateinit var meditationSelect:(meditation: Meditation) -> Unit
    lateinit var notepadSelect:() -> Unit
    private var list = ArrayList<Meditation>()
    fun setList(l: ArrayList<Meditation>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationAdapterLena.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_meditation_lena, parent, false)
        return MeditationAdapterLena.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list.size + 1
    }
    override fun onBindViewHolder(holder: MeditationAdapterLena.MyViewHolder, position: Int) {
        log("position: $position")
        if(position == (list.size)) {
            holder.work.visibility = View.VISIBLE
            holder.duration.visibility = View.GONE
            holder.title.visibility = View.GONE
            holder.play.visibility = View.GONE
            holder.play.setOnClickListener {
            }
            holder.play2.setOnClickListener {
            }
            holder.work.setOnClickListener {
                notepadSelect()
            }
            if(position != 0 && list[position-1].isAboutCourseButton) {
                holder.work.text = "Описание курса"
            } else holder.work.text = "+ выполнить задание"
        } else {
            holder.work.visibility = View.GONE
            holder.duration.visibility = View.VISIBLE
            holder.title.visibility = View.VISIBLE
            holder.play.visibility = View.VISIBLE

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

}