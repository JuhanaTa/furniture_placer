package com.example.furniture_placer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.adapters.RoomAdapter
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.data_models.roomFromFirestore
import com.example.furniture_placer.fragments.CreateRoomFragment
import com.example.furniture_placer.fragments.MainPageFragment
import com.example.furniture_placer.fragments.RoomDetailFragment
import com.example.furniture_placer.interfaces.Communicator
import com.example.furniture_placer.services.FirebaseService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity(),
    Communicator, RoomAdapter.OnItemClickListener {
    val REQUEST_IMAGE_CAPTURE = 1
    var mCurrentPhotoPath: String = ""
    var roomName = ""
    var roomList : ArrayList<Room>? = null
    val camera_RQ = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        checkPermissions(android.Manifest.permission.CAMERA, "camera", camera_RQ)
        listenToRooms()
        val mainPage = MainPageFragment(this)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mainPage).commit()

        //checkPermissions(android.Manifest.permission.CAMERA, "camera", camera_RQ)

    }

    /*override fun nextFragment() {
        val transaction = this.supportFragmentManager.beginTransaction()
        val createRoomFrag = CreateRoomFragment()
        transaction.replace(R.id.fragment_container, createRoomFrag)
        transaction.addToBackStack(null)
        transaction.commit()

    }*/

    override fun openDialog(image: ByteArray?, name: String) {
        val bundle = Bundle()
        bundle.putByteArray("imageByteArray",image)
        bundle.putString("roomName", name)
        val dialog = CreateRoomFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "createRoomDialog")

    }

    override fun takePicture(name: String) {
        roomName = name
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, recIntent)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            GlobalScope.launch(Dispatchers.Main) {
                val baos = ByteArrayOutputStream()
                val pImage = Bitmap.createScaledBitmap(imageBitmap, 1080, 1440, false)
                pImage.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                val data = baos.toByteArray()
                openDialog(data, roomName)
            }
        }
    }

    override fun onItemClick(position: Int) {

        val roomDetailFrag =
                roomList?.get(position)?.let { RoomDetailFragment(it) }

        supportFragmentManager.beginTransaction().apply {
            if (roomDetailFrag != null) {
                replace(R.id.fragment_container, roomDetailFrag)
            }
            addToBackStack(null)
            commit()
        }
    }



private fun listenToRooms() {
 FirebaseService().getUserRoomsCollection()?.addSnapshotListener { value, e ->
     if (e != null) {
         Log.w("Firebase", "Listen failed.", e)
         return@addSnapshotListener
     }

     val rooms = ArrayList<Room>()
     value?.documents?.forEach{
         val room = roomFromFirestore(it)
         room?.id = it.id
         rooms.add(room!!)
     }

     roomList = rooms
 }
}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {


            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
            }
        }
        when (requestCode){
            camera_RQ -> innerCheck("camera")
        }
    }

    fun checkPermissions(permission: String, name: String, requestCode: Int){
        when{
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
            }
            shouldShowRequestPermissionRationale(permission) -> showPermissionDialogDialog(permission,name, requestCode)

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun showPermissionDialogDialog(permission: String, name: String, requestCode: Int){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access $name is required in order to use this app")
            setTitle("Permission required")
            setPositiveButton("OK"){ dialog, which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


}