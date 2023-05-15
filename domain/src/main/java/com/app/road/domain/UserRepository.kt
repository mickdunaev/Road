package com.app.road.domain

import java.util.*

interface UserRepository {
    fun getCurrentCourse(): Course?
    fun getDiscountTimeLeft(course: Course): Long
    fun setDiscountTimeLeft(course: Course)
}