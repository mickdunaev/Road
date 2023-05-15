package com.app.road.ui

import androidx.lifecycle.ViewModel
import com.app.road.domain.Course
import com.app.road.domain.GetCurrentCourseUseCase
import com.app.road.domain.GetDiscountCourseTime
import java.util.*

class MainViewModel(
    private val getCurrentCourseUseCase: GetCurrentCourseUseCase,
    private val getDiscountCourseTime: GetDiscountCourseTime
) : ViewModel() {

    fun getCurrentCourse(): Course? {
        return getCurrentCourseUseCase.invoke()
    }

    fun getDiscountTimeLeft(course: Course): Long {
        return getDiscountCourseTime.invoke(course)
    }
}