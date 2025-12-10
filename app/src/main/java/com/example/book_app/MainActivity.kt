package com.example.book_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

// ==================================================
// MAIN ACTIVITY - CATALOG SCREEN (Activity A)
// ==================================================
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    // ==================== LIFECYCLE METHODS ====================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView for book grid
        recyclerView = findViewById(R.id.bookRecyclerView)

        // Set up Staggered Grid Layout (2 columns)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Create adapter with book data and set it to RecyclerView
        val adapter = BookAdapter(getBookList())
        recyclerView.adapter = adapter
    }

    // ==================== MENU METHODS ====================
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu (three dots) for About screen access
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        // Handle menu item clicks
        return when (item.itemId) {
            R.id.action_about -> {
                // Navigate to About Screen (Activity C)
                val intent = Intent(this, ActivityC::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ==================== DATA METHODS ====================
    /**
     * Creates and returns the list of books for the catalog
     * Each book contains references to string and drawable resources
     */
    private fun getBookList(): List<Book> {
        return listOf(
            Book(1, R.string.book1_title, R.string.book1_author, R.drawable.book1, R.string.book1_desc, R.string.book1_time),
            Book(2, R.string.book2_title, R.string.book2_author, R.drawable.book2, R.string.book2_desc, R.string.book2_time),
            Book(3, R.string.book3_title, R.string.book3_author, R.drawable.book3, R.string.book3_desc, R.string.book3_time),
            Book(4, R.string.book4_title, R.string.book4_author, R.drawable.book4, R.string.book4_desc, R.string.book4_time),
            Book(5, R.string.book5_title, R.string.book5_author, R.drawable.book5, R.string.book5_desc, R.string.book5_time),
            Book(6, R.string.book6_title, R.string.book6_author, R.drawable.book6, R.string.book6_desc, R.string.book6_time),
            Book(7, R.string.book7_title, R.string.book7_author, R.drawable.book7, R.string.book7_desc, R.string.book7_time),
            Book(8, R.string.book8_title, R.string.book8_author, R.drawable.book8, R.string.book8_desc, R.string.book8_time),
            Book(9, R.string.book9_title, R.string.book9_author, R.drawable.book9, R.string.book9_desc, R.string.book9_time)
        )
    }

    // ==================== DATA CLASSES ====================
    /**
     * Data class representing a single book in the catalog
     *  id Unique identifier for the book
     *  titleResId String resource ID for book title
     *  authorResId String resource ID for author name
     *  imageResId Drawable resource ID for book cover
     *  descResId String resource ID for book description
     * timeResId String resource ID for reading time - Variation Code: Time Field
     */
    data class Book(
        val id: Int,
        val titleResId: Int,
        val authorResId: Int,
        val imageResId: Int,
        val descResId: Int,
        val timeResId: Int
    )

    // ==================== ADAPTER CLASS ====================
    /**
     * RecyclerView Adapter for displaying books in a staggered grid
     * Handles creating views and binding data to each book card
     */
    inner class BookAdapter(private val books: List<Book>) :
        RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

        // ==================== VIEW HOLDER ====================
        /**
         * ViewHolder class that holds references to the views in each book card
         */
        inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: CardView = itemView.findViewById(R.id.bookCard)
            val imageView: ImageView = itemView.findViewById(R.id.bookImage)
            val titleView: TextView = itemView.findViewById(R.id.bookTitle)
            val authorView: TextView = itemView.findViewById(R.id.bookAuthor)
        }

        // ==================== ADAPTER METHODS ====================
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            // Inflate the book item layout and create a new ViewHolder
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
            return BookViewHolder(view)
        }

        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            val book = books[position]

            // ========== SET BASIC BOOK DATA ==========
            holder.imageView.setImageResource(book.imageResId)
            holder.titleView.setText(book.titleResId)
            holder.authorView.setText(book.authorResId)

            // ========== STAGGERED GRID HEIGHTS ==========
            //Staggered Grid Layout - Creates different heights for visual interest
            val aspectRatios = listOf(1.2f, 1.5f, 1.8f) // Different aspect ratios for varied heights
            val aspectRatio = aspectRatios[position % 3]

            // Calculate height based on screen width and aspect ratio
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels / 2 - 32 // 2 columns with margins
            val calculatedHeight = (screenWidth * aspectRatio).toInt()

            // Apply calculated height to image view
            val layoutParams = holder.imageView.layoutParams
            layoutParams.height = calculatedHeight
            holder.imageView.layoutParams = layoutParams
            holder.imageView.requestLayout()

            // ========== ACCESSIBILITY ==========
            // Set content description for TalkBack support
            val bookTitle = getString(book.titleResId)
            holder.imageView.contentDescription = "Cover of $bookTitle"

            // ========== CLICK LISTENER ==========
            // Handle book card clicks - navigate to Details screen (Activity B)
            holder.cardView.setOnClickListener {
                val intent = Intent(this@MainActivity, ActivityB::class.java).apply {
                    // Pass all book data via Intent extras
                    putExtra("BOOK_ID", book.id)
                    putExtra("TITLE_RES_ID", book.titleResId)
                    putExtra("AUTHOR_RES_ID", book.authorResId)
                    putExtra("IMAGE_RES_ID", book.imageResId)
                    putExtra("DESC_RES_ID", book.descResId)
                    putExtra("TIME_RES_ID", book.timeResId)
                }
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = books.size
    }
}