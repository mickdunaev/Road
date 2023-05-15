package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AutorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autor)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        val auth = Firebase.auth
        val db = Firebase.firestore

        val numBye = findViewById<TextView>(R.id.num_bye)
        val numTrial = findViewById<TextView>(R.id.num_trial)
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .addSnapshotListener { value, error ->
                if (value != null && error == null) {
                    val doc = value
                    val bye = doc["buy_count"] as Long? ?: 0L
                    val trial = doc["trial_count"] as Long? ?: 0L
                    numBye.text = bye.toString()
                    numTrial.text = trial.toString()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Статистика автора", this::class.java.simpleName)
    }
}