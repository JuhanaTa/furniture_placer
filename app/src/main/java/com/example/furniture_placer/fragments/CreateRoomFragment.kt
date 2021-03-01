package com.example.furniture_placer.fragments

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.interfaces.Communicator
import com.example.furniture_placer.services.FirebaseService
import kotlinx.android.synthetic.main.fragment_create_room_dialog.*
import kotlinx.android.synthetic.main.fragment_create_room_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class CreateRoomFragment : DialogFragment() {
    private lateinit var communicator: Communicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_create_room_dialog, container, false)
        val firebase = FirebaseService()
        val image = arguments?.getByteArray("imageByteArray")
        val roomName = arguments?.getString("roomName")

        Log.d("FYI", "image URL ${image}")
        if (image != null) {
            rootView.roomPreviewImg.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
        }
        rootView.roomTextFieldInput.setText(roomName)

        communicator = activity as Communicator
        rootView.roomPreviewImg.setOnClickListener {
            val returnInt = checkSelfPermission(context!!, Manifest.permission.CAMERA)
            Log.d("FYI", "permission: ${returnInt}")
            if (checkSelfPermission(context!!, Manifest.permission.CAMERA) == 0){
                communicator.takePicture(roomTextFieldInput.text.toString())
                dismiss()
            } else {
                Toast.makeText(context, "Permission to use camera is not granted", Toast.LENGTH_SHORT).show()
            }
        }

        rootView.createRoomBtn.setOnClickListener {
            val roomName = roomTextFieldInput.text
            if (roomName.toString() != "") {
                GlobalScope.launch(Dispatchers.Main) {
                    var room = Room()
                    //val
                    if (image != null) {
                        val imagePath = "${FirebaseService().getCurrentUser()?.uid}/${roomName}/previewImage.jpg"
                        //image stored in firebase before creating room
                        StorageService().storePicture(BitmapFactory.decodeByteArray(image, 0, image.size), imagePath)
                        //room created after. If Room is created first Live data in MainPageFragment executes and does not find image.
                        room = firebase.createRoom(roomName.toString())
                        Log.d("FYI", "room image stored")
                        room.previewPhotoPath = imagePath
                        firebase.updateRoom(room)

                    }
                    val intent = Intent(activity, ArFragmentView::class.java).apply {
                        putExtra("EDITED_ROOM",room)
                    }
                    startActivity(intent)
                    dismiss()
                }
            } else {
                Log.d("FYI", "empty field")
            }
        }

        rootView.cancelRoomBtn.setOnClickListener {
            dismiss()
        }
        return rootView
    }


}