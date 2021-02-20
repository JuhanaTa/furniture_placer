package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
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

fun decorationSnapshotFromFirestore(doc: DocumentSnapshot) : DecorationSnapshot{
    return DecorationSnapshot(
            name = doc["name"] as String?,
            photoPath = doc["photoPath"] as String?
            //itemsInScene = furnitureFromFirestore() as ArrayList<Furniture>?
    )
}

fun decorationSnapshotFromHashMap(map: HashMap<String,Any?>) : DecorationSnapshot{
    val itemMaps = map["itemsInScene"] as ArrayList<Any?>
    val itemsInScene: ArrayList<Furniture>? = ArrayList<Furniture>()
    itemMaps.forEach{
        val map = it as HashMap<String,Any?>
        itemsInScene?.add(furnitureFromHashMap(map))
    }
    return DecorationSnapshot(
            name = map["name"] as String?,
            photoPath = map["photoPath"] as String?,
            itemsInScene = itemsInScene
    )
}