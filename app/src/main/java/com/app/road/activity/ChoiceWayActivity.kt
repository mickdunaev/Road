package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.app.road.R

class ChoiceWayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_way)

        findViewById<Button>(R.id.btnNext).setOnClickListener {
            startActivity(Intent(this, SpecialOfferActivity::class.java))
        }
        findViewById<Button>(R.id.btnToMillion).setOnClickListener {
            //startActivity(Intent(this, MillionActivity::class.java))
        }
    }
}