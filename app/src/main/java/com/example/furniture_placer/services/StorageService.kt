package com.example.camera.CameraService

import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class StorageService : AppCompatActivity() {
    val storage = Firebase.storage
    val storageRef = storage.reference

    fun storePicture(bitmap: Bitmap, path: String) {
        val userPictureRef = storageRef.child(path)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = userPictureRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            Log.w(ContentValues.TAG, "image uploaded ${uploadTask.isComplete}")
            userPictureRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.w(ContentValues.TAG, "download url ${downloadUri.toString()}")
            }
        }
    }

    suspend fun loadPicture(path: String): ByteArray {
        val imageRef = storageRef.child(path)

        val ONE_MEGABYTE: Long = 1024 * 1024
        return imageRef.getBytes(ONE_MEGABYTE).await()
    }
}
