package com.app.road.v4.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.AgreemenActivity
import com.app.road.activity.NotepadActivity
import com.app.road.activity.PlayMeditationActivity
import com.app.road.activity.VideoPlayerActivity
import com.app.road.adapter.MeditationAdapterLena
import com.app.road.model.Meditation
import com.app.road.v4.Utils
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EveningMillionActivity : AppCompatActivity() {
    private var adapter = MeditationAdapterLena()
    private lateinit var day_ui: TextView
    private var tvDay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evening_million)
        val trial = findViewById<TextView>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        day_ui = findViewById<TextView>(R.id.day)
        val name = findViewById<TextView>(R.id.name)
        name.text = Repository.name + "!"
        day_ui.text = Repository.dayMillion.toString()
        if(Repository.trialMillion) trial.visibility = View.VISIBLE
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
                if(Repository.dayMillion == 8L){
                    val accept = findViewById<CheckBox>(R.id.accept)
                    if(accept.isChecked){
                        val intent = Intent(this, PlayMeditationActivity::class.java)
                        intent.putExtra("link", meditation.link)
                        intent.putExtra("title", meditation.title)
                        intent.putExtra("duration", meditation.duration)
                        startActivity(intent)
                    } else {
                        val alert = AlertDialog.Builder(this)
                        alert.setTitle("Предупрежнение")
                        alert.setMessage("Вы должны согласиться с противопоказаниями")
                        alert.setPositiveButton("OK", null)
                        alert.show()
                    }
                } else {
                    val intent = Intent(this, PlayMeditationActivity::class.java)
                    intent.putExtra("link", meditation.link)
                    intent.putExtra("title", meditation.title)
                    intent.putExtra("duration", meditation.duration)
                    startActivity(intent)
                }
            } else {
                Repository.saveLesson(
                    name = "Путь на миллион. День ${Repository.dayMillion}",
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
        updateData(Repository.dayMillion)
    }
    private fun updateData(day2: Long){
        var day = day2
        val db = Firebase.firestore
        var course = "million_course"
        if(Repository.nextStage){
            course = "course"
            day = day - 8L
        }
        if(day2 == 8L){
            val dih = findViewById<View>(R.id.dihatelnay)
            val accept = findViewById<CheckBox>(R.id.accept)
            dih.visibility = View.VISIBLE
            accept.visibility = View.VISIBLE
            dih.setOnClickListener {
                val intent = Intent(this, AgreemenActivity::class.java)
                intent.putExtra("url", "file:///android_res/raw/accept.html")
                startActivity(intent)
            }
        } else {
            val dih = findViewById<View>(R.id.dihatelnay)
            val accept = findViewById<CheckBox>(R.id.accept)
            dih.visibility = View.GONE
            accept.visibility = View.GONE
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
        Utils.setScreenOpenAnalytics("Вечерний тренинг миллион", this::class.java.simpleName)
    }
}