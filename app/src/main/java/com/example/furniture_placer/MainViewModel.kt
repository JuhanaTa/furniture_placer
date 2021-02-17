package com.example.furniture_placer

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.FirebaseService

class MainViewModel(application: Application): AndroidViewModel(application) {
    private  var _roomsLiveData: MutableLiveData<ArrayList<Room>> = MutableLiveData<ArrayList<Room>>()
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
                val room = it.toObject(Room::class.java)
                room?.id = it.id
                rooms.add(room!!)
            }

            _roomsLiveData.value = rooms
        }
    }
    internal var roomsLiveData: MutableLiveData<ArrayList<Room>>
        get() {return _roomsLiveData}
        set(value) {_roomsLiveData = value}
}