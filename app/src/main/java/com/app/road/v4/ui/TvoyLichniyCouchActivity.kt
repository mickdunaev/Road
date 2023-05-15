package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.app.road.R
import com.app.road.activity.ChoiceWayActivity
import com.app.road.activity.SelectCoursActivity
import com.app.road.ui.SelectCourseActivity
import com.app.road.v4.Utils

class TvoyLichniyCouchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tvoy_lichniy_couch)
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startActivity(Intent(this, SelectCourseActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Твой личный коуч", this::class.java.simpleName)
    }
}