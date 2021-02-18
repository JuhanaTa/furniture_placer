package com.example.furniture_placer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.furniture_placer.adapters.RoomAdapter
import com.example.furniture_placer.fragments.CreateRoomFragment
import com.example.furniture_placer.fragments.MainPageFragment
import com.example.furniture_placer.fragments.RoomDetailFragment
import com.example.furniture_placer.interfaces.Communicator
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


    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.Main) {
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainPage =
            MainPageFragment(this)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mainPage).commit()

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
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                openDialog(data, roomName)
            }
        }
    }

    override fun onItemClick(position: Int) {

        val roomDetailFrag =
            RoomDetailFragment()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, roomDetailFrag)
            addToBackStack(null)
            commit()
        }
    }
}