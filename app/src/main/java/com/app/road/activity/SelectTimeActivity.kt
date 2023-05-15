package com.app.road.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.app.road.R
import com.app.road.Repository
import com.app.road.reciver.TimeNotification
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class SelectTimeActivity : AppCompatActivity() {
    private lateinit var timeMoringText: TextView
    private lateinit var timeDayText: TextView
    private lateinit var timeEveningText: TextView

    private lateinit var blockMorning1: View
    private lateinit var blockMorning2: View
    private lateinit var blockDay1: View
    private lateinit var blockDay2: View
    private lateinit var blockEvening1: View
    private lateinit var blockEvening2: View

    private lateinit var morningHour: NumberPicker
    private lateinit var morningMinute: NumberPicker
    private lateinit var dayHour: NumberPicker
    private lateinit var dayMinute: NumberPicker
    private lateinit var eveningHour: NumberPicker
    private lateinit var eveningMinute: NumberPicker
    private lateinit var okButton: View
    private var notify = ArrayList<String>(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_time)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        okButton = findViewById(R.id.ok)
        okButton.setOnClickListener {
            finish()
        }
        //okButton.visibility = View.GONE
        blockMorning1 = findViewById(R.id.morning)
        blockDay1 = findViewById(R.id.day)
        blockEvening1 = findViewById(R.id.evening)
        blockMorning2 = findViewById(R.id.morning_time)
        blockDay2 = findViewById(R.id.day_time)
        blockEvening2 = findViewById(R.id.evening_time)

        morningHour = findViewById(R.id.morning_hour)
        morningMinute = findViewById(R.id.morning_minute)
        dayHour = findViewById(R.id.day_hour)
        dayMinute = findViewById(R.id.day_minute)
        eveningHour = findViewById(R.id.evening_hour)
        eveningMinute = findViewById(R.id.evening_minute)

        timeMoringText = findViewById(R.id.time_morning)
        timeDayText = findViewById(R.id.time_day)
        timeEveningText = findViewById(R.id.time_evening)

        blockMorning1.setOnClickListener {
            if(blockMorning2.visibility == View.GONE) {
                blockMorning2.visibility = View.VISIBLE

            }
            else {
                blockMorning2.visibility = View.GONE

            }
        }
        blockDay1.setOnClickListener {
            if(blockDay2.visibility == View.GONE) {
                blockDay2.visibility = View.VISIBLE
            }
            else {
                blockDay2.visibility = View.GONE
            }
        }
        blockEvening1.setOnClickListener {
            if(blockEvening2.visibility == View.GONE) {
                blockEvening2.visibility = View.VISIBLE
            }
            else {
                blockEvening2.visibility = View.GONE
            }
        }

        setupUi()
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                notify = doc["notify"] as ArrayList<String>? ?: ArrayList()
                if(!notify.isEmpty() && notify.size >=3){
                    val s = notify.size
                    val m = notify[0].split(":")
                    val d = notify[s - 2].split(":")
                    val e = notify[s - 1].split(":")
                    val mh = m[0]
                    val mm = m[1]
                    val dh = d[0]
                    val dm = d[1]
                    val eh = e[0]
                    val em = e[1]
                    morningHour.value = mh.toInt()
                    morningMinute.value = mm.toInt()
                    dayHour.value = dh.toInt()
                    dayMinute.value = dm.toInt()
                    eveningHour.value = eh.toInt()
                    eveningMinute.value = em.toInt()
                    timeMoringText.text = getTextTime(morningHour.value, morningMinute.value)
                    timeDayText.text = getTextTime(dayHour.value, dayMinute.value)
                    timeEveningText.text = getTextTime(eveningHour.value, eveningMinute.value)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        val auth = Firebase.auth
        val db = Firebase.firestore
        notify[0] = morningHour.value.toString() + ":" + morningMinute.value.toString()
        notify[notify.size - 2] = dayHour.value.toString() + ":" + dayMinute.value.toString()
        notify[notify.size - 1] = eveningHour.value.toString() + ":" + eveningMinute.value.toString()

        val user = hashMapOf(
            "morning_hour" to morningHour.value.toString(),
            "morning_minute" to morningMinute.value.toString(),
            "day_hour" to dayHour.value.toString(),
            "day_minute" to dayMinute.value.toString(),
            "evening_hour" to eveningHour.value.toString(),
            "evening_minute" to eveningMinute.value.toString(),
            "notify" to notify
        )
        Repository.notify = notify
        db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
        setNotify(this, notify)
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
        intent.putExtra("message",textNotify(at, mils))
        Log.d("Mikhael", "message установить будильник из времени ${textNotify(at, mils)}")
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

    private fun getTextTime(hour: Int, minute: Int): String {
        val v = minute
        var m = ""
        val h = hour
        var th = ""
        if(v < 10 ) m = "0" + v.toString()
        else m = v.toString()
        if(h < 10 ) th = "0" + h.toString()
        else th = h.toString()
        return th + ":" + m
    }

    private fun setupNumberPicker(hourPicker: NumberPicker, minutePicke: NumberPicker, text: TextView){
        hourPicker.maxValue = 23
        hourPicker.minValue = 0
        hourPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        hourPicker.value = 7
        minutePicke.maxValue = 59
        minutePicke.minValue = 0
        minutePicke.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        minutePicke.value = 0
        hourPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            text.text = getTextTime(hourPicker.value, minutePicke.value)
        }
        minutePicke.setOnValueChangedListener { numberPicker, i, i2 ->
            text.text = getTextTime(hourPicker.value, minutePicke.value)
        }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Установка времени упражнений", this::class.java.simpleName)
    }

    private fun setupUi(){
        blockMorning2.visibility = View.GONE
        blockDay2.visibility = View.GONE
        blockEvening2.visibility = View.GONE

        setupNumberPicker(morningHour, morningMinute, timeMoringText)
        setupNumberPicker(dayHour, dayMinute, timeDayText)
        setupNumberPicker(eveningHour, eveningMinute, timeEveningText)
    }
}