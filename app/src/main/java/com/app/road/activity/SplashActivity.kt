package com.app.road.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.app.road.R
import com.app.road.Repository
import com.app.road.log
import com.app.road.v4.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, 100)
            param(FirebaseAnalytics.Param.ITEM_NAME, "Запуск приложения")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }

        log("splash onCreate")
        val p = getSharedPreferences("road", Context.MODE_PRIVATE)
        val night = p.getBoolean("night", false)
        if(night){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        listener = FirebaseAuth.AuthStateListener {
            if(!Repository.registerComplete) if(auth.currentUser != null){
                val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
                val test = pref.getBoolean("test", false)
                val test2 = pref.getBoolean("test2", false)
                Repository.videoMode = pref.getInt("video", 0)
                log("splash: video mode = ${Repository.videoMode}")
                if(test || test2 || Repository.videoMode == 2 || Repository.videoMode == 1) {
                    startActivity(Intent(this, HelloActivity::class.java))
                    auth.removeAuthStateListener(listener)
                    finish()
                } else {
                    startActivity(Intent(this, StartTestActivity::class.java))
                    auth.removeAuthStateListener(listener)
                    finish()
                }

            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        val screen = findViewById<View>(R.id.screen)
        screen.setOnClickListener {
            auth.addAuthStateListener(listener!!)
        }
        Handler().postDelayed(Runnable {
            auth.addAuthStateListener(listener!!)
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Начальный экран", this::class.java.simpleName)
    }
}