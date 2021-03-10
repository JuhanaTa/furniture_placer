package com.example.furniture_placer

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.data_models.roomFromFirestore
import com.example.furniture_placer.services.FirebaseService

class MainViewModel(application: Application): AndroidViewModel(application) {
    private  var _roomsLiveData: MutableLiveData<ArrayList<Room>> = MutableLiveData()
    private val rooms: MutableLiveData<ArrayList<Room>> = roomsLiveData

    fun getRooms() = rooms

    fun listenToRooms() {
        FirebaseService().getUserRoomsCollection()?.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val rooms = ArrayList<Room>()
            value?.documents?.forEach{
                val room = roomFromFirestore(it)
                room.id = it.id
                rooms.add(room)
            }

            _roomsLiveData.value = rooms
        }
    }
    private var roomsLiveData: MutableLiveData<ArrayList<Room>>
        get() {return _roomsLiveData}
        set(value) {_roomsLiveData = value}
}