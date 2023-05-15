package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.app.road.R
import com.app.road.v4.Utils

class TestResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)

        val result = intent.getStringExtra("result") ?: ""
        val result_ui = findViewById<TextView>(R.id.result)
        val btToStart = findViewById<View>(R.id.bt_to_start)
        val btToTime = findViewById<View>(R.id.bt_to_time)
        result_ui.text = "$result/10"
        btToStart.setOnClickListener {
            startActivity(Intent(this, HelloActivity::class.java))
            finish()
        }
        btToTime.setOnClickListener {
            startActivity(Intent(this, HelloActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Результат теста", this::class.java.simpleName)
    }
}