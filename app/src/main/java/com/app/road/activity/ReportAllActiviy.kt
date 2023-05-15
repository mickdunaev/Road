package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.adapter.ReportVAdapter
import com.app.road.model.Report
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReportAllActiviy : AppCompatActivity() {
    val adapter = ReportVAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_all_activiy)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        val db = Firebase.firestore
        val auth = Firebase.auth
        db.collection("reports")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get().addOnCompleteListener {
                val docs = it.result
                if(docs != null){
                    val ll = ArrayList<Report>()
                    for(doc in docs){
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
        Utils.setScreenOpenAnalytics("Все отзывы", this::class.java.simpleName)
    }
}