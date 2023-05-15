package com.app.road.v4.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.activity.format
import com.app.road.adapter.ReportAdapter
import com.app.road.domain.Course
import com.app.road.log
import com.app.road.model.Author
import com.app.road.model.Report
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun Double.format(digits: Int) = "%.${digits}f".format(this)

class CourseMoneyInHeadActivity : AppCompatActivity() {
    val adapter = ReportAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_money_in_head)
        val list = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        val tvBuyFinanceCourse = findViewById<View>(R.id.btnPayCourse)
        val tvBuyFinanceCourse2 = findViewById<View>(R.id.payCourse)
        findViewById<View>(R.id.autor).setOnClickListener {
            val intent = Intent(this, EkaterinaProfileActivity::class.java)
            startActivity(intent)
        }

        tvBuyFinanceCourse.setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", Course.MONEY_IN_THE_HEAD.name)
            startActivity(intent)
        }
        tvBuyFinanceCourse2.setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", Course.MONEY_IN_THE_HEAD.name)
            startActivity(intent)
        }
        val more = findViewById<View>(R.id.more)
        more.setOnClickListener {
            startActivity(Intent(this, ReportAllActiviy::class.java))
        }
        findViewById<View>(R.id.btnOtherCourse).setOnClickListener {
            startActivity(Intent(this, CourseBodyAndMentalActivity::class.java))
            finish()
        }

        val lenaTest = findViewById<TextView>(R.id.btnGetFreeDays)
        if(Repository.videoMode == 1){
            lenaTest.text = "Продолжить"
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
        lenaTest.setOnClickListener {
            val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
            pref.edit {
                putInt("video", 1)
                putString("current_author", "lena")
                apply()
            }
            log("lenaTest.setOnClickListener/videoMode: ${Repository.videoMode}")
            if (Repository.videoMode == 1) {
                startActivity(Intent(this, HelloActivity::class.java))
                Repository.videoMode = 1
            }
            Repository.videoMode = 1
            val uid = auth.currentUser!!.uid
            val user = hashMapOf(
                "trial_new_course" to true,
                "select_new_course" to true,
                "select_million" to false,
            )
            db.collection("users")
                .document(uid)
                .update(user as Map<String, Any>)
                .addOnCompleteListener {
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .get()
                        .addOnCompleteListener { value ->
                            if (value != null) {
                                val doc = value.result
                                var trial = doc["trial_count"] as Long? ?: 0L
                                trial += 1L
                                val usr = hashMapOf(
                                    "trial_count" to trial
                                )
                                db.collection("users")
                                    .document(uid)
                                    .update(usr as Map<String, Any>)
                                    .addOnCompleteListener {

                                        if(testWasComplete) {
                                            startActivity(Intent(this, BaseLenaActivity::class.java))
                                            finish()
                                        } else {
                                            startActivity(Intent(this, TestLenaActivity::class.java))
                                            finish()
                                        }
                                    }

                            }
                        }

                }

        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Курс деньги в голове", this::class.java.simpleName)
    }
}