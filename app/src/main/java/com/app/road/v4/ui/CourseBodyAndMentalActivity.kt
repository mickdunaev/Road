package com.app.road.v4.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
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

class CourseBodyAndMentalActivity : AppCompatActivity() {
    val adapter = ReportAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_body_and_mental)

        val list = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
        val tvBuyFinanceCourse = findViewById<View>(R.id.btnPayCourse)
        val tvBuyFinanceCourse2 = findViewById<View>(R.id.payCourse)
        val accept = findViewById<CheckBox>(R.id.accept)
        findViewById<View>(R.id.btnOtherCourse).setOnClickListener {
            startActivity(Intent(this, CourseMoneyInHeadActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.autor).setOnClickListener {
            val intent = Intent(this, EkaterinaProfileActivity::class.java)
            startActivity(intent)
        }

        tvBuyFinanceCourse.setOnClickListener {
            if(accept.isChecked){
                val intent = Intent(this, SubscribeBaseActivity::class.java)
                intent.putExtra("author", Author.ELENA.name)
                intent.putExtra("course_name", Course.BODY_AND_MENTAL.name)
                startActivity(intent)
            } else {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Предупрежнение")
                alert.setMessage("Вы должны согласиться с предупреждением")
                alert.setPositiveButton("OK", null)
                alert.show()
            }

        }
        tvBuyFinanceCourse2.setOnClickListener {
            if(accept.isChecked){
                val intent = Intent(this, SubscribeBaseActivity::class.java)
                intent.putExtra("author", Author.ELENA.name)
                intent.putExtra("course_name", Course.BODY_AND_MENTAL.name)
                startActivity(intent)
            } else {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Предупрежнение")
                alert.setMessage("Вы должны согласиться с предупреждением")
                alert.setPositiveButton("OK", null)
                alert.show()
            }
        }
        val more = findViewById<View>(R.id.more)
        more.setOnClickListener {
            startActivity(Intent(this, ReportAllActiviy::class.java))
        }
        val lenaTest2 = findViewById<TextView>(R.id.btnGetFreeDays)
        if(Repository.videoMode == 2){
            lenaTest2.text = "Продолжить"
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
        lenaTest2.setOnClickListener {
            if(accept.isChecked){
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
                    "select_new_course" to true,
                    "select_million" to false
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

            } else {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Предупрежнение")
                alert.setMessage("Вы должны согласиться с предупреждением")
                alert.setPositiveButton("OK", null)
                alert.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Курс тело и психика", this::class.java.simpleName)
    }
}