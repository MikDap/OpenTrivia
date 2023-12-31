package com.example.opentrivia

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserMethods {
    private lateinit var database: DatabaseReference

    // da chiamare prima di writeNewUser
    fun initializeDatabase() {
        database = Firebase.database.reference
    }

    fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name, email)

        database.child("users").child(userId).setValue(user)
    }
}