package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.AgreemenActivity
import com.app.road.activity.PlayMeditationActivity
import com.app.road.activity.VideoPlayerActivity
import com.app.road.adapter.MeditationAdapter
import com.app.road.adapter.MeditationAdapterLena
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeightVideoActivity : AppCompatActivity() {
    private var adapter = MeditationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_video)
        val name = findViewById<TextView>(R.id.name)
        name.text = Repository.name + "!"
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        adapter.meditationSelect = {meditation ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", meditation.link)
            intent.putExtra("title", meditation.title)
            intent.putExtra("duration", meditation.duration)
            intent.putExtra("medi", true)
            startActivity(intent)
        }
        findViewById<View>(R.id.play).setOnClickListener {
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", "http://mybestway.ru/weight/7.mp3")
            intent.putExtra("title", "Медитация")
            intent.putExtra("duration", "08:15")
            intent.putExtra("medi", false)
            startActivity(intent)

        }
        findViewById<View>(R.id.play2).setOnClickListener {
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", "http://mybestway.ru/weight/7.mp3")
            intent.putExtra("title", "Медитация")
            intent.putExtra("duration", "08:15")
            intent.putExtra("medi", false)
            startActivity(intent)

        }
        findViewById<View>(R.id.itm).setOnClickListener {
            val intent = Intent(this, AgreemenActivity::class.java)
            intent.putExtra("url", "file:///android_res/raw/itm.html")
            startActivity(intent)

        }

        val db = Firebase.firestore
        db.collection("weight")
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener {
                val docs = it.result
                if (docs != null) {
                    val ls = ArrayList<Meditation>()
                    for(doc in docs){
                        val title = doc["name"] as String? ?: ""
                        val link = doc["link"] as String? ?: ""
                        val duration = doc["duration"] as String? ?: ""
                        val id = doc["id"] as Long? ?: 0L
                        val m = -1L
                        ls.add(Meditation(title,id,m,link, duration))
                    }
                    adapter.setList(ls)
                    Log.d("Mikhael", docs.size().toString())
                }

            }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Видео курса похудения", this::class.java.simpleName)
    }
}