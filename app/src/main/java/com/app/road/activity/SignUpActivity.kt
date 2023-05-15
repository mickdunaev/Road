package com.app.road.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.app.road.R
import com.app.road.Repository
import com.app.road.v4.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val signin = findViewById<View>(R.id.signin)
        val signup = findViewById<View>(R.id.btnSignUp)
        val name_ui = findViewById<TextInputEditText>(R.id.name_text)
        val email_ui = findViewById<TextInputEditText>(R.id.email_text)
        val password_ui = findViewById<TextInputEditText>(R.id.password_text)
        auth = Firebase.auth
        db = Firebase.firestore
        auth.addAuthStateListener {
            if(auth.currentUser != null){
                startActivity(Intent(this, StartTestActivity::class.java))
                finish()
            }
        }


        signin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        signup.setOnClickListener {
            val name = name_ui.text.toString()
            val email = email_ui.text.toString().trim()
            val password = password_ui.text.toString()
            if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(baseContext, "Заполните все поля формы", Toast.LENGTH_SHORT).show()
            } else if(password.length < 6) {
                Toast.makeText(baseContext, "Длина пароля должна быть не менее 6", Toast.LENGTH_SHORT).show()
            } else {

                Repository.registerComplete = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser!!.uid
                            val user = hashMapOf(
                                "name" to name
                            )
                            db.collection("users")
                                .document(uid)
                                .set(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AH", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Ошибка регистрации ${task.exception!!.message}", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Регистрация", this::class.java.simpleName)
    }
}