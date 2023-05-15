package com.app.road.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.Repository
import com.app.road.model.Author
import com.app.road.v4.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signin = findViewById<View>(R.id.signin)
        val signup = findViewById<View>(R.id.signup)
        val email_ui = findViewById<TextInputEditText>(R.id.email_text)
        val password_ui = findViewById<TextInputEditText>(R.id.password_text)
        auth = Firebase.auth
        findViewById<View>(R.id.remember).setOnClickListener {
            val builder: AlertDialog.Builder =  AlertDialog.Builder(this)
            builder.setTitle("Ваша почта")
            val input = EditText(this)
            input.setInputType(InputType.TYPE_CLASS_TEXT)
            builder.setView(input)
            builder.setPositiveButton("Отправить") { dialog, which ->
                val em = input.text.toString()
                auth.sendPasswordResetEmail(em).addOnCompleteListener {
                    Toast.makeText(this, "Проверьте почту", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }
        auth.addAuthStateListener {
            if (auth.currentUser != null) {
                val pref = getSharedPreferences("road", Context.MODE_PRIVATE)
                val test = pref.getBoolean("test", false)
                if (test) {
                    startActivity(Intent(this, HelloActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, StartTestActivity::class.java))
                    finish()
                }
            }
        }

        signin.setOnClickListener {
            val email = email_ui.text.toString().trim()
            val password = password_ui.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(baseContext, "Заполните все поля формы", Toast.LENGTH_SHORT).show()
            } else if(password.length < 6) {
                Toast.makeText(baseContext, "Длина пароля должна быть не менее 6", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                    } else {
                        Toast.makeText(baseContext, "Ошибка авторизации ${task.exception!!.message}",Toast.LENGTH_SHORT).show()
                    }
                }

            }
                Repository.registerComplete = true

        }

        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Вход в аккаунт", this::class.java.simpleName)
    }
}