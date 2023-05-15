package com.app.road.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.road.model.Diary
import com.app.road.adapter.DiaryAdapter
import com.app.road.R
import com.app.road.Repository
import com.app.road.RepositoryAudio
import com.app.road.model.Audio
import com.app.road.service.MediaPlayerService
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiaryActivity : AppCompatActivity() {
    private lateinit var list: RecyclerView
    private var player: MediaPlayerService? = null
    var serviceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val play = findViewById<View>(R.id.play)
        play.setOnClickListener {
            playAudio("http://mybestway.ru/audio/diary.mp3")
            //Toast.makeText(this, "Воспроизвести аудио", Toast.LENGTH_SHORT).show()
        }
        list = findViewById(R.id.list)
        val addDiary = findViewById<View>(R.id.add_diary)
        addDiary.setOnClickListener {
            startActivity(Intent(this, AddDiaryActivity::class.java))
        }
        val adapter = DiaryAdapter()
        list.adapter = adapter
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("diary")
            .whereEqualTo("user_id", auth.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val docs = value.documents
                    val ls = ArrayList<Diary>()
                    for(doc in docs){
                        val header = doc["header"] as String? ?: ""
                        val text = doc["text"] as String? ?: ""
                        val year = doc["year"] as Long? ?: 1970L
                        val month = doc["month"] as Long? ?: 1L
                        val day = doc["day"] as Long? ?: 1L
                        val hour = doc["hour"] as Long? ?: 1L
                        val minute = doc["minute"] as Long? ?: 1L
                        ls.add(Diary(header, text, year, month, day, hour, minute))
                    }
                    adapter.setList(ls)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Дневник благодарности", this::class.java.simpleName)
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
    Repository.audioText = "Дневник"
            RepositoryAudio.activeAudio = Audio(path, "Дневник", "", "")
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