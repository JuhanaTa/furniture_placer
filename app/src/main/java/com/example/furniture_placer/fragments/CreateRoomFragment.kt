package com.example.furniture_placer.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.furniture_placer.interfaces.Communicator
import com.example.furniture_placer.R
import com.example.furniture_placer.services.FirebaseService
import kotlinx.android.synthetic.main.fragment_create_room_dialog.*
import kotlinx.android.synthetic.main.fragment_create_room_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            communicator.takePicture(roomTextFieldInput.text.toString())
            dismiss()
        }

        rootView.createRoomBtn.setOnClickListener {
            val roomName =  roomTextFieldInput.text
            if (roomName.toString() != "") {
                GlobalScope.launch(Dispatchers.Main) {
                    firebase.createRoom(roomName.toString())
                }
                Log.d("FYI", roomName.toString())
                val intent = Intent(activity, ArFragmentView::class.java).apply {
                }
                startActivity(intent)
                dismiss()
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