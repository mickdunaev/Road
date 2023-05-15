package com.app.road.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Diary
import java.util.*

class DiaryAdapter: RecyclerView.Adapter<DiaryAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val header = itemView.findViewById<TextView>(R.id.header)
        val text = itemView.findViewById<TextView>(R.id.text)
        val date = itemView.findViewById<TextView>(R.id.date)
        val time = itemView.findViewById<TextView>(R.id.time)
    }
    private var list = ArrayList<Diary>()
    fun setList(l: ArrayList<Diary>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_diary, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val diary = list[position]
        holder.header.text = diary.header
        holder.text.text = diary.text
        holder.time.text = formatTime(diary.hour, diary.minute)
        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR).toLong()
        val currMonth = (calendar.get(Calendar.MONTH) + 1).toLong()
        val currDay = calendar.get(Calendar.DAY_OF_MONTH).toLong()
        if(currYear == diary.year && currMonth == diary.month && currDay == diary.day){
            holder.date.text = "Сегодня"
        } else if(currYear == diary.year && currMonth == diary.month && currDay == (diary.day + 1)){
            holder.date.text = "Вчера"
        } else {
            holder.date.text = formatDate(diary.year, diary.month, diary.day)
        }
    }
    private fun formatTime(hour: Long, minute: Long): String {
        return formatNum(hour) + ":" + formatNum(minute)
    }
    private fun formatDate(year: Long, month: Long, day: Long): String{
        return formatNum(day) + "." + formatNum(month) + "." + year.toString()
    }
    private fun formatNum(d:Long): String {
        var ret = d.toString()
        if(d<10) ret = "0" + d.toString()
        return ret
    }
}