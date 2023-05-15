package com.app.road.domain

class GetCurrentCourseUseCase(
    private val userRepository: UserRepository
) {
    fun invoke() = userRepository.getCurrentCourse()
}