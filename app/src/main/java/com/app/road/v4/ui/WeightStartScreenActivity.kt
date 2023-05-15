package com.app.road.v4.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.activity.ReportAllActiviy
import com.app.road.activity.SubscribeBaseActivity
import com.app.road.activity.format
import com.app.road.adapter.ReportAdapter
import com.app.road.domain.Course
import com.app.road.model.Author
import com.app.road.model.Report
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeightStartScreenActivity : AppCompatActivity() {
    val adapter = ReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_start_screen)
        val list = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        val more = findViewById<View>(R.id.more)
        val tv90 = findViewById<TextView>(R.id.textView90)
        val tv91 = findViewById<TextView>(R.id.textView91)
        tv90.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        tv91.apply {
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        more.setOnClickListener {
            startActivity(Intent(this, ReportAllActiviy::class.java))
        }
        findViewById<View>(R.id.btnOtherCourse).setOnClickListener {
            startActivity(Intent(this, CourseBodyAndMentalActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.autor).setOnClickListener {
            val intent = Intent(this, EkaterinaProfileActivity::class.java)
            startActivity(intent)
        }
        findViewById<View>(R.id.pay).setOnClickListener {
            //val intent = Intent(this, WeightVideoActivity::class.java)
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", "Вес")

            startActivity(intent)
        }
        findViewById<View>(R.id.btnPayCourse).setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", "Вес")

            startActivity(intent)

        }
        val db = Firebase.firestore
        val auth = Firebase.auth
        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
        val testWasComplete = pref.getBoolean("test2", false)
        db.collection("reports")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get().addOnCompleteListener {
                val docs = it.result
                if (docs != null) {
                    val ll = ArrayList<Report>()
                    for (doc in docs) {
                        val name = doc["username"] as String? ?: ""
                        val rating = doc["rating"] as Double? ?: 0.0
                        val year = doc["year"] as Long? ?: 2022L
                        val month = doc["month"] as Long? ?: 6L
                        val day = doc["day"] as Long? ?: 16L
                        val text = doc["text"] as String? ?: ""
                        val date = "$day.$month.$year"
                        val report = Report(name, rating.format(1), date, text)
                        ll.add(report)
                    }
                    adapter.setList(ll)
                }
            }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Курс похудения", this::class.java.simpleName)
    }
}