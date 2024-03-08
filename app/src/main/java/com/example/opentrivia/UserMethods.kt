package com.example.opentrivia

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserMethods {
    private lateinit var database: DatabaseReference
    private var auth: FirebaseAuth = Firebase.auth

    // da chiamare prima di writeNewUser
    fun initializeDatabase() {
        database = Firebase.database.reference
    }

    fun writeNewUserOnDB(userId: String, name: String, email: String) {
        val user = User(name, email)

        database.child("users").child(userId).setValue(user)
    }

    fun checkUtenteisLoggato() : Boolean{
        if(auth.currentUser != null)
            return true
        return false
    }
}