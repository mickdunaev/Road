package com.app.road

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

object Repository {
    var selectLena = false
    var selectMillion = false
    var registerComplete = false
    var trial = false
    var trialLena = false
    var trialMillion = true
    var trialEkaterina = false
    var premium = false
    var premiumLena = false
    var name = ""
    var email = ""
    var phone = ""
    var day = 1L
    var dayLena = 1L
    var dayMillion = 1L
    var rating = "0.0"
    var notify = ArrayList<String>()
    var audioText = ""
    var videoMode = 0
    var millionPremium = false
    var nextStage = false
    val payList = ArrayList<Long>()
    public fun saveLesson(name: String, content: String, link: String){
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.uid
        val cal = Calendar.getInstance()
        val timestamp = cal.timeInMillis
        val currYear = cal.get(Calendar.YEAR)
        val currMonth = cal.get(Calendar.MONTH) + 1
        val currDay = cal.get(Calendar.DAY_OF_MONTH)
        val currHour = cal.get(Calendar.HOUR)
        val currMinute = cal.get(Calendar.MINUTE)
        var moth = currMonth.toString()
        if(currMonth < 10){
            moth = "0$currMonth"
        }

        val date = "$currDay/$moth/$currYear $currHour:$currMinute"
        val lesson = hashMapOf(
            "uid" to uid,
            "name" to name,
            "content" to content,
            "date" to date,
            "link" to link,
            "timestamp" to timestamp,
        )
        db.collection("lessons").document().set(lesson)
    }
}