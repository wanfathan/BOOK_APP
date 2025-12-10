package com.example.book_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView

// ==================================================
// ACTIVITY C - ABOUT/STYLE SCREEN
// ==================================================
class ActivityC : AppCompatActivity() {

    // ==================== LIFECYCLE METHODS ====================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c)

        // Enable back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ========== INITIALIZE VIEWS ==========
        // Find all views from the layout
        val appNameText: TextView = findViewById(R.id.appNameText)
        val yourNameText: TextView = findViewById(R.id.yourNameText)
        val variationCodeText: TextView = findViewById(R.id.variationCodeText)
        val largeTextSwitch: Switch = findViewById(R.id.largeTextSwitch)
        val wanDecText: TextView = findViewById(R.id.wanDecText) // Personal message text

        // ========== TEXT SIZE TOGGLE FUNCTIONALITY ==========
        // Runtime UI-only toggle that affects current session without saving
        largeTextSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newTextSize = if (isChecked) 30f else 18f // Large vs Normal size

            // Apply new text size to all text elements
            appNameText.textSize = newTextSize
            yourNameText.textSize = newTextSize
            variationCodeText.textSize = newTextSize
            wanDecText.textSize = newTextSize


        }
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
}