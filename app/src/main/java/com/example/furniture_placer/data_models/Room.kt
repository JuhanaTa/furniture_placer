package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room (
    val name: String? = null,
    val created: Timestamp = Timestamp.now(),
    var id: String? = null,
    var previewPhotoPath: String? = null,
    var decoreationPhotoPaths: MutableList<String>? = null,
    var recentFurniture: ArrayList<String>? = null) : Parcelable


fun roomToHash(room: Room): HashMap<String, Any?> {

    return hashMapOf(
        "name" to room.name,
        "created" to room.created,
        "recentFurniture" to room.recentFurniture,
        "previewPhotoPath" to room.previewPhotoPath,
        "decoreationPhotoPaths" to room.decoreationPhotoPaths
    )
}