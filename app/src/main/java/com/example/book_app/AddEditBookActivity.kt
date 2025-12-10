package com.example.book_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEditBookActivity : AppCompatActivity() {

    // UI Components
    private lateinit var editTitle: EditText
    private lateinit var editAuthor: EditText
    private lateinit var editDescription: EditText
    private lateinit var editTime: EditText
    private lateinit var editUrl: EditText      // For "Try It" Web Link
    private lateinit var editImageUrl: EditText // For "Cover Image" URL
    private lateinit var imageSpinner: Spinner
    private lateinit var saveButton: Button

    // Firebase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // State
    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_book)

        // 1. Enable Back Navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2. Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 3. Bind Views
        editTitle = findViewById(R.id.editTitle)
        editAuthor = findViewById(R.id.editAuthor)
        editDescription = findViewById(R.id.editDescription)
        editTime = findViewById(R.id.editTime)
        editUrl = findViewById(R.id.editUrl)
        editImageUrl = findViewById(R.id.editImageUrl)
        imageSpinner = findViewById(R.id.imageSpinner)
        saveButton = findViewById(R.id.saveButton)

        // 4. Setup Spinner (Local Image Backup)
        val coverOptions = arrayOf("Book 1", "Book 2", "Book 3", "Book 4", "Book 5", "Book 6", "Book 7", "Book 8", "Book 9")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, coverOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageSpinner.adapter = adapter

        // 5. Check Intent (Edit Mode vs Add Mode)
        if (intent.hasExtra("BOOK_ID")) {
            bookId = intent.getStringExtra("BOOK_ID")
            editTitle.setText(intent.getStringExtra("TITLE"))
            editAuthor.setText(intent.getStringExtra("AUTHOR"))
            editDescription.setText(intent.getStringExtra("DESC"))
            editTime.setText(intent.getStringExtra("TIME"))
            editUrl.setText(intent.getStringExtra("URL"))

            // Image Logic: If it's a URL, fill the text box. If it's "bookX", we leave it to the spinner.
            val currentImageRef = intent.getStringExtra("IMAGE_REF") ?: ""
            if (currentImageRef.startsWith("http")) {
                editImageUrl.setText(currentImageRef)
            }

            saveButton.text = "Update Book"
            supportActionBar?.title = "Edit Book"
        } else {
            supportActionBar?.title = "Add New Book"
        }

        // 6. Quick Add Rule (Even ID Variation)
        if (intent.getBooleanExtra("IS_QUICK_ADD", false)) {
            editTitle.setText("New Book Entry")
            editTime.setText("1 hour")
        }

        saveButton.setOnClickListener {
            saveBook()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveBook() {
        val title = editTitle.text.toString().trim()
        val author = editAuthor.text.toString().trim()
        val description = editDescription.text.toString().trim()
        val time = editTime.text.toString().trim()
        val url = editUrl.text.toString().trim()
        val imageUrl = editImageUrl.text.toString().trim()

        // Validation
        if (title.isEmpty() || author.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill out Title, Author and Time", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be signed in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // INTELLIGENT IMAGE SELECTION
        // If the user pasted a URL, use it. Otherwise, use the Spinner value.
        val spinnerSelection = "book${imageSpinner.selectedItemPosition + 1}"
        val finalImageRef = if (imageUrl.isNotEmpty()) imageUrl else spinnerSelection

        // Create Data Map
        val book = hashMapOf(
            "title" to title,
            "subtitle" to author,
            "description" to description,
            "extraField" to time,
            "url" to url,
            "imageRef" to finalImageRef, // Can be URL or "book1"
            "ownerUid" to currentUser.uid
        )

        // Save to Firestore
        if (bookId == null) {
            firestore.collection("items").add(book)
                .addOnSuccessListener {
                    Toast.makeText(this, "Book added!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            firestore.collection("items").document(bookId!!).set(book)
                .addOnSuccessListener {
                    Toast.makeText(this, "Book updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}