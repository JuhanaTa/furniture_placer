package com.example.furniture_placer.data_models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Used to store all the information of users single room. Stores list of [DecorationSnapshot] and list of [Furniture] as recent furniture
 */
@Parcelize
data class Room (
        val name: String? = null,
        val created: Timestamp? = Timestamp.now(),
        var id: String? = null,
        var previewPhotoPath: String? = null,
        var decorationSnapshots: @RawValue ArrayList<DecorationSnapshot>? = ArrayList(),
        var recentFurniture: ArrayList<Furniture>? = ArrayList()): Parcelable

/**
 * For making a [Room] class to [HashMap]. This is used if you want to save this class to firestore.
 */
fun roomToHash(room: Room): HashMap<String, Any?> {
    return hashMapOf(
        "name" to room.name,
        "created" to room.created,
        "recentFurniture" to room.recentFurniture,
        "previewPhotoPath" to room.previewPhotoPath,
        "decorationSnapshots" to room.decorationSnapshots
    )
}
/**
 * Returns a [Room] class from firestore [DocumentSnapshot]. This is used to parse firestore maps
 */
@Suppress("UNCHECKED_CAST")
fun roomFromFirestore(doc: DocumentSnapshot) : Room{
    val decorationSnapshotList: ArrayList<DecorationSnapshot> = ArrayList<DecorationSnapshot>()
    val recentFurnitureList : ArrayList<Furniture> = ArrayList<Furniture>()

    //Parses all the decorationSnapshots as ArrayList of HashMaps and gives it to the parser function
    if (doc["decorationSnapshots"] != null) {
        val decorationSnapshotHashMaps = doc["decorationSnapshots"] as ArrayList<*>
        decorationSnapshotHashMaps.forEach {
            val map = it as HashMap<String, Any?>
            decorationSnapshotList.add(decorationSnapshotFromHashMap(map))

        }
    }

    //Parses all the recentFurnitures as ArrayList of HashMaps and gives it to the parser function
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