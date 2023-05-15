package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val textRating = findViewById<TextView>(R.id.text_rating)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)

        ratingBar.rating = 3.5f

        val send = findViewById<Button>(R.id.send)
        val report = findViewById<EditText>(R.id.report)
        send.setOnClickListener {
            val text = report.text.toString()
            val value = ratingBar.rating
            if(text.isEmpty()){
                Toast.makeText(this, "Напишите отзыв", Toast.LENGTH_SHORT).show()
            } else {
                val db = Firebase.firestore
                val auth = Firebase.auth
                val calendar = Calendar.getInstance()
                val currYear = calendar.get(Calendar.YEAR)
                val currMonth = calendar.get(Calendar.MONTH) + 1
                val currDay = calendar.get(Calendar.DAY_OF_MONTH)
                val currHour = calendar.get(Calendar.HOUR)
                val currMinute = calendar.get(Calendar.MINUTE)
                val timestamp = calendar.timeInMillis
                val username = Repository.name
                val useremail = Repository.email

                Log.d("Mikhael", "send report")
                val rep = hashMapOf(
                    "user_id" to auth.uid,
                    "username" to username,
                    "useremail" to useremail,
                    "text" to text,
                    "rating" to value,
                    "year" to currYear,
                    "month" to currMonth,
                    "day" to currDay,
                    "hour" to currHour,
                    "minute" to currMinute,
                    "timestamp" to timestamp
                )
                db.collection("reports").document().set(rep).addOnSuccessListener {
                    Toast.makeText(this,"Отзыв успешно отправлен", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,"Не удалось отправить отзыв", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Написание отзыва", this::class.java.simpleName)
    }
}