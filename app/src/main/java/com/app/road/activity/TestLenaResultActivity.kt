package com.app.road.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestLenaResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result_lena)

        val result1 = intent.getStringExtra("result1") ?: ""
        val result2 = intent.getStringExtra("result2") ?: ""
        val result3 = intent.getStringExtra("result3") ?: ""
        val auth = Firebase.auth
        val db = Firebase.firestore

        val lichnost_result_f = result1.toFloat()
        val zelania_result_f = result2.toFloat()
        val strategia_result_f = result3.toFloat()

        val lichnost_result = lichnost_result_f.toInt()
        val zelania_result = zelania_result_f.toInt()
        val strategia_result = strategia_result_f.toInt()

        val lich_color = calcResultLichnost(lichnost_result)
        val zel_color = calcResultZelaniay(zelania_result)
        val str_color = calcResultStrategia(strategia_result)
        val user = hashMapOf(
            "result1" to result1,
            "result2" to result2,
            "result3" to result3,
            "lich" to lich_color,
            "zel" to zel_color,
            "strat" to str_color
        )

        db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>)
        //val result_ui = findViewById<TextView>(R.id.result)
        val btToStart = findViewById<View>(R.id.bt_to_start)
        btToStart.setOnClickListener {
            val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
            pref.edit {
                putInt("video", 1)
                apply()

            }
            Repository.videoMode = 1

            startActivity(Intent(this, HelloActivity::class.java))
            finish()
        }
        val img_lichnost_ui = findViewById<ImageView>(R.id.imglichnost)
        val img_zelania_ui = findViewById<ImageView>(R.id.imgzelaniay)
        val img_strategiay_ui = findViewById<ImageView>(R.id.imgstrategiay)
        cardViewColor(img_lichnost_ui, lich_color)
        cardViewColor(img_zelania_ui, zel_color)
        cardViewColor(img_strategiay_ui, str_color)
        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
        pref.edit {
            putBoolean("test2", true)
            apply()

        }

    }

    fun cardViewColor(img: ImageView, mode: Int){
        if(mode == 0){
            img.setImageDrawable(getDrawable(R.drawable.green_test_result_bg))
        }else if(mode == 1){
            img.setImageDrawable(getDrawable(R.drawable.yellow_test_result_bg))
        } else {
            img.setImageDrawable(getDrawable(R.drawable.red_test_result_bg))
        }
    }

    // 0 - зеленый
    // 1 - желтый
    // 2 - красный
    fun calcResultLichnost(r: Int): Int{
        var ret = 0
        if(r <= 15){
            ret = 0
        } else if(r < 50) {
            ret = 1
        } else {
            ret = 2
        }
        return ret
    }
    fun calcResultZelaniay(r: Int): Int{
        var ret = 0
        if(r <= 15){
            ret = 0
        } else if(r < 50) {
            ret = 1
        } else {
            ret = 2
        }
        return ret
    }
    fun calcResultStrategia(r: Int): Int{
        var ret = 0
        if(r <=50){
            ret = 2
        } else if(r <= 85){
            ret = 1
        } else {
            ret = 0
        }

        return ret
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Результат теста Екатерины", this::class.java.simpleName)
    }
}