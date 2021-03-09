package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

/**
 * Used to store all the information of the spawnable models in the ArFragmentView. Also uses [Parcelable]
 */
@Parcelize
data class Furniture (
        val name: String,
        var id: String,
        var path: String,
        var price: String,
        var previewImagePath: String? = null,
        var modelFiles: ArrayList<String>? = null
): Parcelable

/**
 * For making a [Furniture] class to [HashMap]. This is used if you want to save this class to firestore.
 */
fun furnitureToHash(furniture: Furniture) : HashMap<String, Any?>{
    return hashMapOf(
            "name" to furniture.name,
            "path" to furniture.path,
            "path" to furniture.price,
            "id" to furniture.id,
            "previewImagePath" to furniture.previewImagePath,
            "modelFiles" to furniture.modelFiles
    )
}
/**
 * Returns a [Furniture] class from firestore [DocumentSnapshot]. This is used to parse firestore maps
 */
@Suppress("UNCHECKED_CAST")
fun furnitureFromFirestore(doc: DocumentSnapshot) : Furniture{
    return Furniture(
            name = doc["name"] as String,
            path = doc["path"] as String,
            price = doc["price"] as String,
            id = doc.id,
            previewImagePath = doc["previewImagePath"] as String?,
            modelFiles = doc["recentFurniture"] as ArrayList<String>?
    )
}

/**
 * Returns a [Furniture] class from [HashMap]. This is usually used when parsing [Furniture] class out from [DecorationSnapshot] class
 */
@Suppress("UNCHECKED_CAST")
fun furnitureFromHashMap(map: HashMap<String, Any?>) : Furniture{
    return Furniture(
            name = map["name"] as String,
            path = map["path"] as String,
            price = map["price"] as String,
            id = map["id"] as String,
            previewImagePath = map["previewImagePath"] as String?,
            modelFiles = map["modelFiles"] as ArrayList<String>?
    )
}