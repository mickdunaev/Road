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
import com.app.road.model.Task
import java.util.*


class TaskAdapter: RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    var mselectYear: Int? = null
    var mselectMonth: Int? = null
    var mselectDay: Int? = null
    var noteText: String = ""

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val calendar = itemView.findViewById<CalendarView>(R.id.calendar)
        val task = itemView.findViewById<View>(R.id.task)
        val header = itemView.findViewById<TextView>(R.id.header)
        val text = itemView.findViewById<TextView>(R.id.text)
        val fromDate = itemView.findViewById<TextView>(R.id.fromDate)
        val toDate = itemView.findViewById<TextView>(R.id.toDate)
        val taskGo = itemView.findViewById<View>(R.id.task_go)
        val calendarBlock= itemView.findViewById<View>(R.id.calendar_block)
        val noteUi = itemView.findViewById<EditText>(R.id.note_text)
    }
    private var list = ArrayList<Task>()
    fun setList(l: ArrayList<Task>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    fun setNote(note: String){
        noteText = note
        notifyDataSetChanged()
    }
    lateinit var calandarSelect:(year: Int, month: Int, day: Int) -> Unit
    lateinit var taskSelect:(task: Task) -> Unit
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size + 1
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(position == list.size){
            if(mselectDay != null && mselectMonth != null && mselectYear != null){
                val calendar = Calendar.getInstance()
                calendar.set(
                    mselectYear!!,
                    mselectMonth!! - 1,
                    mselectDay!!
                )
                holder.calendar.date = calendar.timeInMillis
            }
            holder.calendarBlock.visibility = View.VISIBLE
            holder.calendar.visibility = View.VISIBLE
            holder.task.visibility = View.GONE
            holder.calendar.setOnDateChangeListener { calendarView, year, month, day ->
                calandarSelect(year, month, day)
                noteText = ""
                holder.noteUi.setText("")
            }
            holder.noteUi.setText(noteText)
            holder.noteUi.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    // you can call or do what you want with your EditText here
                    // yourEditText...
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    Log.d("Mikhael","t")
                    noteText = s.toString()
                }
            })

        } else {
            holder.calendarBlock.visibility = View.GONE
            holder.calendar.visibility = View.GONE
            holder.task.visibility = View.VISIBLE
            holder.header.text = list[position].header
            holder.text.text = list[position].text
            holder.fromDate.text = list[position].fromDate
            holder.toDate.text = list[position].toDate

            holder.taskGo.setOnClickListener {
                taskSelect(list[position])
            }
        }
    }
}