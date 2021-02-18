package com.example.furniture_placer.data_models

import com.google.firebase.Timestamp

data class Room (
    val name: String? = null,
    val created: Timestamp = Timestamp.now(),
    var id: String? = null,
    var previewPhotoPath: String? = null,
    var decoreationPhotoPaths: MutableList<String>? = null,
    var recentFurniture: MutableList<String>? = null)


fun roomToHash(room: Room): HashMap<String, Any?> {

    return hashMapOf(
        "name" to room.name,
        "created" to room.created,
        "recentFurniture" to room.recentFurniture,
        "previewPhoto" to room.previewPhotoPath,
        "decoreationPhotos" to room.decoreationPhotoPaths
    )
}