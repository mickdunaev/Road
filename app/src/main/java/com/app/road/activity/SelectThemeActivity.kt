package com.app.road.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.app.road.R
import com.app.road.v4.Utils

class SelectThemeActivity : AppCompatActivity() {
    private lateinit var select_light: View
    private lateinit var unselect_light: View
    private lateinit var select_night: View
    private lateinit var unselect_night: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_theme)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        select_light = findViewById<View>(R.id.select_light)
        unselect_light = findViewById<View>(R.id.unselect_light)
        select_night = findViewById<View>(R.id.select_night)
        unselect_night = findViewById<View>(R.id.unselect_night)
        val p = getSharedPreferences("road", Context.MODE_PRIVATE)
        val night = p.getBoolean("night", false)
        if(night){
            showSelectNight()
        } else {
            showSelectLight()
        }
        unselect_light.setOnClickListener {
            showSelectLight()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
            pref.edit {
                putBoolean("night", false)
                apply()
            }

        }
        unselect_night.setOnClickListener {
            showSelectNight()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
            pref.edit {
                putBoolean("night", true)
                apply()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Выбрать тему", this::class.java.simpleName)
    }

    private fun showSelectLight(){
        select_light.visibility = View.VISIBLE
        unselect_light.visibility = View.INVISIBLE
        select_night.visibility = View.INVISIBLE
        unselect_night.visibility = View.VISIBLE
    }

    private fun showSelectNight(){
        select_light.visibility = View.INVISIBLE
        unselect_light.visibility = View.VISIBLE
        select_night.visibility = View.VISIBLE
        unselect_night.visibility = View.INVISIBLE
    }
}