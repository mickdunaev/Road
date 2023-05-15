package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.app.road.R
import com.app.road.v4.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class TaskCompliteActivity : AppCompatActivity() {
    private lateinit var header: String
    private lateinit var text: String
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_complite)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val header_ui = findViewById<TextView>(R.id.header_task)
        val text_ui = findViewById<TextView>(R.id.task_text)
        val fromDate_ui = findViewById<TextView>(R.id.fromDate)
        val toDate_ui = findViewById<TextView>(R.id.toDate)
        val closeTask = findViewById<View>(R.id.close_task)



        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR)
        val currMonth = calendar.get(Calendar.MONTH) + 1
        val currDay = calendar.get(Calendar.DAY_OF_MONTH)

        header = intent.getStringExtra("header") ?: ""
        text = intent.getStringExtra("text") ?: ""
        fromDate = intent.getStringExtra("fromDate") ?: ""
        toDate = intent.getStringExtra("toDate") ?: ""
        id = intent.getStringExtra("id") ?: ""
        val delta = calcDelta(currYear, currMonth, currDay)
        Log.d("Mikhael", delta)

        header_ui.text = header
        text_ui.text = text
        fromDate_ui.text = fromDate
        toDate_ui.text = toDate

        val taskMes = findViewById<TextView>(R.id.task_mes)
        val delta_ui = findViewById<TextView>(R.id.delta)
        if(delta.isEmpty()){
            taskMes.text = "Время на выполнение задачи закончилось"
            delta_ui.visibility = View.GONE
        } else {
            taskMes.text = "На выполнение поставленной задачи осталось:"
            delta_ui.text = delta + genText(delta.toInt())
        }
        val ok = findViewById<CheckBox>(R.id.ok)
        closeTask.setOnClickListener {
            if(ok.isChecked){
                val db = Firebase.firestore
                val task = hashMapOf(
                    "is_complited" to true
                )
                db.collection("tasks").document(id).update(task as Map<String, Any>)
            }
            finish()
        }
    }

    private fun genText(d: Int): String{
        return when(d){
            1 -> " день"
            2 -> " дня"
            3 -> " дня"
            4 -> " дня"
            else -> " дней"
        }
    }

    private fun calcDelta(y: Int, m: Int, d: Int): String{
        var toYear = 0
        var toMonth = 0
        var toDay = 0
        val to = toDate.split(".")
        toDay = to[0].toInt()
        toMonth = to[1].toInt()
        toYear = to[2].toInt()
        val fr = y*365 + m*30 + d
        val t = toYear*365 + toMonth*30 + toDay
        var d = t - fr
        if(d == 0) d = 1
        if(d < 0) return ""
        return d.toString()
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Задача", this::class.java.simpleName)
    }
}