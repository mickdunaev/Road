package com.app.road.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.app.road.R
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        var fromYear = intent.getIntExtra("year", 1970)
        var fromMonth = intent.getIntExtra("month", 1)
        var fromDay = intent.getIntExtra("day", 1)
        var toYear = fromYear
        var toMonth = fromMonth
        var toDay = fromDay

        val fromDate = findViewById<TextView>(R.id.fromDate)
        val toDate = findViewById<TextView>(R.id.toDate)

        fromDate.text = formatDate(fromYear, fromMonth, fromDay)
        toDate.text = formatDate(toYear, toMonth, toDay)

        fromDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                fromYear = year
                fromMonth = monthOfYear + 1
                fromDay = dayOfMonth
                fromDate.text = formatDate(fromYear, fromMonth, fromDay)
            }, fromYear, fromMonth - 1, fromDay)
            dpd.show()
        }

        toDate.setOnClickListener {
            val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                toYear = year
                toMonth = monthOfYear + 1
                toDay = dayOfMonth
                toDate.text = formatDate(toYear, toMonth, toDay)
            }, toYear, toMonth - 1, toDay)
            dpd.show()
        }
        val taskHeared = findViewById<TextView>(R.id.header_task)
        val taskText = findViewById<TextView>(R.id.task_text)
        val addTask = findViewById<View>(R.id.add_task)
        addTask.setOnClickListener {
            val header = taskHeared.text.toString()
            val text = taskText.text.toString()
            if(header.isEmpty()){
                Toast.makeText(this,"Укажите заголовок задачи", Toast.LENGTH_SHORT).show()
            } else if(text.isEmpty()){
                Toast.makeText(this,"Укажите текст задачи", Toast.LENGTH_SHORT).show()
            } else {
                val auth = Firebase.auth
                val db = Firebase.firestore
                val task = hashMapOf(
                    "user_id" to auth.uid,
                    "is_complited" to false,
                    "header" to header,
                    "text" to text,
                    "from_year" to fromYear,
                    "from_month" to fromMonth,
                    "from_day" to fromDay,
                    "to_year" to toYear,
                    "to_month" to toMonth,
                    "to_day" to toDay
                )
                db.collection("tasks").document().set(task).addOnSuccessListener {
                    Toast.makeText(this,"Задача успешно создана", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,"Не удалось создать задачу", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun formatNum(num: Int): String {
        var ret = ""
        if(num < 10) ret = "0" + num.toString()
        else ret = num.toString()
        return ret
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val y = year.toString()
        val m = formatNum(month)
        val d = formatNum(day)
        return d + "." + m + "." + y
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Добавить задачу", this::class.java.simpleName)
    }
}