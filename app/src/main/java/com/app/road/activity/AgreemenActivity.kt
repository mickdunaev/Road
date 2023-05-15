package com.app.road.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import com.app.road.R
import com.app.road.v4.Utils

class AgreemenActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreemen)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        var url = intent.getStringExtra("url")
        if(url == null){
            url = "file:///android_res/raw/agreemen2.html"
        } else {
            findViewById<View>(R.id.title).visibility = View.INVISIBLE
        }
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.loadUrl(url)
        CookieManager.getInstance().setAcceptCookie(true)

    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Соглашение", this::class.java.simpleName)
    }
}