package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.adapter.MeditationAdapter
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoritesActivity : AppCompatActivity() {
    private var adapter = MeditationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        val trial = findViewById<View>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        if(Repository.trial) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE
        back.setOnClickListener {
            finish()
        }
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        val db = Firebase.firestore
        val auth = Firebase.auth
        val uid = auth.uid!!
        db.collection("favorites")
            .whereEqualTo("user_id", uid)
            .get()
            .addOnCompleteListener {
                val docs = it.result
                if(docs.size() != 0 ){
                    val ls = ArrayList<Meditation>()
                    for(doc in docs){
                        val title = doc["title"] as String? ?: ""
                        val link = doc["link"] as String? ?: ""
                        val duration = doc["duration"] as String? ?: ""
                        ls.add(Meditation(title,0,0,link, duration))
                    }
                    adapter.setList(ls)
                }
            }
        adapter.meditationSelect = {meditation ->
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", meditation.link)
            intent.putExtra("title", meditation.title)
            intent.putExtra("duration", meditation.duration)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Избранное", this::class.java.simpleName)
    }
}