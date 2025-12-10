package com.example.book_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.bumptech.glide.Glide // Import Glide for URL images

// ==================================================
// MAIN ACTIVITY - CATALOG SCREEN (Activity A)
// ==================================================
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabQuickAdd: FloatingActionButton
    private lateinit var adapter: BookAdapter

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Data List
    private var bookList = mutableListOf<BookModel>()
    private var filteredList = mutableListOf<BookModel>()

    // ==================== LIFECYCLE METHODS ====================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 2. Check Auth Status (Security)
        if (auth.currentUser == null) {
            // Not signed in? Send to Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 3. Setup UI
        recyclerView = findViewById(R.id.bookRecyclerView)
        fabQuickAdd = findViewById(R.id.fabQuickAdd)

        // Setup RecyclerView (Staggered Grid as per CA1)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = BookAdapter(filteredList)
        recyclerView.adapter = adapter

        // 4. Setup FAB (Quick Add - Variation Rule)
        fabQuickAdd.setOnClickListener {
            val intent = Intent(this, AddEditBookActivity::class.java)
            // Pass flag to trigger "Quick Add" logic in the next screen
            intent.putExtra("IS_QUICK_ADD", true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data every time we come back to this screen
        loadBooksFromFirestore()
    }

    // ==================== FIRESTORE METHODS ====================
    private fun loadBooksFromFirestore() {
        val user = auth.currentUser ?: return

        // Query: Get items where ownerUid matches current user
        // Sort: By 'extraField' (Time) as per Variation Rule
        firestore.collection("items")
            .whereEqualTo("ownerUid", user.uid)
            .orderBy("extraField", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                bookList.clear()
                for (document in documents) {
                    // Convert Firestore doc to BookModel
                    val book = document.toObject(BookModel::class.java)
                    // Ensure ID is set (needed for editing/deleting)
                    book.id = document.id
                    bookList.add(book)
                }
                // Update the visible list
                filterList("")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ==================== MENU & SEARCH ====================
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // SAFE SEARCH SETUP (Prevents Crash if menu is missing)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as? SearchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterList(newText)
                    return true
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, ActivityC::class.java))
                true
            }
            R.id.action_sign_out -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterList(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(bookList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (book in bookList) {
                if (book.title.lowercase().contains(lowerCaseQuery)) {
                    filteredList.add(book)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    // ==================== ADAPTER CLASS ====================
    inner class BookAdapter(private val books: List<BookModel>) :
        RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

        inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: CardView = itemView.findViewById(R.id.bookCard)
            val imageView: ImageView = itemView.findViewById(R.id.bookImage)
            val titleView: TextView = itemView.findViewById(R.id.bookTitle)
            val authorView: TextView = itemView.findViewById(R.id.bookAuthor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
            return BookViewHolder(view)
        }

        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            val book = books[position]

            holder.titleView.text = book.title
            holder.authorView.text = book.subtitle

            // --- IMAGE LOGIC (Supports URL & Local) ---
            if (book.imageRef.startsWith("http")) {
                // Case A: It is a URL -> Use Glide
                Glide.with(holder.itemView.context)
                    .load(book.imageRef)
                    .centerCrop()
                    .placeholder(R.drawable.book1)
                    .into(holder.imageView)
            } else {
                // Case B: It is a Local Resource -> Use standard way
                val imageResId = try {
                    resources.getIdentifier(book.imageRef, "drawable", packageName)
                } catch (e: Exception) {
                    R.drawable.book1
                }
                holder.imageView.setImageResource(if (imageResId != 0) imageResId else R.drawable.book1)
            }

            // Dynamic Height Logic (Visual Polish)
            val aspectRatios = listOf(1.2f, 1.5f, 1.8f)
            val aspectRatio = aspectRatios[position % 3]
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels / 2 - 32
            val calculatedHeight = (screenWidth * aspectRatio).toInt()
            holder.imageView.layoutParams.height = calculatedHeight
            holder.imageView.requestLayout()

            // Click -> Go to Details (Activity B)
            holder.cardView.setOnClickListener {
                val intent = Intent(this@MainActivity, ActivityB::class.java).apply {
                    putExtra("BOOK_ID", book.id)
                    putExtra("TITLE", book.title)
                    putExtra("AUTHOR", book.subtitle)
                    putExtra("DESC", book.description)
                    putExtra("TIME", book.extraField)

                    // PASS IMAGE_REF (String) so Activity B knows if it's a URL or "book1"
                    putExtra("IMAGE_REF", book.imageRef)

                    // PASS URL (Try It link)
                    putExtra("URL", book.url)
                }
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = books.size
    }
}