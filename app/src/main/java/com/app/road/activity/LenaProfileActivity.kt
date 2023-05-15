package com.app.road.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
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

class LenaProfileActivity : AppCompatActivity() {
    val adapter = ReportAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lena_profile)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val list = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        val tvBuyFinanceCourse = findViewById<View>(R.id.tvBuyFinanceCourse)
        val tvBuyBodyCourse = findViewById<View>(R.id.tvBuyBodyCourse)
        tvBuyFinanceCourse.setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", Course.MONEY_IN_THE_HEAD.name)
            startActivity(intent)
        }
        tvBuyBodyCourse.setOnClickListener {
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", Author.ELENA.name)
            intent.putExtra("course_name", Course.BODY_AND_MENTAL.name)
            startActivity(intent)
        }
        val more = findViewById<View>(R.id.more)
        more.setOnClickListener {
            startActivity(Intent(this, ReportAllActiviy::class.java))
        }
        val lenaTest = findViewById<TextView>(R.id.tvGetFreeFinanceDays)

        val lenaTest2 = findViewById<TextView>(R.id.tvGetFreeBodyDays)
        if(Repository.videoMode == 1){
            lenaTest.text = "Продолжить"
        }
        if(Repository.videoMode == 2){
            lenaTest2.text = "Продолжить"
        }

        val lenaVideo = findViewById<View>(R.id.lena_video)
        lenaVideo.setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", "http://mybestway.ru/video/intro/1.mp4")
            startActivity(intent)

        }
        val db = Firebase.firestore
        val auth = Firebase.auth


        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
        val testWasComplete = pref.getBoolean("test2", false)

        /*findViewById<TextView>(R.id.btnGetFreeDays).setOnClickListener {
            if (Repository.videoMode == 1) {
                startActivity(Intent(this, HelloActivity::class.java))
                log("to hello activity")
            } else {
                val uid = auth.currentUser!!.uid
                val user = hashMapOf(
                    "trial_new_course" to true,
                    "select_new_course" to true
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
                                            startActivity(Intent(this, TestLenaActivity::class.java))
                                            finish()
                                        }

                                }
                            }

                    }
            }
        }*/

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

        lenaTest2.setOnClickListener {
            val sharedPreferences = getSharedPreferences("road", Context.MODE_PRIVATE)
            sharedPreferences.edit {
                putString("current_author", "lena")
                apply()
            }
            if (Repository.videoMode == 2) {
                startActivity(Intent(this, HelloActivity::class.java))
            }

            val uid = auth.currentUser!!.uid
            val user = hashMapOf(
                "trial_new_course" to true,
                "select_new_course" to true
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
                                        Repository.videoMode = 2
                                        val pref =
                                            getSharedPreferences("road", Context.MODE_PRIVATE)
                                        pref.edit {
                                            putInt("video", 2)
                                            apply()

                                        }

                                        startActivity(Intent(this, HelloActivity::class.java))
                                        finish()
                                    }

                            }
                        }

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
                "select_new_course" to true
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
        Utils.setScreenOpenAnalytics("Профиль Екатерины", this::class.java.simpleName)
    }
}