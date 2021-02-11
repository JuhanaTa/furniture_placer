package com.example.furniture_placer.data_models

import com.google.firebase.Timestamp

data class UserData(val name: String, val email: String,val uid : String,val lastLogin: Timestamp) {
}