package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.VideoPlayerActivity
import com.app.road.v4.Utils
import java.util.Calendar

class MillionStartScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_million_start_screen)
        findViewById<View>(R.id.btnFreeDays).setOnClickListener {
            startActivity(Intent(this, PathMillionActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.autor).setOnClickListener {
            val intent = Intent(this, EkaterinaProfileActivity::class.java)
            startActivity(intent)
        }
        findViewById<View>(R.id.btnVideoMillion1).setOnClickListener {
            Repository.saveLesson(
                name = "Екатерина Кудрявцева",
                content = "Путь на миллион. Вводный 1",
                link = "http://mybestway.ru/million/start/1.mp4",
            )
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", "http://mybestway.ru/million/start/1.mp4")
            startActivity(intent)
        }
        findViewById<View>(R.id.btnVideoMillion2).setOnClickListener {
            Repository.saveLesson(
                name = "Екатерина Кудрявцева",
                content = "Путь на миллион. Вводный 2",
                link = "http://mybestway.ru/million/start/2.mp4",
            )

            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", "http://mybestway.ru/million/start/2.mp4")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Курс на миллион", this::class.java.simpleName)
    }
}