package com.app.road.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.domain.Course
import com.app.road.ui.MainViewModel
import com.app.road.v4.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SpecialOfferActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private var currentCourse: Course? = null

    private var tvCounter: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_offer)

        tvCounter = findViewById(R.id.tvCounter)

        currentCourse = getCourseFromIntent(intent)

        if(currentCourse != null) {
            val dateWhenTimeLeft = viewModel.getDiscountTimeLeft(currentCourse!!)
            val dayAfter = Calendar.getInstance()
            dayAfter.time = Date(dateWhenTimeLeft)
            dayAfter.add(Calendar.DAY_OF_YEAR, 1)

            setDiscountCounter(dayAfter.timeInMillis - Calendar.getInstance().timeInMillis)
        }
    }

    private fun setDiscountCounter(msCount: Long) {
        object : CountDownTimer(msCount, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tvCounter?.text = formatMillis(millisUntilFinished)
            }
            override fun onFinish() {
                tvCounter?.text = "Время вышло:("
            }
            private fun formatMillis(value: Long): String {
                var hours = (value / 1000 / 60 / 60 % 24).toString()
                var minutes = (value / 1000 / 60 % 60).toString()
                var seconds = (value / 1000 % 60).toString()

                if(minutes.length < 2) minutes = "0$minutes"
                if(seconds.length < 2) seconds = "0$seconds"

                return "$hours:$minutes:$seconds"
            }
        }.start()
    }

    private fun getCourseFromIntent(intent: Intent): Course? {
        val courseString = intent.getStringExtra(pickedCourseKey)
        if(!courseString.isNullOrEmpty()) {
            return Course.valueOf(courseString)
        } else return null
    }

    companion object {
        const val pickedCourseKey = "picked_course"
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Специальное предложение", this::class.java.simpleName)
    }
}