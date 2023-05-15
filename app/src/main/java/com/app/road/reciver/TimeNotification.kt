package com.app.road.reciver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.*
import com.app.road.v4.ui.BaseMillionActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class TimeNotification: BroadcastReceiver() {
    val ALARM_CHANNEL_ID = "alarm_playback_channel"
    private val NOTIFICATION_ID = 102
    private var course = "base"
    override fun onReceive(context: Context, intent: Intent) {
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_icon_way) //replace with your own image
        createChannel(context)
        val message = intent.getStringExtra("message") ?: "message"
        val test = intent.getStringExtra("test") ?: "test"
        course = intent.getStringExtra("course") ?: "base"
        Log.d("Mikhael", "message course $course")
        Log.d("Mikhael", "message test $test")
        val i = when(message){
            "Поддерживающие уведомление" -> when(course){
                "base" -> Intent(context, DayAffirmationActivity::class.java)
                "lena" -> {
                    val newIntent = Intent(context, PlayMeditationActivity::class.java)
                    intent.putExtra("link", "http://mybestway.ru/meditation/1.mp3")
                    intent.putExtra("title", "Короткая медитация")
                    intent.putExtra("duration", "06:09")
                    newIntent
                }
                "million" -> {
                    val newIntent = Intent(context, PlayMeditationActivity::class.java)
                    intent.putExtra("link", "http://mybestway.ru/meditation/1.mp3")
                    intent.putExtra("title", "Короткая медитация")
                    intent.putExtra("duration", "06:09")
                    newIntent
                }
                else -> Intent(context, DayAffirmationActivity::class.java)
            }
            else -> when(course){
                "base" -> Intent(context, BaseActivity::class.java)
                "lena" -> Intent(context, BaseLenaActivity::class.java)
                "million" -> Intent(context, BaseMillionActivity::class.java)
                else -> Intent(context, BaseActivity::class.java)
            }
        }
//        val i = Intent(context, SplashActivity::class.java)
//        i.putExtra("notify", true)

            //Intent(context, BaseActivity::class.java)

        i.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(context, 0, i, 0)

        Log.d("Mikhael", "Показать уведомление message $message")
        val notificationBuilder = NotificationCompat.Builder( context, ALARM_CHANNEL_ID)
            .setColor(context.getColor(R.color.colorAccent))
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_baseline_face_24)
            .setContentText(message)
            .setContentTitle("Путь")
            //.setContentInfo("title3")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi)
            .setDefaults(Notification.DEFAULT_ALL)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, notificationBuilder.build())
        initNotify(context)
        val auth = Firebase.auth
        val db = Firebase.firestore
        val time = messageTime()
        val notify = hashMapOf(
            "user_id" to auth.uid,
            "message" to message,
            "time" to time
        )
        db.collection("notify").document().set(notify)

    }
    private fun messageTime(): String{
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        return "$day/$month/$year $hour:$minute"
    }
    private fun createChannel(context: Context) {
        // The id of the channel.
        val id: String = ALARM_CHANNEL_ID
        // The user-visible name of the channel.
        val name: CharSequence = "Alarm notify"
        // The user-visible description of the channel.
        val description = "Alarm control"
        val importance: Int
        importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            0
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.setShowBadge(false)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                mChannel
            )
        }
    }
    private fun initNotify(contex: Context){
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                val notify = doc["notify"] as ArrayList<String>? ?: ArrayList()
                if(!notify.isEmpty()){
                    setNotify(contex, notify)
                }
            }
        }
    }

    private fun setNotify(context: Context, notify: ArrayList<String>){
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
            Toast.makeText(context,"Уведомление не установлено", Toast.LENGTH_SHORT).show()
            return
        }
        //для отладки перевести в время
        val alarm = convertMilsToTime(cal, at)
        Log.d("Mikhael", alarm)
        val intent = Intent(context, TimeNotification::class.java)
        //поставить будильник
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var text = textNotify(at, mils)
        //text = "непонятно что"
        intent.putExtra("message", text)
        intent.putExtra("test","это следующее уведомление из будильника")
        intent.putExtra("course", course)
        Log.d("Mikhael", "message следующий будильник на сообщение $text")
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pendingIntent)
        am.set(AlarmManager.RTC_WAKEUP, at, pendingIntent)
        Toast.makeText(context,"Уведомление поставлено на $alarm", Toast.LENGTH_SHORT).show()

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
    private fun convertMilsToTime(cal: Calendar, time: Long): String {
        cal.timeInMillis = time
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        return "$h:$m"
    }

}