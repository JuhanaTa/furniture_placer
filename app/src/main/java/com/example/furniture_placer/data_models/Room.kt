package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Room (
        val name: String? = null,
        val created: Timestamp? = Timestamp.now(),
        var id: String? = null,
        var previewPhotoPath: String? = null,
        var decorationSnapshots: @RawValue ArrayList<DecorationSnapshot>? = ArrayList(),
        var recentFurniture: ArrayList<Furniture>? = ArrayList()): Parcelable


fun roomToHash(room: Room): HashMap<String, Any?> {
    return hashMapOf(
        "name" to room.name,
        "created" to room.created,
        "recentFurniture" to room.recentFurniture,
        "previewPhotoPath" to room.previewPhotoPath,
        "decorationSnapshots" to room.decorationSnapshots
    )
}

fun roomFromFirestore(doc: DocumentSnapshot) : Room{
    val decorationSnapshotList: ArrayList<DecorationSnapshot> = ArrayList<DecorationSnapshot>()
    val recentFurnitureList : ArrayList<Furniture> = ArrayList<Furniture>()
    if (doc["decorationSnapshots"] != null) {
        val decorationSnapshotHashMaps = doc["decorationSnapshots"] as ArrayList<*>
        decorationSnapshotHashMaps.forEach {
            val map = it as HashMap<String, Any?>
            decorationSnapshotList.add(decorationSnapshotFromHashMap(map))

        }
    }
    if(doc["recentFurniture"] != null){
        val recentFurnitureHashMaps = doc["recentFurniture"] as ArrayList<*>
        recentFurnitureHashMaps.forEach{
            val map = it as HashMap<String,Any?>
            recentFurnitureList.add(furnitureFromHashMap(map))
        }
    }

    return Room(
            name = doc["name"] as String?,
            created = doc["created"] as Timestamp?,
            id = doc.id,
            previewPhotoPath = doc["previewPhotoPath"] as String?,
            recentFurniture = recentFurnitureList,
            decorationSnapshots = decorationSnapshotList
    )
}