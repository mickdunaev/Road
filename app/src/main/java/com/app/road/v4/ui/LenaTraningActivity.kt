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
import com.app.road.activity.NotepadActivity
import com.app.road.activity.PlayMeditationActivity
import com.app.road.activity.VideoPlayerActivity
import com.app.road.adapter.MeditationAdapterLena
import com.app.road.log
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class LenaTraningActivity : AppCompatActivity() {
    private var adapter = MeditationAdapterLena()
    private lateinit var day_ui: TextView
    private var tvDay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lena_traning)
        val mode = intent.getIntExtra("mode", 0)
        val collection = intent.getStringExtra("collection") ?: "finans_day1"
        val trial = findViewById<TextView>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        val mode_ui = findViewById<TextView>(R.id.mode)
        day_ui = findViewById<TextView>(R.id.day)
        val name = findViewById<TextView>(R.id.name)
        name.text = Repository.name + "!"
        day_ui.text = Repository.day.toString()
        val isLena = intent.getBooleanExtra("lena", false)
        if(isLena){
            day_ui.text = Repository.dayLena.toString()
        }
        if(Repository.trial) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE
        if(isLena && Repository.trialLena) {
            trial.visibility = View.VISIBLE
            trial.text = " "
        }
        else trial.visibility = View.INVISIBLE

        back.setOnClickListener {
            finish()
        }
        findViewById<MaterialButton>(R.id.btnFinishAction).setOnClickListener {
            finish()
        }
        adapter.notepadSelect = {
            startActivity(Intent(this, NotepadActivity::class.java))
        }
        adapter.meditationSelect = {meditation ->
            if(meditation.mode == 0L){
                val intent = Intent(this, PlayMeditationActivity::class.java)
                intent.putExtra("link", meditation.link)
                intent.putExtra("title", meditation.title)
                intent.putExtra("duration", meditation.duration)
                startActivity(intent)
            } else {
                var tit = "Деньги в голове"
                if(Repository.videoMode == 2){
                    tit = "Здоровье"
                }
                Repository.saveLesson(
                    name = "$tit. День ${Repository.dayMillion}",
                    content = "${meditation.title}",
                    link = meditation.link,
                )

                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("url", meditation.link)
                intent.putExtra("title", meditation.title)
                intent.putExtra("duration", meditation.duration)
                startActivity(intent)
            }
        }

        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        tvDay = findViewById(R.id.tvDay)
        updateData(Repository.dayLena)
    }
    private fun updateData(day: Long){
        val db = Firebase.firestore
        var course = "course"

        log("videoMode: ${Repository.videoMode}")
        if(Repository.videoMode == 2){
            course = "course2"
        } else {
            if(Repository.dayLena == 0L && Repository.videoMode == 1) {
                day_ui.visibility = View.INVISIBLE
                tvDay?.text = "Введение в курс"
            } else {
                day_ui.text = Repository.dayLena.toString()
                day_ui.visibility = View.VISIBLE
                tvDay?.text = "День"
            }
        }
        db.collection(course)
            .orderBy("id", Query.Direction.ASCENDING)
            .whereEqualTo("day", day)
            .get()
            .addOnCompleteListener {
                val docs = it.result
                if (docs != null) {
                    val ls = ArrayList<Meditation>()
                    for(doc in docs) {
                        val title = doc["title"] as String? ?: ""
                        val link = doc["link"] as String? ?: ""
                        val duration = doc["duration"] as String? ?: ""
                        val id = doc["id"] as Long? ?: 0L
                        val m = doc["mode"] as Long? ?: -1L
                        ls.add(
                            Meditation(
                            title,
                            id,
                            m,
                            link,
                            duration,
                            courseButton = day == 0L // для того, чтобы в нулевом дне на кнопке снизу менять текст
                        )
                        )
                    }
                    adapter.setList(ls)
                    Log.d("Mikhael", docs.size().toString())
                }

            }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Тренинг Екатерины", this::class.java.simpleName)
    }

}