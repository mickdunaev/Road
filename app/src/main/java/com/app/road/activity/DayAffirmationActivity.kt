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

class DayAffirmationActivity : AppCompatActivity() {
    var isNot = false
    private var adapter = MeditationAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_affirmation)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        isNot = intent.getBooleanExtra("notify", false)
        adapter.meditationSelect = {meditation ->
            val intent = Intent(this, PlayMeditationActivity::class.java)
            intent.putExtra("link", meditation.link)
            intent.putExtra("title", meditation.title)
            intent.putExtra("duration", meditation.duration)
            startActivity(intent)
        }
        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        val ls = ArrayList<Meditation>()
        if(Repository.day < 8){
            ls.add(Meditation("медитация – короткая ресурсная",-1,-1,"http://mybestway.ru/audio/affirmation/week1/1.mp3", "5:10"))
            ls.add(Meditation("афирмации",-1,-1,"http://mybestway.ru/audio/affirmation/week1/2.mp3", "1:46"))
        } else if(Repository.day < 15){
            if(Repository.day == 8L){
                ls.add(Meditation("медитация – короткая ресурсная",-1,-1,"http://mybestway.ru/audio/affirmation/week1/1.mp3", "5:10"))
            }else {
                ls.add(Meditation("медитация благодарность, короткая версия",-1,-1,"http://mybestway.ru/audio/day9/morning/1.mp3", "2:16"))
            }
            ls.add(Meditation("афирмации",-1,-1,"http://mybestway.ru/audio/affirmation/week2/1.mp3", "1:32"))
        } else if(Repository.day < 21){
            if(Repository.day == 15L){
                ls.add(Meditation("медитация благодарность, короткая версия",-1,-1,"http://mybestway.ru/audio/affirmation/week1/1.mp3", "2:16"))
            }else {
                ls.add(Meditation("Медитация счастья (короткая версия)",-1,-1,"http://mybestway.ru/audio/week3/notify/1.mp3", "3:55"))
            }
            ls.add(Meditation("афирмации",-1,-1,"http://mybestway.ru/audio/week3/notify/2.mp3", "1:26"))
        } else if(Repository.day < 29){
            if(Repository.day == 21L){
                ls.add(Meditation("Медитация счастья (короткая версия)",-1,-1,"http://mybestway.ru/audio/week3/notify/1.mp3", "3:55"))
            }else {
                ls.add(Meditation("Медитация любви (короткая версия)",-1,-1,"http://mybestway.ru/audio/week4/notify/1.mp3", "4:02"))
            }
            ls.add(Meditation("афирмации",-1,-1,"http://mybestway.ru/audio/week4/notify/2.mp3", "1:27"))
        }
        if(Repository.selectLena){
            ls.clear()
            ls.add(Meditation("Короткая медитация",-1,-1,"http://mybestway.ru/meditation/1.mp3", "06:04"))
        }
        adapter.setList(ls)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isNot){
            val intent  = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Список аффирмаций", this::class.java.simpleName)
    }
}