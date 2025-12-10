package com.example.book_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide

// ==================================================
// ACTIVITY B - BOOK DETAILS SCREEN
// ==================================================
class ActivityB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Book Details"

        // ========== GET DATA FROM INTENT ==========
        val bookId = intent.getStringExtra("BOOK_ID")
        val title = intent.getStringExtra("TITLE") ?: "No Title"
        val author = intent.getStringExtra("AUTHOR") ?: "Unknown Author"
        val desc = intent.getStringExtra("DESC") ?: "No description available."
        val time = intent.getStringExtra("TIME") ?: ""
        val bookUrl = intent.getStringExtra("URL") ?: ""

        // Get Image Reference (String) - Can be URL or "book1"
        val imageRef = intent.getStringExtra("IMAGE_REF") ?: "book1"

        // ========== INITIALIZE VIEWS ==========
        val bookImage: ImageView = findViewById(R.id.detailBookImage)
        val bookTitle: TextView = findViewById(R.id.detailBookTitle)
        val bookAuthor: TextView = findViewById(R.id.detailBookAuthor)
        val bookTime: TextView = findViewById(R.id.detailBookTime)
        val bookDesc: TextView = findViewById(R.id.detailBookDesc)

        // Buttons
        val tryItButton: Button = findViewById(R.id.tryItButton)
        val shareButton: Button = findViewById(R.id.shareButton)
        val editButton: Button = findViewById(R.id.editButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        // ========== SET DATA ==========
        bookTitle.text = title
        bookAuthor.text = author
        bookDesc.text = desc
        bookTime.text = "Reading Time: $time"

        // --- INTELLIGENT IMAGE LOADING ---
        if (imageRef.startsWith("http")) {
            // It's a URL - Use Glide
            Glide.with(this)
                .load(imageRef)
                .fitCenter()
                .placeholder(R.drawable.book1)
                .into(bookImage)
        } else {
            // It's a local file name (book1, etc.)
            val imageResId = try {
                resources.getIdentifier(imageRef, "drawable", packageName)
            } catch (e: Exception) {
                R.drawable.book1
            }
            bookImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.book1)
        }

        // Accessibility
        bookImage.contentDescription = "Cover of $title"

        // ========== BUTTON LISTENERS ==========

        // 1. "Try it" Button (Web Link)
        tryItButton.setOnClickListener {
            // Use the book's specific URL if available, otherwise search Goodreads
            val finalUrl = if (bookUrl.isNotEmpty()) bookUrl else "https://www.goodreads.com/search?q=$title"

            val intent = Intent(this@ActivityB, WebViewActivity::class.java).apply {
                putExtra("URL", finalUrl)
            }
            startActivity(intent)
        }

        // 2. Share Button
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this book: $title by $author")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share this book via"))
        }

        // 3. Edit Button (Update)
        editButton.setOnClickListener {
            val intent = Intent(this, AddEditBookActivity::class.java).apply {
                putExtra("BOOK_ID", bookId)
                putExtra("TITLE", title)
                putExtra("AUTHOR", author)
                putExtra("DESC", desc)
                putExtra("TIME", time)
                putExtra("URL", bookUrl)
                putExtra("IMAGE_REF", imageRef)
            }
            startActivity(intent)
            finish()
        }

        // 4. Delete Button (Remove)
        deleteButton.setOnClickListener {
            if (bookId != null) {
                AlertDialog.Builder(this)
                    .setTitle("Delete Book")
                    .setMessage("Are you sure you want to delete this book? This cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("items").document(bookId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Book Deleted Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error deleting: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Error: Cannot delete (No ID found)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}