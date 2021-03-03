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

    fun storePictureSync(bitmap: Bitmap, path: String) {
        val userPictureRef = storageRef.child(path)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = userPictureRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            Log.w(ContentValues.TAG, "image uploaded ${uploadTask.isComplete}")
            userPictureRef.downloadUrl.addOnCanceledListener {  }
            Log.w("StorageService", "downloadURl: ${userPictureRef.downloadUrl}")
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.w(ContentValues.TAG, "download url $downloadUri")
            }
        }
    }

    suspend fun loadPicture(path: String): ByteArray {
        val imageRef = storageRef.child(path)

        val ONE_MEGABYTE: Long = 1024 * 1024
        return imageRef.getBytes(ONE_MEGABYTE).await()
    }

    fun deleteFileSync(path:String){
        val ref = storageRef.child(path)
        // Delete the file
        ref.delete().addOnSuccessListener {
            Log.w("StorageService", "File deleted successfully")
        }.addOnFailureListener {
            Log.w("StorageService", " Uh-oh, an error occurred! during file deleting: ${it.message}")
        }
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
