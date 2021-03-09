package com.example.furniture_placer.services

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

/**
 * This class is used to communicate with the firebase storage. You can use this to access all the images and files in the firebase
 */
class StorageService : AppCompatActivity() {
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    /**
     * Takes a image as [Bitmap] and path as [String] to the firebase storage. for example the path is 'folder/file.jpg'
     */
    suspend fun storePicture(bitmap: Bitmap, path: String) {
        val userPictureRef = storageRef.child(path)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        userPictureRef.putBytes(data).await()

    }

    /**
     * This function can be called anywhere, see[storePicture] for async option. Takes a image as [Bitmap] and path as [String] to the firebase storage. for example the path is 'folder/file.jpg'
     */
    fun storePictureSync(bitmap: Bitmap, path: String) {
        val userPictureRef = storageRef.child(path)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = userPictureRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
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

    /**
     * Loads an image from given path, and returns it as [ByteArray]. Example path is in 'folder/file.jpg'
     */
    suspend fun loadPicture(path: String): ByteArray {
        val imageRef = storageRef.child(path)

        val oneMegabyte: Long = 1024 * 1024
        return imageRef.getBytes(oneMegabyte).await()
    }

    /**
     * Deletes the file with given path. This function is run synchronously. Example path is in 'folder/file.jpg'
     */
    fun deleteFileSync(path:String){
        val ref = storageRef.child(path)
        // Delete the file
        ref.delete().addOnSuccessListener {
            Log.w("StorageService", "File deleted successfully")
        }.addOnFailureListener {
            Log.w("StorageService", " Uh-oh, an error occurred! during file deleting: ${it.message}")
        }
    }

    /**
     * Loads a model from firebase storage with [String] path and filename separately. This is because the saving is done using just the filename. Returns an [Uri] to model file
     */
    suspend fun loadModel(path:String,fileName: String,context: Context) : Uri{

        val filesDir = context.filesDir
        Log.w("StorageService", "path: ${filesDir.absolutePath}")
        val savingFolder = File(filesDir, "models")
        if(!savingFolder.exists())
        savingFolder.mkdir()

        val assetRef = storageRef.child("$path$fileName")
        val localFile = File(savingFolder.absolutePath, fileName)


        assetRef.getFile(localFile).await()
        return localFile.toUri()
    }
}
