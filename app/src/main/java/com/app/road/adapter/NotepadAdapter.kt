package com.app.road.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.model.Meditation
import com.app.road.model.Notepad
import java.util.ArrayList

class NotepadAdapter: RecyclerView.Adapter<NotepadAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question = itemView.findViewById<TextView>(R.id.question)
        val answer = itemView.findViewById<EditText>(R.id.answer)
    }
    var list = ArrayList<Notepad>()
    fun setListM(l: ArrayList<Notepad>){
        list.clear()
        list.addAll(l)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotepadAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notepad, parent, false)
        return NotepadAdapter.MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        if(list == null) return 0
        return list!!.size
    }
    override fun onBindViewHolder(holder: NotepadAdapter.MyViewHolder, position: Int) {
        val notepad = list[position]
        holder.question.text = notepad.text
        holder.answer.setText(notepad.answer)
        if(notepad.mode == 0){
            holder.answer.visibility = View.GONE
            holder.question.typeface = Typeface.DEFAULT_BOLD
        } else {
            holder.answer.visibility = View.VISIBLE
            holder.question.typeface = Typeface.DEFAULT
        }
        holder.answer.addTextChangedListener {
            notepad.answer = holder.answer.text.toString()
        }
     }

}