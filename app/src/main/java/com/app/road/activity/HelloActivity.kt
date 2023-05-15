package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.app.road.R
import com.app.road.Repository
import com.app.road.log
import com.app.road.v4.Utils
import com.app.road.v4.ui.BaseMillionActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HelloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Repository.registerComplete = false
        val date = Date()
        val h = date.hours
        if(h >= 0 && h <= 11){
            setContentView(R.layout.activity_hello_morning)
        } else if(h > 11 && h < 17){
            setContentView(R.layout.activity_hello_day)
        } else {
            setContentView(R.layout.activity_hello_evening)
        }
        Handler().postDelayed({
            val db = Firebase.firestore
            val auth = Firebase.auth
            val uid = auth.currentUser!!.uid
            db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener {
                    val doc = it.result
                    if(doc != null){
                        val trial = doc["trial"] as Boolean? ?: false
                        val trialEkaterina = doc["trial_ekaterina"] as Boolean? ?: true
                        val trialLena = doc["trial_new_course"] as Boolean? ?: false
                        val premiumLena = doc["premium_new_course"] as Boolean? ?: false
                        val selectNewCourse = doc["select_new_course"] as Boolean? ?: false
                        val selectMillion = doc["select_million"] as Boolean? ?: false
                        val millionPremium = doc["million_premium"] as Boolean? ?: false
                        Repository.name = doc["name"] as String? ?: ""
                        val premium = doc["premium"] as Boolean? ?: false
                        Repository.selectLena = selectNewCourse
                        Repository.selectMillion = selectMillion
                        log("selected_new_course: $selectNewCourse")
                        Repository.premium = premium
                        Repository.trial = trial
                        Repository.trialLena = trialLena
                        Repository.trialEkaterina = trialEkaterina
                        Repository.premiumLena = premiumLena
                        Repository.millionPremium = millionPremium
                        if(selectMillion){
                            startActivity(Intent(this, BaseMillionActivity::class.java))
                        } else
                        if(trial || premium || trialLena || premiumLena){
                            if(!selectNewCourse){
                                startActivity(Intent(this, BaseActivity::class.java))
                            } else {
                                startActivity(Intent(this, BaseLenaActivity::class.java))
                            }

                            finish()
                        } else {
                            startActivity(Intent(this, SubscribeBaseActivity::class.java))
                            finish()
                        }
                    }
                }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Начальный экран (заставка)", this::class.java.simpleName)
    }
}