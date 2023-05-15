package com.app.road.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Report
import com.app.road.model.Task
import java.util.*


class ReportVAdapter: RecyclerView.Adapter<ReportVAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val rating = itemView.findViewById<TextView>(R.id.rating_our)
        val date = itemView.findViewById<TextView>(R.id.date)
        val text = itemView.findViewById<TextView>(R.id.text)
    }
    private var list = ArrayList<Report>()
    fun setList(l: ArrayList<Report>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rating_v, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val report = list[position]
        holder.date.text = report.date
        holder.name.text = report.name
        holder.rating.text = report.rating
        holder.text.text = report.text
    }
}