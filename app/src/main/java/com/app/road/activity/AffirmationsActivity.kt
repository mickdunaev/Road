package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils

class AffirmationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affirmations)
        val trial = findViewById<View>(R.id.trial)
        val back = findViewById<View>(R.id.back)
        if(Repository.trial) trial.visibility = View.VISIBLE
        else trial.visibility = View.INVISIBLE

        back.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Аффирмации", this::class.java.simpleName)
    }
}