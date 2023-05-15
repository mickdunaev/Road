package com.app.road.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.app.road.R

class BlogActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private val baseUrl = "https://zen.yandex.ru/id/628ce75594aa827132b0ae65"
    private var firstLoad = true
    private var currentUrl = baseUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)
        val back = findViewById<View>(R.id.back)
        back.setOnClickListener {
            finish()
        }
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                //здесь url на который происходит переход
                view.loadUrl(request.url.toString())
                currentUrl = request.url.toString()
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //здесь url на который происходит переход
                view.loadUrl(url)
                currentUrl = url
                return false
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                progressBar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if(firstLoad){
                    progressBar.visibility = View.VISIBLE
                    firstLoad = false
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                progressBar.visibility = View.GONE
            }
        }
        webView.webViewClient = webViewClient
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.loadUrl(baseUrl)
        CookieManager.getInstance().setAcceptCookie(true);
    }

}