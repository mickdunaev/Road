package com.app.road.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.Repository
import com.app.road.RepositoryAudio
import com.app.road.model.Audio
import com.app.road.service.MediaPlayerService
import com.app.road.v4.Utils
import com.google.android.material.slider.Slider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlayMeditationActivity : AppCompatActivity() {
    private var player: MediaPlayerService? = null
    private lateinit var progress: ProgressBar
    private lateinit var seekBar: Slider
    private lateinit var time: TextView
    private lateinit var star: ImageView
    var medi = false
    var serviceBound = false
    var isPause = false
    var isFavotite = false
    var handler: Handler = Handler()
    var favoriteDocument: DocumentSnapshot? = null

    private fun updateFavorite(){
        if(isFavotite){
            star.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            star.setImageResource(R.drawable.ic_baseline_star_border_24)
        }
    }

    private fun testOnFavotite(link: String){
        val uid = Firebase.auth.uid
        val db = Firebase.firestore
        db.collection("favorites")
            .whereEqualTo("user_id", uid)
            .whereEqualTo("link", link)
            .get()
            .addOnCompleteListener { result ->
                val docs = result.result
                if (docs != null) {
                    if(docs.size() == 0){
                        isFavotite = false
                        favoriteDocument = null
                        updateFavorite()
                    } else {
                        favoriteDocument = docs.documents[0]
                        isFavotite = true
                        updateFavorite()

                    }
                } else {
                    favoriteDocument = null
                    isFavotite = false
                    updateFavorite()
                }
            }
    }



    private fun deleteFavorite(){
        if(favoriteDocument == null) return
        favoriteDocument!!.reference.delete()
        favoriteDocument = null
    }

    private fun saveFavoriteState(link: String, title: String, duration: String){
        val uid = Firebase.auth.uid
        val db = Firebase.firestore
        if(isFavotite){
            //save document
            val favorite = hashMapOf(
                "user_id" to uid,
                "link" to link,
                "title" to title,
                "duration" to duration
            )
            db.collection("favorites").add(favorite as Map<String, Any>).addOnCompleteListener {
//                if(it.isSuccessful){
//                    favoriteDocument = it.result.get().result
//                }
                db.collection("favorites")
                    .whereEqualTo("link", link)
                    .whereEqualTo("user_id", uid)
                    .get()
                    .addOnCompleteListener {
                        val docs = it.result
                        if(docs.size() != 0 && docs.documents.size != 0){
                            favoriteDocument = docs.documents[0]
                        }
                    }
            }
        } else {
            //delete document
            deleteFavorite()
        }
    }

    private val sendData: Runnable = object : Runnable {
        override fun run() {
            try {
                //prepare and send the data here..
                if (serviceBound && player != null) {
                    progress.progress = player!!.progress
                    seekBar.value = player!!.progress.toFloat()
                    time.text = player!!.time
                    if(medi){
                        if(player!!.time == "01:00"){
                            finish()
                        }
                    }
                }
                handler.postDelayed(this, 1000)
            } catch (e: Exception) {
                //e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Проигрывание медитации", this::class.java.simpleName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_meditation)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }


        val link = intent.getStringExtra("link") ?: "http://mybestway.ru/meditation/1.mp3"
        val title = intent.getStringExtra("title") ?: "Короткая медитация"
        val duration = intent.getStringExtra("duration") ?: "06:09"
        medi = intent.getBooleanExtra("medi",false)
        val title_ui = findViewById<TextView>(R.id.title)
        val duration_ui = findViewById<TextView>(R.id.duration)
        val playTime_ui = findViewById<TextView>(R.id.play_time)
        time = findViewById(R.id.time)
        val durationTime_ui = findViewById<TextView>(R.id.duration_time)
        seekBar = findViewById(R.id.seekBar)
        star = findViewById(R.id.star)
        updateFavorite()
        testOnFavotite(link)
        star.setOnClickListener {
            if(isFavotite){
                isFavotite = false
                updateFavorite()
                saveFavoriteState(link, title, duration)
            } else {
                isFavotite = true
                updateFavorite()
                saveFavoriteState(link, title, duration)
            }
        }
        val seek1 = findViewById<View>(R.id.seek1)
        val seek2 = findViewById<View>(R.id.seek2)
        val play = findViewById<ImageView>(R.id.play)
        progress = findViewById(R.id.progress)
        handler.post(sendData)
        seekBar.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(slider: Slider) {
                val pos = seekBar.value.toInt()
                Log.d("Mikhael", "New Position $pos")
                setAudioPosition(pos)
            }
        })
        title_ui.text = title
        Repository.audioText = title
        duration_ui.text = duration
        playTime_ui.text = duration
        play.setOnClickListener {
            if(!isPause) {
                pauseAudio()
                play.setImageResource(R.drawable.icon_meditation)
                isPause = true
            } else {
                resumeAudio()
                play.setImageResource(R.drawable.ic_baseline_pause_24)
                isPause = false
            }
        }
        playAudio(link)
        play.setImageResource(R.drawable.ic_baseline_pause_24)

        configureUI()
    }

    private fun configureUI() {
        findViewById<Slider>(R.id.seekBar).also {
            it.setCustomThumbDrawable(R.drawable.mediation_seekbar_thumb)
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
    private fun playAudio(path: String) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudio(audioList)
//            storage.storeAudioIndex(audioIndex)
            RepositoryAudio.activeAudio = Audio(path, Repository.audioText, "альбом", "артист")
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
            val broadcastIntent = Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
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
    private fun setAudioPosition(pos: Int){
        //Check is service is active
        if (!serviceBound) {
        } else {
            val broadcastIntent = Intent(MainActivity.Broadcast_SET_POSITION)
            broadcastIntent.putExtra("pos", pos)
            sendBroadcast(broadcastIntent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(sendData)
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            //mHandler.
            player!!.stopSelf()
        }
    }


}