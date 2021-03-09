package com.example.furniture_placer.data_models

import com.google.firebase.Timestamp

/**
 * Holds all the user information from Firestore
 */
data class UserData(val name: String, val email: String,val uid : String,val lastLogin: Timestamp) {
}