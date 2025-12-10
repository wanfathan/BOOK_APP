package com.example.book_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

// ==================================================
// ACTIVITY B - BOOK DETAILS SCREEN
// ==================================================
class ActivityB : AppCompatActivity() {

    // ==================== LIFECYCLE METHODS ====================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)

        // Enable back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ========== GET DATA FROM INTENT ==========
        // Retrieve book data passed from MainActivity via Intent extras
        val bookId = intent.getIntExtra("BOOK_ID", 0)
        val titleResId = intent.getIntExtra("TITLE_RES_ID", 0)
        val authorResId = intent.getIntExtra("AUTHOR_RES_ID", 0)
        val imageResId = intent.getIntExtra("IMAGE_RES_ID", 0)
        val descResId = intent.getIntExtra("DESC_RES_ID", 0)
        val timeResId = intent.getIntExtra("TIME_RES_ID", 0)

        // ========== INITIALIZE VIEWS ==========
        // Find all views from the layout
        val bookImage: ImageView = findViewById(R.id.detailBookImage)
        val bookTitle: TextView = findViewById(R.id.detailBookTitle)
        val bookAuthor: TextView = findViewById(R.id.detailBookAuthor)
        val bookTime: TextView = findViewById(R.id.detailBookTime)
        val bookDesc: TextView = findViewById(R.id.detailBookDesc)
        val tryItButton: Button = findViewById(R.id.tryItButton)
        val shareButton: Button = findViewById(R.id.shareButton)

        // ========== SET BOOK DATA TO VIEWS ==========
        // Display the book information in the views
        bookImage.setImageResource(imageResId)
        bookTitle.setText(titleResId)
        bookAuthor.setText(authorResId)
        bookDesc.setText(descResId)

        // Variation Code: Time Field - Display reading time with label
        val timeText = "${getString(R.string.time_label)} ${getString(timeResId)}"
        bookTime.text = timeText

        // ========== ACCESSIBILITY ==========
        // Set content description for TalkBack support
        val bookTitleText = getString(titleResId)
        bookImage.contentDescription = "Cover of $bookTitleText"

        // ========== BUTTON CLICK LISTENERS ==========

        //"Try it" Button - Opens Goodreads in WebView
        tryItButton.setOnClickListener {
            val goodreadsUrl = getGoodreadsUrl(bookId)
            val intent = Intent(this@ActivityB, WebViewActivity::class.java).apply {
                putExtra("URL", goodreadsUrl)
            }
            startActivity(intent)
        }

        // Share Button - Implicit Intent for sharing book title
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this book: ${getString(titleResId)}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share this book via"))
        }
    }

    // ==================== HELPER METHODS ====================
    /**
     * Returns Goodreads URL based on book ID
     * @param bookId The unique identifier of the book
     * @return Goodreads URL string for the specific book
     */
    private fun getGoodreadsUrl(bookId: Int): String {
        return when (bookId) {
            1 -> "https://www.goodreads.com/book/show/48855.The_Diary_of_a_Young_Girl"
            2 -> "https://www.goodreads.com/book/show/4865.How_to_Win_Friends_and_Influence_People"
            3 -> "https://www.goodreads.com/book/show/18144590-the-alchemist"
            4 -> "https://www.goodreads.com/book/show/30659.Meditations"
            5 -> "https://www.goodreads.com/book/show/43877.The_Monk_Who_Sold_His_Ferrari"
            6 -> "https://www.goodreads.com/book/show/6900.Tuesdays_with_Morrie"
            7 -> "https://www.goodreads.com/book/show/12321.Beyond_Good_and_Evil"
            8 -> "https://www.goodreads.com/book/show/97411.Letters_from_a_Stoic"
            9 -> "https://www.goodreads.com/book/show/170448.Animal_Farm"
            else -> "https://www.goodreads.com/"
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