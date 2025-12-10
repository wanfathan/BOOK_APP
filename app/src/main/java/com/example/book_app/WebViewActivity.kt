package com.example.book_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

// ==================================================
// WEBVIEW ACTIVITY - FOR "TRY IT" BUTTON FUNCTIONALITY
// ==================================================
class WebViewActivity : AppCompatActivity() {

    // ==================== LIFECYCLE METHODS ====================
    /**
     * Creates the WebView and loads the Goodreads URL
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create WebView
        val webView = WebView(this)
        setContentView(webView)

        // Get URL from Intent extras, fallback to Goodreads homepage
        val url = intent.getStringExtra("URL") ?: "https://www.goodreads.com/"

        // ========== WEBVIEW CONFIGURATION ==========
        webView.webViewClient = WebViewClient() // Handle links within WebView
        webView.settings.javaScriptEnabled = true // Enable JavaScript for modern websites
        webView.loadUrl(url) // Load the Goodreads page

        // Enable back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // ==================== NAVIGATION METHODS ====================
    /**
     * Handles the back/up button in the action bar
     * @return Boolean indicating if the event was handled
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Handles device back button - navigates back in WebView history if possible
     */
    override fun onBackPressed() {
        val webView = findViewById<WebView>(android.R.id.content)
        if (webView.canGoBack()) {
            webView.goBack() // Go to previous page in WebView history
        } else {
            super.onBackPressed() // Close the activity if no history
        }
    }
}