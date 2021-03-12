package com.example.furniture_placer.interfaces


interface Communicator {
    fun openDialog(image: ByteArray?, name : String)
    fun takePicture(roomName: String)
}