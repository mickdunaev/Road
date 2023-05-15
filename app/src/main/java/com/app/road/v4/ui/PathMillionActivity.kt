package com.app.road.v4.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.app.road.R
import com.app.road.Repository
import com.app.road.activity.HelloActivity
import com.app.road.activity.SubscribeBaseActivity
import com.app.road.domain.Course
import com.app.road.model.Author
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PathMillionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_million)
        val auth = Firebase.auth
        val db = Firebase.firestore
        val uid = auth.currentUser!!.uid
        val user = hashMapOf(
            "select_new_course" to false,
            "select_million" to true,
        )
        val next = findViewById<TextView>(R.id.btnGetFreeDays)
        if(Repository.selectMillion){
            next.text = "Продолжить"
        }
        next.setOnClickListener {
            db.collection("users")
                .document(uid)
                .update(user as Map<String, Any>)
                .addOnCompleteListener {
                    startActivity(Intent(this, HelloActivity::class.java))
                    finish()
                }
        }
        findViewById<View>(R.id.payCourse).setOnClickListener{
            val intent = Intent(this, SubscribeBaseActivity::class.java)
            intent.putExtra("author", "Екатерина")
            intent.putExtra("course_name", "Путь на миллион")
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Инфа о курсе миллион", this::class.java.simpleName)
    }
}