package com.app.road.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.Repository
import com.app.road.reciver.TimeNotification
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SelectNotifyActivity : AppCompatActivity() {
    private lateinit var countNotity: NumberPicker
    private lateinit var okButton: View
    var notify = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_notify)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        okButton = findViewById(R.id.ok)
        okButton.setOnClickListener {
            finish()
        }

        countNotity = findViewById(R.id.countNotify)
        countNotity.maxValue = 10
        countNotity.minValue = 0
        countNotity.value = 3
        countNotity.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                //val cn = doc["count_notify"] as String? ?: "3"
                notify = doc["notify"] as ArrayList<String>? ?: ArrayList()
                if(!notify.isEmpty() && notify.size >=3){
                    val count = notify.size - 3
                    countNotity.value = count
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
            Toast.makeText(this,"Уведомление не установлено", Toast.LENGTH_SHORT).show()
            return
        }

        //для отладки перевести в время
        val alarm = convertMilsToTime(cal, at)
        Log.d("Mikhael", alarm)
        val intent = Intent(context, TimeNotification::class.java)
        val alarmManager2 = getSystemService(ALARM_SERVICE) as AlarmManager

        val pendingIntent2 = PendingIntent.getBroadcast(
            applicationContext, 0, Intent(
                this,
                TimeNotification::class.java
            ), FLAG_IMMUTABLE
        )
        alarmManager2.cancel(pendingIntent2)

        //поставить будильник
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val text = textNotify(at, mils)
        intent.putExtra("message", text)
        Log.d("Mikhael", "message  установить будильник $text")
        var course = "base"
        if(Repository.selectLena) course = "lena"
        if(Repository.selectMillion) course = "million"
        intent.putExtra("course", course)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pendingIntent)
        am.set(AlarmManager.RTC_WAKEUP, at, pendingIntent)
        Toast.makeText(context,"Уведомление поставлено на $alarm", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        createNotify()
        val auth = Firebase.auth
        val db = Firebase.firestore
        val user = hashMapOf(
            "count_notify" to countNotity.value.toString(),
            "notify" to notify
        )


        Repository.notify = notify
        db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
        setNotify(this, notify)
    }

    private fun createNotify(){
        val count = countNotity.value
        if(count == 0) return
        val cal = Calendar.getInstance()
        val na = ArrayList<Long>()
        val timeMorning = notify[0]
        val timeDay = notify[notify.size - 2]
        val timeEvening = notify[notify.size - 1]
        val milsMorning = convertTimeToMils(cal, timeMorning)
        val milsDay = convertTimeToMils(cal, timeDay)
        val milsEvening = convertTimeToMils(cal, timeEvening)
        na.add(milsMorning)
        //здесь цикл заполнения
        //промежуток от утра до вечера
        val length = milsDay - milsMorning
        if(length < (count + 1)) return
        val step = length / (count + 1)
        for(i in 0 until count){
            na.add(na[na.size -1] + step)
        }
        //и добавляем последнии значения
        na.add(milsDay)
        na.add(milsEvening)
        notify.clear()
        for(time in na){
            notify.add(convertMilsToTime(cal, time))
        }
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

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Поставить уведомления", this::class.java.simpleName)
    }
}
