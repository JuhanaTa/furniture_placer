package com.example.furniture_placer

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.inject.Deferred
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class firebaseService {
    val db = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createPersonalStorage(user: FirebaseUser) {
        if (user == null) return
        val doc = db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    // Create a new user
                    val userData = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email,
                        "uid" to user.uid,
                        "lastLogin" to Timestamp(System.currentTimeMillis())
                    )

                    // Add a new document with a generated ID
                    db.collection("users")
                        .document(user.uid).set(userData)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "user get failed with ", exception)
            }
    }

}