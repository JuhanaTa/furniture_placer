package com.example.furniture_placer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_create_room_dialog.*
import kotlinx.android.synthetic.main.fragment_create_room_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateRoomFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_create_room_dialog, container, false)
        val firebase = FirebaseService()

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