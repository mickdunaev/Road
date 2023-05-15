package com.app.road.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.ui.SelectCourseActivity

class MainActivity : AppCompatActivity() {
    companion object {
        const val Broadcast_PLAY_NEW_AUDIO = "com.app.road.audioplayer.PlayNewAudio"
        const val Broadcast_PAUSE_AUDIO = "com.app.road.audioplayer.PauseAudio"
        const val Broadcast_RESUME_AUDIO = "com.app.road.audioplayer.ResumeAudio"
        const val Broadcast_SET_POSITION = "com.app.road.audioplayer.SetPosition"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}