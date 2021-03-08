package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Furniture (
        val name: String,
        var id: String,
        var path: String,
        var price: String,
        var previewImagePath: String? = null,
        var modelFiles: ArrayList<String>? = null
): Parcelable

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

@Suppress("UNCHECKED_CAST")
fun furnitureFromHashMap(map: HashMap<String, Any?>) : Furniture{
    return Furniture(
            name = map["name"] as String,
            path = map["path"] as String,
            price = map["price"] as String,
            id = map["id"] as String,
            previewImagePath = map["previewImagePath"] as String?,
            modelFiles = map["recentFurniture"] as ArrayList<String>?
    )
}