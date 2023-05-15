package com.app.road

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import com.app.road.di.appModule
import com.app.road.di.dataModule
import com.app.road.di.domainModule
import com.google.firebase.FirebaseApp
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.onesignal.OneSignal
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

const val ONESIGNAL_APP_ID = "71b73218-0e0a-4339-9fc5-e5420351844b"
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        activateAppMetrica()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, domainModule, dataModule)
        }
    }

    private fun activateAppMetrica() {
        val appMetricaConfig: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder("20697d5d-9521-472a-8a0f-5fb5eac4115c")
                .handleFirstActivationAsUpdate(isFirstActivationAsUpdate())
                .withLocationTracking(true)
                .withStatisticsSending(true)
                .build()
        YandexMetrica.activate(applicationContext, appMetricaConfig)
    }

    private fun isFirstActivationAsUpdate(): Boolean {
        // Implement logic to detect whether the app is opening for the first time.
        // For example, you can check for files (settings, databases, and so on),
        // which the app creates on its first launch.
//        throw Exception("An operation is not implemented.")
        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
        var first = pref.getBoolean("first", true)
        if(first) {
            first = false
            pref.edit {
                putBoolean("first", false)
                apply()
            }
        }
        return first
    }
}