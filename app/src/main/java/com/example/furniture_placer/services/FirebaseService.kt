package com.example.furniture_placer.services

import android.content.ContentValues.TAG
import android.util.Log
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.data_models.roomToHash
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

class FirebaseService {
    val db = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createPersonalStorage(user: FirebaseUser) {
        db.collection("users").document(user.uid).get()
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
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "user get failed with ", exception)
            }
    }

    suspend fun createRoom(name: String): Room {
        val user = firebaseAuth.currentUser
            ?: return Room(name = name)
        val uid = user.uid

        val roomData = hashMapOf(
            "name" to name,
            "created" to Timestamp(System.currentTimeMillis()),
            "itemCount" to 0
        )
        val doc = db.collection("users").document(uid).collection("rooms").add(roomData).await()
        return Room(name = name, id = doc.id)
    }

    fun updateRoom(room: Room) {
        val user = firebaseAuth.currentUser ?: return
        val uid = user.uid
        val roomData = roomToHash(room)

        db.collection("users").document(uid).collection("rooms").document("${room.id}")
            .update(roomData).addOnSuccessListener {
            Log.d(
                TAG,
                "room updated successfully!"
            )
        }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating room", e) }
    }

    fun getUserRoomsCollection(): CollectionReference? {
        val user = firebaseAuth.currentUser
            ?: return null
        val uid = user.uid
        return db.collection("users").document(uid).collection("rooms")
    }

    suspend fun getFurnitures() :List<Furniture?>{
        val query = db.collection("furnitures").get().await()
        val furnitures =  query.documents.map{doc ->
            Furniture(
                name = doc["name"] as String,
                id = doc.id,
                modelFiles = doc["modelFiles"] as ArrayList<String>?,
                path = doc["path"] as String,
                previewImagePath = doc["previewImagePath"] as String?
            )}

        return furnitures
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}