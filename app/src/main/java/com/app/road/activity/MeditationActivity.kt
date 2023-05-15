package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.adapter.MeditationAdapter
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MeditationActivity : AppCompatActivity() {
    private var adapter = MeditationAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation)
        val trial = findViewById<View>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        if(Repository.trial) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE
        val mode = intent.getIntExtra("mode", -1)
        val collection = intent.getStringExtra("collection") ?: "finans_day1"
        back.setOnClickListener {
            finish()
        }
        findViewById<MaterialButton>(R.id.btnFinishAction).setOnClickListener {
            finish()
        }
        adapter.meditationSelect = {meditation ->
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", meditation.link)
            intent.putExtra("title", meditation.title)
            intent.putExtra("duration", meditation.duration)
            startActivity(intent)
        }
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        if(mode == 0){ //утренние медитации
            val db = Firebase.firestore
            db.collection(collection)
                .orderBy("id", Query.Direction.ASCENDING)
                .whereEqualTo("mode", 0L)
                .get()
                .addOnCompleteListener {
                    val docs = it.result
                    if (docs != null) {
                        val ls = ArrayList<Meditation>()
                        for(doc in docs){
                            val title = doc["title"] as String? ?: ""
                            val link = doc["link"] as String? ?: ""
                            val duration = doc["duration"] as String? ?: ""
                            val id = doc["id"] as Long? ?: 0L
                            val m = doc["mode"] as Long? ?: -1L
                            ls.add(Meditation(title,id,m,link, duration))
                        }
                        adapter.setList(ls)
                        Log.d("Mikhael", docs.size().toString())
                    }

                }

        } else if(mode == 1){//вечернии упражнения
            val db = Firebase.firestore
            db.collection(collection)
                .orderBy("id", Query.Direction.ASCENDING)
                .whereEqualTo("mode", 1L)
                .get()
                .addOnCompleteListener {
                    val docs = it.result
                    if (docs != null) {
                        val ls = ArrayList<Meditation>()
                        for(doc in docs){
                            val title = doc["title"] as String? ?: ""
                            val link = doc["link"] as String? ?: ""
                            val duration = doc["duration"] as String? ?: ""
                            val id = doc["id"] as Long? ?: 0L
                            val m = doc["mode"] as Long? ?: -1L
                            ls.add(Meditation(title,id,m,link, duration))
                        }
                        adapter.setList(ls)
                        Log.d("Mikhael", docs.size().toString())
                    }

                }
        } else if(mode == 2){
            val db = Firebase.firestore
            db.collection(collection)
                .orderBy("id", Query.Direction.ASCENDING)
                .whereEqualTo("mode", 2L)
                .get()
                .addOnCompleteListener {
                    val docs = it.result
                    if (docs != null) {
                        val ls = ArrayList<Meditation>()
                        for(doc in docs){
                            val title = doc["title"] as String? ?: ""
                            val link = doc["link"] as String? ?: ""
                            val duration = doc["duration"] as String? ?: ""
                            val id = doc["id"] as Long? ?: 0L
                            val m = doc["mode"] as Long? ?: -1L
                            ls.add(Meditation(title,id,m,link, duration))
                        }
                        adapter.setList(ls)
                        Log.d("Mikhael", docs.size().toString())
                    }

                }

        } else if(mode == 10){
            val db = Firebase.firestore
            db.collection(collection)
                .orderBy("id", Query.Direction.ASCENDING)
                .whereEqualTo("mode", 10L)
                .get()
                .addOnCompleteListener {
                    val docs = it.result
                    if (docs != null) {
                        val ls = ArrayList<Meditation>()
                        for(doc in docs){
                            val title = doc["title"] as String? ?: ""
                            val link = doc["link"] as String? ?: ""
                            val duration = doc["duration"] as String? ?: ""
                            val id = doc["id"] as Long? ?: 0L
                            val m = doc["mode"] as Long? ?: -1L
                            ls.add(Meditation(title,id,m,link, duration))
                        }
                        adapter.setList(ls)
                        Log.d("Mikhael", docs.size().toString())
                    }

                }

        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Медитации", this::class.java.simpleName)
    }
}