package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.road.R
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectSexActivity : AppCompatActivity() {
    private var isMan = true;

    private lateinit var selectMan: View
    private lateinit var unselectMan: View
    private lateinit var selectWoman: View
    private lateinit var unselectWoman: View
    private lateinit var icon_selectMan: View
    private lateinit var icon_unselectMan: View
    private lateinit var icon_selectWoman: View
    private lateinit var icon_unselectWoman: View
    private lateinit var text_selectMan: View
    private lateinit var text_unselectMan: View
    private lateinit var text_selectWoman: View
    private lateinit var text_unselectWoman: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sex)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        selectMan = findViewById(R.id.select_man)
        icon_selectMan = findViewById(R.id.icon_select_man)
        text_selectMan = findViewById(R.id.text_select_man)
        icon_unselectMan = findViewById(R.id.icon_unselect_man)
        text_unselectMan = findViewById(R.id.text_unselect_man)
        unselectMan = findViewById(R.id.unselect_man)
        selectWoman = findViewById(R.id.select_woman)
        unselectWoman = findViewById(R.id.unselect_woman)
        icon_selectWoman = findViewById(R.id.icon_select_woman)
        text_selectWoman = findViewById(R.id.text_select_woman)
        icon_unselectWoman = findViewById(R.id.icon_unselect_woman)
        text_unselectWoman = findViewById(R.id.text_unselect_woman)
        val auth = Firebase.auth
        val db = Firebase.firestore

        unselectMan.setOnClickListener {
            isMan = true
            updateUi()
            val user = hashMapOf(
                "is_man" to isMan
            )
            db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>).addOnCompleteListener {}
        }
        unselectWoman.setOnClickListener {
            isMan = false
            updateUi()
            val user = hashMapOf(
                "is_man" to isMan
            )
            db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>).addOnCompleteListener {}
        }
        updateUi()
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if (doc != null) {
                isMan = doc["is_man"] as Boolean? ?: true
                updateUi()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Выбрать пол", this::class.java.simpleName)
    }

    private fun updateUi(){
        if(isMan){
            selectMan.visibility = View.VISIBLE
            icon_selectMan.visibility = View.VISIBLE
            text_selectMan.visibility = View.VISIBLE
            unselectMan.visibility = View.GONE
            icon_unselectMan.visibility = View.GONE
            text_unselectMan.visibility = View.GONE
            selectWoman.visibility = View.GONE
            icon_selectWoman.visibility = View.GONE
            text_selectWoman.visibility = View.GONE
            unselectWoman.visibility = View.VISIBLE
            icon_unselectWoman.visibility = View.VISIBLE
            text_unselectWoman.visibility = View.VISIBLE
        } else {
            selectMan.visibility = View.GONE
            icon_selectMan.visibility = View.GONE
            text_selectMan.visibility = View.GONE
            unselectMan.visibility = View.VISIBLE
            icon_unselectMan.visibility = View.VISIBLE
            text_unselectMan.visibility = View.VISIBLE
            selectWoman.visibility = View.VISIBLE
            icon_selectWoman.visibility = View.VISIBLE
            text_selectWoman.visibility = View.VISIBLE
            unselectWoman.visibility = View.GONE
            icon_unselectWoman.visibility = View.GONE
            text_unselectWoman.visibility = View.GONE
        }
    }
}