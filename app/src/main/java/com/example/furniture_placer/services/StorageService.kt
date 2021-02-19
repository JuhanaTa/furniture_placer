package com.example.camera.CameraService

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader


class StorageService : AppCompatActivity() {
    val storage = Firebase.storage
    val storageRef = storage.reference

    suspend fun storePicture(bitmap: Bitmap, path: String) {
        val userPictureRef = storageRef.child(path)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = userPictureRef.putBytes(data).await()

    }

    suspend fun loadPicture(path: String): ByteArray {
        val imageRef = storageRef.child(path)

        val ONE_MEGABYTE: Long = 1024 * 1024
        return imageRef.getBytes(ONE_MEGABYTE).await()
    }

    suspend fun loadModel(path:String,fileName: String,context: Context) : Uri{

        val filesDir = context.filesDir
        Log.w("StorageService", "path: ${filesDir.absolutePath}")
        val savingFolder = File(filesDir, "models")
        if(!savingFolder.exists())
        savingFolder.mkdir()

        val assetRef = storageRef.child("$path$fileName")
        val localFile = File(savingFolder.absolutePath,"$fileName")


        val getFileResult = assetRef.getFile(localFile).await()
        return localFile.toUri()
    }
}
