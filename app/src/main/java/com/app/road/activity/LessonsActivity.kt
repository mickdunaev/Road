package com.app.road.activity

import android.app.DownloadManager.Query
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.adapter.LessonsAdapter
import com.app.road.model.Lesson
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LessonsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lessons)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val list = findViewById<RecyclerView>(R.id.list)
        val adapter = LessonsAdapter()
        list.adapter = adapter
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("lessons")
            .whereEqualTo("uid", auth.uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                val docs = it.result
                val ls = ArrayList<Lesson>()
                if (docs != null) {
                    docs.forEach { doc ->
                        val name = doc["name"] as String? ?: ""
                        val date = doc["date"] as String? ?: ""
                        val content = doc["content"] as String? ?: ""
                        val link = doc["link"] as String? ?: ""
                        ls.add(Lesson(content, name, date, link))
                    }
                    adapter.setList(ls)
                }
            }
        adapter.containerSelect = {lesson ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", lesson.link)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Задания", this::class.java.simpleName)
    }
}