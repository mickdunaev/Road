package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var avatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        avatar = findViewById(R.id.avatar)
        val getPhoto = findViewById<View>(R.id.get_photo)
        getPhoto.setOnClickListener {
            setAvatar()
        }
        val name_ui = findViewById<EditText>(R.id.name)
        val email_ui = findViewById<EditText>(R.id.email)
        val phone_ui = findViewById<EditText>(R.id.phone)
        name_ui.setText(Repository.name)
        email_ui.setText(Repository.email)
        phone_ui.setText(Repository.phone)

        val verifyEmail = findViewById<View>(R.id.verify_email)
        val verifyPhone = findViewById<View>(R.id.verify_phone)
        val verifyAll = findViewById<View>(R.id.verify_all)
        verifyEmail.visibility = View.INVISIBLE
        verifyPhone.visibility = View.INVISIBLE
        verifyAll.visibility = View.INVISIBLE
        val auth = Firebase.auth
        val db = Firebase.firestore
        db.collection("users").document(auth.currentUser!!.uid).get().addOnCompleteListener {
            val doc = it.result
            if(doc != null){
                val name = doc["name"] as String? ?: ""
                val email = doc["email"] as String? ?: ""
                val phone = doc["phone"] as String? ?: ""
                val verify_email = doc["verify_email"] as Boolean? ?: false
                val verify_phone = doc["verify_phone"] as Boolean? ?: false
                name_ui.setText(name)
                email_ui.setText(email)
                phone_ui.setText(phone)
                if(verify_email) verifyEmail.visibility = View.VISIBLE
                if(verify_phone) verifyPhone.visibility = View.VISIBLE
                if(verify_email && verify_phone) verifyAll.visibility = View.VISIBLE
            }
        }
        val save = findViewById<View>(R.id.save)
        save.setOnClickListener {
            val user = hashMapOf(
                "name" to name_ui.text.toString(),
                "email" to email_ui.text.toString(),
                "phone" to phone_ui.text.toString()
            )

            db.collection("users").document(auth.currentUser!!.uid).update(user as Map<String, Any>).addOnCompleteListener {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Редактировать профиль", this::class.java.simpleName)
    }

    private fun setAvatar(){
        Toast.makeText(this,"Задать фотографию", Toast.LENGTH_SHORT).show()
    }
}