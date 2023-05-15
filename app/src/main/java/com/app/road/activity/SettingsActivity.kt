package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.road.R
import com.app.road.v4.Utils

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val sex = findViewById<View>(R.id.sex)
        val time = findViewById<View>(R.id.time)
        val notify = findViewById<View>(R.id.notify)
        val theme = findViewById<View>(R.id.theme)

        sex.setOnClickListener {
            startActivity(Intent(this, SelectSexActivity::class.java))
        }
        time.setOnClickListener {
            startActivity(Intent(this, SelectTimeActivity::class.java))
        }
        notify.setOnClickListener {
            startActivity(Intent(this, SelectNotifyActivity::class.java))
        }
        theme.setOnClickListener {
            startActivity(Intent(this, SelectThemeActivity::class.java))
        }
        //theme.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Настройки", this::class.java.simpleName)
    }
}