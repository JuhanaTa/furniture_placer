package com.example.furniture_placer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.furniture_placer.adapters.ImageSliderAdapter
import com.example.furniture_placer.adapters.RoomAdapter
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.data_models.roomFromFirestore
import com.example.furniture_placer.fragments.CreateRoomFragment
import com.example.furniture_placer.fragments.ImageFullscreenFragment
import com.example.furniture_placer.fragments.MainPageFragment
import com.example.furniture_placer.fragments.RoomDetailFragment
import com.example.furniture_placer.interfaces.Communicator
import com.example.furniture_placer.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity(),
    Communicator, RoomAdapter.OnItemClickListener, ImageSliderAdapter.OnImageClickListener {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var mCurrentPhotoPath: String = ""
    private var roomName = ""
    private var roomList : ArrayList<Room>? = null
    private val camera_RQ = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions(android.Manifest.permission.CAMERA, "camera", camera_RQ)
        listenToRooms()
        val mainPage = MainPageFragment(this)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mainPage).commit()

    }


    override fun openDialog(image: ByteArray?, name: String) {
        val bundle = Bundle()
        bundle.putByteArray("imageByteArray",image)
        bundle.putString("roomName", name)
        val dialog = CreateRoomFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "createRoomDialog")

    }

    override fun takePicture(roomName: String) {
        this.roomName = roomName
        val fileName = "temp_photo"
        val imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile: File?
        imageFile = File.createTempFile(fileName, ".jpg", imgPath )
        mCurrentPhotoPath = imageFile!!.absolutePath

        val photoURI: Uri = FileProvider.getUriForFile(this,
            "com.example.furniture_placer.fileprovider",
            imageFile)

        val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (myIntent.resolveActivity(packageManager) != null) {
            myIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
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
                roomList?.get(position)?.let { RoomDetailFragment(it, this) }

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
         if(it != null){
             val room = roomFromFirestore(it)
             room.id = it.id
             rooms.add(room)
         }
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
                Toast.makeText(applicationContext, getString(R.string.permission_denied, name), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, getString(R.string.permission_success, name), Toast.LENGTH_SHORT).show()
            }
        }
        when (requestCode){
            camera_RQ -> innerCheck("camera")
        }
    }

    private fun checkPermissions(permission: String, name: String, requestCode: Int){
        when{
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(applicationContext, getString(R.string.permission_success, name), Toast.LENGTH_SHORT).show()
            }
            shouldShowRequestPermissionRationale(permission) -> showPermissionDialogDialog(permission,name, requestCode)

            else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    private fun showPermissionDialogDialog(permission: String, name: String, requestCode: Int){
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage(getString(R.string.permission_message, name))
            setTitle(getString(R.string.permission_required))
            setPositiveButton("OK"){ _, _ ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onImageClick(imageData: Bitmap) {

        val imageFullScreenFrag = ImageFullscreenFragment(imageData)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, imageFullScreenFrag)
            addToBackStack(null)
            commit()
        }
    }


}