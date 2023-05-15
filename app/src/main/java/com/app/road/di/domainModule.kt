package com.app.road.di

import com.app.road.domain.GetCurrentCourseUseCase
import com.app.road.domain.GetDiscountCourseTime
import org.koin.dsl.module

val domainModule = module {
    factory {
        GetCurrentCourseUseCase(get())
    }
    factory {
        GetDiscountCourseTime(get())
    }
}