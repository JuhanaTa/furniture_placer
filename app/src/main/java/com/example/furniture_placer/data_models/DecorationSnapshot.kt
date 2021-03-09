package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize


/**
 * This is data class for storing all the information when user takes a screenshot.
 */
@Parcelize
data class DecorationSnapshot(
        val name: String? = null,
        var photoPath: String? = null,
        var itemsInScene: ArrayList<Furniture>? = ArrayList()): Parcelable

/**
 * For making a [DecorationSnapshot] class to [HashMap]. This is used if you want to save this class to firestore.
 */
fun decorationSnapShotToHash(decorationSnapshot: DecorationSnapshot): HashMap<String, Any?> {
    return hashMapOf(
            "name" to decorationSnapshot.name,
            "photoPath" to decorationSnapshot.photoPath,
            "itemsInScene" to decorationSnapshot.itemsInScene
    )
}

/**
 * Returns a [DecorationSnapshot] class from firestore [DocumentSnapshot]. This is used to parse firestore maps
 */
fun decorationSnapshotFromFirestore(doc: DocumentSnapshot) : DecorationSnapshot{
    return DecorationSnapshot(
            name = doc["name"] as String?,
            photoPath = doc["photoPath"] as String?
            //itemsInScene = furnitureFromFirestore() as ArrayList<Furniture>?
    )
}

/**
 * Returns a [DecorationSnapshot] class from [HashMap]. This is usually used when parsing [DecorationSnapshot] class out from [Room] class
 */
@Suppress("UNCHECKED_CAST")
fun decorationSnapshotFromHashMap(map: HashMap<String,Any?>) : DecorationSnapshot{
    val itemMaps = map["itemsInScene"] as ArrayList<Any?>
    val itemsInScene: ArrayList<Furniture>? = ArrayList<Furniture>()
    itemMaps.forEach{
        val itemMap = it as HashMap<String,Any?>
        itemsInScene?.add(furnitureFromHashMap(itemMap))
    }
    return DecorationSnapshot(
            name = map["name"] as String?,
            photoPath = map["photoPath"] as String?,
            itemsInScene = itemsInScene
    )
}
