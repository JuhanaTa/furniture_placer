package com.example.furniture_placer.data_models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DecorationSnapshot(
        val name: String? = null,
        var photoPath: String? = null,
        var itemsInScene: ArrayList<Furniture>? = ArrayList()): Parcelable

fun decorationSnapShotToHash(decorationSnapshot: DecorationSnapshot): HashMap<String, Any?> {

    return hashMapOf(
            "name" to decorationSnapshot.name,
            "photoPath" to decorationSnapshot.photoPath,
            "itemsInScene" to decorationSnapshot.itemsInScene
    )
}