package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.app.road.R

class StartInfoOneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_info_one)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, ChoiceWayActivity::class.java))
        }
    }
}