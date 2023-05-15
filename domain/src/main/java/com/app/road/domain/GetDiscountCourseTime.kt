package com.app.road.domain

import java.util.Date

// получить время, которое осталось до конца спецпредложения
class GetDiscountCourseTime(
    private val userRepository: UserRepository
) {

    fun invoke(course: Course): Long {
        val timeLeft = userRepository.getDiscountTimeLeft(course)

        if(timeLeft == 0L) { // если показа спецпредложения еще не было
            userRepository.setDiscountTimeLeft(course)
            return Date().time
        } else return timeLeft
    }
}