package com.example.furniture_placer

import android.net.Uri
import com.google.android.gms.tasks.Task

interface Communicator {
    fun nextFragment()
    fun openDialog(image: ByteArray?)
    fun takePicture()
}