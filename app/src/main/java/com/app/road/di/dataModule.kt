package com.app.road.di

import android.content.Context
import android.content.SharedPreferences
import com.app.road.data.LocalStore
import com.app.road.data.UserRepositoryIml
import com.app.road.domain.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("road", Context.MODE_PRIVATE)
    }
    single<LocalStore> {
        LocalStore(get())
    }
    single<UserRepository> {
        UserRepositoryIml(get())
    }
}