package com.example.book_app

import com.google.firebase.firestore.DocumentId

// ==================================================
// DATA MODEL - FIRESTORE BOOK ITEM
// ==================================================
/**
 * Represents a book document in the 'items' collection in Firestore.
 * Matches the CA2 requirements: title, subtitle, description, imageRef, extraField, ownerUid.
 */
data class BookModel(
    @DocumentId
    var id: String = "", // Firestore Document ID (auto-generated or set)

    var title: String = "",
    var subtitle: String = "", // Used for Author
    var description: String = "",
    var url: String = "",

    // Stores the NAME of the drawable (e.g., "book1"), not the ID (Int)
    var imageRef: String = "book1",

    // CA2 Requirement: extraField (number | string)
    // We use this for 'Time' based on your Variation Code
    var extraField: String = "",

    // CA2 Requirement: ownerUid
    // Ensures users only see their own data
    var ownerUid: String = ""
) {
    // Empty constructor required by Firestore for deserialization
    constructor() : this("", "", "", "", "book1", "", "", "")
}