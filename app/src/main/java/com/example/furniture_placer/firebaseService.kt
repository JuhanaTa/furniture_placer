package com.example.furniture_placer

import android.content.ContentValues.TAG
import android.util.Log
import com.example.furniture_placer.data_models.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.inject.Deferred
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
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

    suspend fun createRoom(name:String) : Room{
        val user = firebaseAuth.currentUser
            ?: return Room(name = name)
        var uid = user.uid

        val roomData = hashMapOf(
            "name" to name,
            "created" to Timestamp(System.currentTimeMillis()),
            "itemCount" to 0
        )
        val doc = db.collection("users").document(uid).collection("rooms").add(roomData).await()
        return Room(name = name,id = doc.id,itemCount = 0)
    }

    suspend fun getUserRooms() : List<Room?> {
        val user = firebaseAuth.currentUser
            ?: return mutableListOf<Room>()

        var uid = user.uid

        val query = db.collection("users").document(uid).collection("rooms").get().await()

        return query.documents.map{doc ->  doc.toObject<Room>()}
    }

}