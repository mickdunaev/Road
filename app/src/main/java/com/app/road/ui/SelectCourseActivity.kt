package com.app.road.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.road.R
import com.app.road.activity.SpecialOfferActivity
import com.app.road.domain.Course
import com.app.road.log
import com.app.road.v4.Utils
import com.app.road.v4.ui.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SelectCourseActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    private var clWantMillion: ConstraintLayout? = null
    private var clMoneyInHeadCourse: ConstraintLayout? = null
    private var clBodyMentalCourse: ConstraintLayout? = null
    private var clMoneyEnergyCourse: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_course)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        //clWantMillion = findViewById(R.id.clWantMillion)
        clMoneyInHeadCourse = findViewById(R.id.courseMoneyInHead)
        clBodyMentalCourse = findViewById(R.id.courseBodyAndMental)
        clMoneyEnergyCourse = findViewById(R.id.courseMoneyEnergy)

        findViewById<View>(R.id.courseWeight).setOnClickListener {
            startActivity(Intent(this, WeightStartScreenActivity::class.java))
        }

        findViewById<View>(R.id.courseMeditation).setOnClickListener {
            startActivity(Intent(this, MeditationScreenActivity::class.java))
        }

        log("${viewModel.getCurrentCourse()}")

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Выбор курса", this::class.java.simpleName)
    }

    private fun setListeners() {
        clWantMillion?.setOnClickListener {
            startActivity(Intent(this, MillionStartScreenActivity::class.java))
        }
        clMoneyInHeadCourse?.setOnClickListener {
            startActivity(Intent(this, CourseMoneyInHeadActivity::class.java))
        }
        clBodyMentalCourse?.setOnClickListener {
            startActivity(Intent(this, CourseBodyAndMentalActivity::class.java))
        }
        clMoneyEnergyCourse?.setOnClickListener {
            startActivity(Intent(this, CourseMoneyEnergyActivity::class.java))
        }
    }

    inner class CourseListener(private val course: Course) : OnClickListener {
        override fun onClick(view: View?) {
            val firstCourseOpenDate = Calendar.getInstance()
            firstCourseOpenDate.time = Date(viewModel.getDiscountTimeLeft(course)) // получаем дату, когда первый раз зашли в курс

            val dayAfterFirstOpen = Calendar.getInstance()
            dayAfterFirstOpen.time = firstCourseOpenDate.time
            dayAfterFirstOpen.add(Calendar.DAY_OF_YEAR, 1)

            if(Calendar.getInstance() <= dayAfterFirstOpen) {
                startActivity(getSpecialOfferIntent(course))
            }
        }
    }

    private fun getSpecialOfferIntent(course: Course): Intent {
        val specialOfferIntent = Intent(this, SpecialOfferActivity::class.java)
        specialOfferIntent.putExtra(SpecialOfferActivity.pickedCourseKey, course.name)
        return specialOfferIntent
    }
}