package com.app.road.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R
import com.app.road.v4.Utils

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signin = findViewById<View>(R.id.btnSignIn)
        val signup = findViewById<View>(R.id.btnSignUp)
        signin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Авторизация", this::class.java.simpleName)
    }
}