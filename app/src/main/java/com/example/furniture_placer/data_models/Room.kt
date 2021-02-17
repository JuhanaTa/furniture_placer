package com.example.furniture_placer.data_models

import com.google.firebase.Timestamp

data class Room (
    val name: String? = null,
    val created: Timestamp = Timestamp.now(),
    var id: String? = null,
    var previewPhotoPath: String? = null,
    var decoreationPhotoPaths: MutableList<String>? = null,
    var itemCount: Int = 0)


fun roomToHash(room: Room): HashMap<String, Any?> {

    return hashMapOf(
        "name" to room.name,
        "created" to room.created,
        "itemCount" to room.itemCount,
        "previewPhoto" to room.previewPhotoPath,
        "decoreationPhotos" to room.decoreationPhotoPaths
    )
}