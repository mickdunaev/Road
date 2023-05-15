package com.app.road.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.road.R
import com.app.road.Repository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectCoursActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_cours)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val vasiliyCourse = findViewById<View>(R.id.vasiliyCourse)
        val lenaCourse = findViewById<View>(R.id.lena_course)
        val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
        val test = pref.getBoolean("test", false)
        val test2 = pref.getBoolean("test2", false)

        vasiliyCourse.setOnClickListener {
            startActivity(Intent(this, VasiliyProfileActivity::class.java))
         }
        lenaCourse.setOnClickListener {
            startActivity(Intent(this, LenaProfileActivity::class.java))


//            val db = Firebase.firestore
//            val auth = Firebase.auth
//            val uid = auth.currentUser!!.uid
//            val user = hashMapOf(
//                "select_new_course" to true
//            )
//
//            Repository.selectLena = true
//            db.collection("users")
//                .document(uid)
//                .update(user as Map<String, Any>)
//                .addOnCompleteListener {
//                    Repository.selectLena = true
//                    if(!test2){
//                        startActivity(Intent(this, TestLenaActivity::class.java))
//                        finish()
//                    } else {
//                        if(Repository.trialLena || Repository.premiumLena){
//                            startActivity(Intent(this, HelloActivity::class.java))
//                        } else {
//                            startActivity(Intent(this, LenaProfileActivity::class.java))
//                        }
//
//                        finish()
//                    }
//                    //startActivity(Intent(this, LenaProfileActivity::class.java))
//                    finish()
//
//                }
            //startActivity(Intent(this, TestLenaActivity::class.java))
        }
        val lenaVideo = findViewById<View>(R.id.lena_video)
        lenaVideo.setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", "http://mybestway.ru/video/intro/1.mp4")
            startActivity(intent)

        }
    }
}