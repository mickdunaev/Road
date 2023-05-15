package com.app.road.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.widget.Button
import android.widget.VideoView
import com.app.road.R
import com.app.road.Repository
import com.app.road.service.MediaPlayerService
import com.app.road.v4.Utils
import com.app.road.v4.ui.TvoyLichniyCouchActivity

class StartTestActivity : AppCompatActivity() {

    private var player: MediaPlayerService? = null
    var serviceBound = false
    var isPlay = true
    var isResume = false

    /*private fun updateUi(){
        if(isPlay){
            playImage.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playImage.setImageResource(R.drawable.icon_meditation)
        }
    }*/

    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_test)
        Repository.registerComplete = false

        val btnSkip = findViewById<Button>(R.id.btnSkip)

        videoView = findViewById(R.id.videoView)
        val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.start_video)
        videoView?.setVideoURI(uri)
        videoView?.start()

        videoView?.setOnPreparedListener { mp ->
            mp.setVolume(0.5f, 0.5f)
        }

        /* startAudio.setOnClickListener {
             if(!isPlay){
                 if(!isResume){
                     playAudio("http://mybestway.ru/audio/enter.mp3")
                     videoView?.start()
                 } else {
                     resumeAudio()
                     videoView?.start()
                 }

                 isPlay = true
                 updateUi()
             } else {
                 isResume = true
                 isPlay = false
                 pauseAudio()
                 updateUi()
                 videoView?.pause()
             }
             //Toast.makeText(baseContext, "Воспроизвести аудио файл", Toast.LENGTH_SHORT).show()
         }*/
        //playAudio("http://mybestway.ru/audio/enter.mp3")
        //updateUi()
        btnSkip.setOnClickListener {
            startActivity(Intent(this, TvoyLichniyCouchActivity::class.java))
            //startActivity(Intent(this, SelectCoursActivity::class.java))
        }
    }

    private fun pauseAudio() {
        //Check is service is active
        if (!serviceBound) {
        } else {
            val broadcastIntent = Intent(MainActivity.Broadcast_PAUSE_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    private fun resumeAudio() {
        //Check is service is active
        if (!serviceBound) {
        } else {
            val broadcastIntent = Intent(MainActivity.Broadcast_RESUME_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("serviceStatus")
    }

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder: MediaPlayerService.LocalBinder = service as MediaPlayerService.LocalBinder
            player = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }
    /*private fun playAudio(path: String) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudio(audioList)
//            storage.storeAudioIndex(audioIndex)
    Repository.audioText = "Вводная"
            RepositoryAudio.activeAudio = Audio(path, "Вводная", "", "")
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, BIND_AUTO_CREATE)
        } else {
            //Store the new audioIndex to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudioIndex(audioIndex)

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            RepositoryAudio.activeAudio = Audio(path, "", "", "")
            val broadcastIntent = Intent(Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }*/

    override fun onResume() {
        super.onResume()
        videoView?.start()
        Utils.setScreenOpenAnalytics("Начальное видео", this::class.java.simpleName)
    }

    override fun onStop() {
        super.onStop()
        videoView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            //mHandler.
            player!!.stopSelf()
        }
    }
}