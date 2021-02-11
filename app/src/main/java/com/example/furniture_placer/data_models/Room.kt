package com.example.furniture_placer.data_models

import com.google.firebase.Timestamp

data class Room (
    val name: String? = null,
    val created: Timestamp = com.google.firebase.Timestamp.now(),
    var id: String? = null,
    var itemCount: Int = 0)