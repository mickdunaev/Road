package com.app.road.data

import com.app.road.domain.Course
import com.app.road.domain.UserRepository
import java.util.*

class UserRepositoryIml(
    private val localStore: LocalStore
) : UserRepository {

    override fun getCurrentCourse(): Course? {
        return localStore.getCurrentCourse()
    }

    override fun getDiscountTimeLeft(course: Course): Long {
        return localStore.getDiscountTimeLeft(course = course)
    }

    override fun setDiscountTimeLeft(course: Course) {
        localStore.setDiscountTimeLeft(course)
    }
}