package com.example.furniture_placer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity(), Communicator {
    val REQUEST_IMAGE_CAPTURE = 1
    var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.Main) {
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainPage = MainPageFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mainPage).commit()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            // Permission not granted
            //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
        }else {
            val fileName = "temp_photo"
            val imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var imageFile: File? = null
            imageFile = File.createTempFile(fileName, ".jpg", imgPath )
            mCurrentPhotoPath = imageFile!!.absolutePath

            val photoURI: Uri = FileProvider.getUriForFile(this,
                "com.example.furniture_placer.fileprovider",
                imageFile)

            val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (myIntent.resolveActivity(packageManager) != null) {
                myIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(myIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    override fun nextFragment() {
        val transaction = this.supportFragmentManager.beginTransaction()
        val createRoomFrag = CreateRoomFragment()
        transaction.replace(R.id.fragment_container, createRoomFrag)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun openDialog() {
        var dialog = CreateRoomFragment()
        dialog.show(supportFragmentManager, "customDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, recIntent)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            StorageService().storePicture(imageBitmap, "${FirebaseService().getCurrentUser()?.uid}/roomName/previewImage.jpg")
        }
    }
}