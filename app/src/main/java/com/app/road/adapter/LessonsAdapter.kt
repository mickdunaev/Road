package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Meditation
import com.app.road.model.Lesson
import java.util.*

class LessonsAdapter: RecyclerView.Adapter<LessonsAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val content = itemView.findViewById<TextView>(R.id.content)
        val date = itemView.findViewById<TextView>(R.id.date)
        val container = itemView.findViewById<View>(R.id.container)
    }
    lateinit var containerSelect:(Lesson: Lesson) -> Unit
    private var list = ArrayList<Lesson>()
    fun setList(l: ArrayList<Lesson>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonsAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lessons, parent, false)
        return LessonsAdapter.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: LessonsAdapter.MyViewHolder, position: Int) {
        val Lesson = list[position]
        holder.name.text = Lesson.name
        holder.content.text = Lesson.course
        holder.date.text = Lesson.date
        holder.container.setOnClickListener {
            containerSelect(Lesson)
        }
     }

}