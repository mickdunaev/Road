package com.app.road.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.adapter.ReportAdapter
import com.app.road.domain.Course
import com.app.road.model.Author
import com.app.road.model.Report
import com.app.road.v4.Utils
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class VasiliyProfileActivity : AppCompatActivity() {

    val adapter = ReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vasiliy_profile)

        val db = Firebase.firestore
        val auth = Firebase.auth

        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val list = findViewById<RecyclerView>(R.id.list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter

//        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
//        val test = pref.getBoolean("test", false)

//        val btnGetFreeDays = findViewById<TextView>(R.id.btnGetFreeDays)
//        val btnBuyCourse = findViewById<ConstraintLayout>(R.id.vasiliy_course)
//        val tvBuyCourse = findViewById<TextView>(R.id.tvBuyCourse)
//        tvBuyCourse.setOnClickListener {
//            val intent = Intent(this, SubscribeBaseActivity::class.java)
//            intent.putExtra("author", Author.VASILIY.name)
//            intent.putExtra("course_name", Course.MONEY_ENERGY.name)
//            startActivity(intent)
//        }

        val more = findViewById<View>(R.id.more)
        more.setOnClickListener {
            startActivity(Intent(this, ReportAllActiviy::class.java))
        }

//        btnBuyCourse.setOnClickListener {
//            val intent = Intent(this, SubscribeBaseActivity::class.java)
//            intent.putExtra("author", Author.VASILIY.name)
//            intent.putExtra("course_name", Course.MONEY_ENERGY.name)
//            startActivity(intent)
//        }
//
//        btnGetFreeDays.setOnClickListener {
//            val sharedPreferences = getSharedPreferences("road", Context.MODE_PRIVATE)
//            sharedPreferences.edit {
//                putString("current_author", "vas")
//                apply()
//            }
//            val db = Firebase.firestore
//            val auth = Firebase.auth
//            val uid = auth.currentUser!!.uid
//            val user = hashMapOf(
//                "select_new_course" to false
//            )
//            Repository.selectLena = false
//            db.collection("users")
//                .document(uid)
//                .update(user as Map<String, Any>)
//                .addOnCompleteListener {
//                    if(!test){
//                        startActivity(Intent(this, TestActivity::class.java))
//                        finish()
//                    } else {
//                        startActivity(Intent(this, BaseActivity::class.java))
//                        finish()
//                    }
//
//                }
//        }
//
        db.collection("vasiliy_reports")
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

        /*val rep = hashMapOf(
            "user_id" to auth.uid,
            "username" to "",
            "useremail" to "",
            "text" to "",
            "rating" to 4.9,
            "year" to 2022,
            "month" to 2,
            "day" to 2,
            "hour" to 2,
            "minute" to 2,
            "timestamp" to Timestamp(Date())
        )
        db.collection("vasiliy_reports").document().set(rep)*/
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Профиль Василия", this::class.java.simpleName)
    }
}