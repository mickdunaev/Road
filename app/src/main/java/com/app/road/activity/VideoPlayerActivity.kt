package com.app.road.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.v4.Utils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView


class VideoPlayerActivity : AppCompatActivity() {
    var player: ExoPlayer ? = null
    var url = "http://mybestway.ru/video/intro/BigBuckBunny.mp4"
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
         url = intent.getStringExtra("url")?: "http://mybestway.ru/video/intro/BigBuckBunny.mp4"
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
         player = ExoPlayer.Builder(this).build()
         val playerView = findViewById<StyledPlayerView>(R.id.player_view)
         playerView.setPlayer(player)
         val mediaItem: MediaItem = MediaItem.fromUri(url)
// Set the media item to be played.
         player!!.setMediaItem(mediaItem)
// Prepare the player.
         player!!.prepare()
// Start the playback.
         player!!.play()

         window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Видеоплеер", this::class.java.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(player != null){
            player!!.stop()
            player!!.release()
        }
    }
}