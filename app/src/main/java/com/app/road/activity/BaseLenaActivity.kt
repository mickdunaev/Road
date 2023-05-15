package com.app.road.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import com.app.road.R
import com.app.road.Repository
import com.app.road.RepositoryAudio
import com.app.road.fragment.BaseFragment
import com.app.road.fragment.BaseLenaFragment
import com.app.road.fragment.NotifyFragment
import com.app.road.fragment.ProfileFragment
import com.app.road.model.Audio
import com.app.road.reciver.TimeNotification
import com.app.road.service.MediaPlayerService
import com.app.road.v4.ui.NewBaseLenaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class BaseLenaActivity : AppCompatActivity() {
    private lateinit var fragment: FragmentContainerView
    private lateinit var navigation: BottomNavigationView
    var serviceBound = false
    var isPause = false
    private var player: MediaPlayerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_lena)
        fragment = findViewById(R.id.fragment)
        navigation = findViewById(R.id.navigation)


        initNotify()

        navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {

                    baseSelect()
                }
                R.id.notify -> {
                    stopSelf()
                    notifySelect()
                }
                R.id.profile -> {
                    stopSelf()
                    profileSelect()
                }
            }
            true
        }
    }
    private fun initNotify(){
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                val notify = doc["notify"] as ArrayList<String>? ?: ArrayList()
                if(notify.isEmpty()){
                    notify.add("7:0")
                    notify.add("18:0")
                    notify.add("21:0")
                    val user = hashMapOf(
                        "notify" to notify
                    )
                    db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
                    setNotify(notify)
                } else {
                    setNotify(notify)
                }
            }
        }
    }

    private fun setNotify(notify: ArrayList<String>){
        Repository.notify = notify
        //превратить нотифи в милсы
        val cal = Calendar.getInstance()
        val time = cal.timeInMillis
        val mils = ArrayList<Long>()
        for(not in notify){
            mils.add(convertTimeToMils(cal, not))
        }
        //найти ближайший нотифи в милсах
        var at = time
        for(mil in mils){
            if(mil > at){
                at = mil
                break
            }
        }
        if(time > mils[mils.size - 1]){
            //переключить на следующий день
            val not = notify[0]
            at = convertTimeToMilsNextDay(cal, not)
        }else if(at == time){
            Toast.makeText(this,"Уведомление не установлено", Toast.LENGTH_SHORT).show()
            return
        }

        //для отладки перевести в время
        val alarm = convertMilsToTime(cal, at)
        Log.d("Mikhael", alarm)
        val intent = Intent(this, TimeNotification::class.java)
        val alarmManager2 = getSystemService(ALARM_SERVICE) as AlarmManager

        val pendingIntent2 = PendingIntent.getBroadcast(
            applicationContext, 0, Intent(
                this,
                TimeNotification::class.java
            ), FLAG_IMMUTABLE
        )
        alarmManager2.cancel(pendingIntent2)

        //поставить будильник
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("Mikhael", "message будильник на BaseLenaActivity ${textNotify(at, mils)}")
        intent.putExtra("course","lena")
        intent.putExtra("message",textNotify(at, mils))

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0,
            intent, PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pendingIntent)
        am.set(AlarmManager.RTC_WAKEUP, at, pendingIntent)
        //Toast.makeText(this,"Уведомление поставлено на $alarm", Toast.LENGTH_SHORT).show()
    }
    private fun convertTimeToMilsNextDay(cal: Calendar, time: String): Long{
        if(time.isEmpty()) return 0L
        val t = time.split(":")
        val h = t[0].toInt()
        val m = t[1].toInt()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(year, month, day, h, m, 0)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return cal.timeInMillis
    }

    private fun textNotify(mil: Long, mils: ArrayList<Long>): String{
        var ret = "Уведомление"
        if(mils.size < 3) return ret
        ret = when(mil){
            mils[0] -> "Утренние упражнения"
            mils[mils.size - 2] -> "Теория и задания"
            mils[mils.size - 1] -> "Упражнения перед сном"
            else -> "Поддерживающие уведомление"
        }
        return ret
    }
    private fun convertTimeToMils(cal: Calendar, time: String): Long{
        if(time.isEmpty()) return 0L
        val t = time.split(":")
        val h = t[0].toInt()
        val m = t[1].toInt()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(year, month, day, h, m, 0)
        return cal.timeInMillis
    }
    private fun convertMilsToTime(cal: Calendar, time: Long): String {
        cal.timeInMillis = time
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        return "$h:$m"
    }

    override fun onBackPressed() {
        finish()
    }

    private fun baseSelect(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, NewBaseLenaFragment::class.java, null)
            .setReorderingAllowed(true)
            .addToBackStack(null) // name can be null
            .commit()
    }

    private fun notifySelect(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, NotifyFragment::class.java, null)
            .setReorderingAllowed(true)
            .addToBackStack(null) // name can be null
            .commit()
    }

    private fun profileSelect(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, ProfileFragment::class.java, null)
            .setReorderingAllowed(true)
            .addToBackStack(null) // name can be null
            .commit()
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
    fun playAudio(path: String) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
//            val storage = StorageUtil(applicationContext)
//            storage.storeAudio(audioList)
//            storage.storeAudioIndex(audioIndex)
            Repository.audioText = "Первый день"
            RepositoryAudio.activeAudio = Audio(path, "Первый день", "", "")
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
    fun pauseAudio() {
        //Check is service is active
        if (!serviceBound) {
        } else {
            val broadcastIntent = Intent(MainActivity.Broadcast_PAUSE_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }
    fun resumeAudio() {
        //Check is service is active
        if (!serviceBound) {
        } else {
            val broadcastIntent = Intent(MainActivity.Broadcast_RESUME_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }
    fun stopSelf(){
        try {
            if (serviceBound) {
                unbindService(serviceConnection)
                //service is active
                //mHandler.
                player!!.stopSelf()
            }
        } catch (e: Exception){}

    }
    override fun onDestroy() {
        super.onDestroy()
        try{
            if (serviceBound) {
                unbindService(serviceConnection)
                //service is active
                //mHandler.
                player!!.stopSelf()
            }
        } catch (e: Exception){}
    }


}