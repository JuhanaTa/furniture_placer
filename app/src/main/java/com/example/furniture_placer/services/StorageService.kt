package com.example.camera.CameraService

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.furniture_placer.Communicator
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Error


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
