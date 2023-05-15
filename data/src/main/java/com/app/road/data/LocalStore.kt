package com.app.road.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.app.road.domain.Course
import com.app.road.domain.UserRepository
import java.util.*

class LocalStore(
    private val sharedPreferences: SharedPreferences
) {

    fun setCurrentCourse(course: Course) {
        sharedPreferences.edit {
            putString(currentCourseKey, course.name)
            apply()
        }
    }
    fun getCurrentCourse(): Course? {
        val stringValue = sharedPreferences.getString(currentCourseKey, "")
        if(!stringValue.isNullOrEmpty()) {
            return Course.valueOf(stringValue)
        } else return null
    }

    fun getDiscountTimeLeft(course: Course): Long {
        return when(course) {
            Course.MONEY_IN_THE_HEAD -> sharedPreferences.getLong(moneyHeadCourseDiscountKey, 0)
            Course.BODY_AND_MENTAL -> sharedPreferences.getLong(bodyMentalCourseDiscountKey, 0)
            Course.MONEY_ENERGY -> sharedPreferences.getLong(moneyEnergyDiscountKey, 0)
            Course.WANT_MILLION -> sharedPreferences.getLong(wantMillionDiscountKey, 0)
        }
    }
    fun setDiscountTimeLeft(course: Course) {
        sharedPreferences.edit {
            when(course) { // ставим текущее время
                Course.MONEY_IN_THE_HEAD -> putLong(moneyHeadCourseDiscountKey, Date().time)
                Course.BODY_AND_MENTAL -> putLong(bodyMentalCourseDiscountKey, Date().time)
                Course.MONEY_ENERGY -> putLong(moneyEnergyDiscountKey, Date().time)
                Course.WANT_MILLION -> putLong(wantMillionDiscountKey, Date().time)
            }
            apply()
        }
    }

    companion object {
        const val currentCourseKey = "current_course"
        const val moneyHeadCourseDiscountKey = "money_in_head_discount"
        const val bodyMentalCourseDiscountKey = "body_mental_discount"
        const val moneyEnergyDiscountKey = "money_energy_discount"
        const val wantMillionDiscountKey = "want_million_discount"
    }

}