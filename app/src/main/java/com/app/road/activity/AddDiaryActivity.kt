package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.app.road.R
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddDiaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val play = findViewById<View>(R.id.play)
        play.setOnClickListener {
            Toast.makeText(this, "Воспроизвести аудио", Toast.LENGTH_SHORT).show()
        }
        val ok = findViewById<View>(R.id.ok)
        val headerDiary = findViewById<EditText>(R.id.header)
        val textDiary = findViewById<EditText>(R.id.text)
        ok.setOnClickListener {
            val header = headerDiary.text.toString()
            val text = textDiary.text.toString()
            if (header.isEmpty()) {
                Toast.makeText(this, "Укажите заголовок", Toast.LENGTH_SHORT).show()
            } else if (text.isEmpty()) {
                Toast.makeText(this, "Укажите текст", Toast.LENGTH_SHORT).show()
            } else {
                val calendar = Calendar.getInstance()
                val currYear = calendar.get(Calendar.YEAR)
                val currMonth = calendar.get(Calendar.MONTH) + 1
                val currDay = calendar.get(Calendar.DAY_OF_MONTH)
                val currHour = calendar.get(Calendar.HOUR)
                val currMinute = calendar.get(Calendar.MINUTE)
                val timestamp = calendar.timeInMillis
                val auth = Firebase.auth
                val db = Firebase.firestore
                val diary = hashMapOf(
                    "user_id" to auth.uid,
                    "header" to header,
                    "text" to text,
                    "year" to currYear,
                    "month" to currMonth,
                    "day" to currDay,
                    "hour" to currHour,
                    "minute" to currMinute,
                    "timestamp" to timestamp
                )
                db.collection("diary").document().set(diary).addOnSuccessListener {
                    Toast.makeText(this,"Запись успешно создана", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,"Не удалось создать запись", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Добавить запись в дневник", this::class.java.simpleName)
    }
}