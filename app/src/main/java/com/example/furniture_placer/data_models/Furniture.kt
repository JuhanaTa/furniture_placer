package com.example.furniture_placer.data_models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Furniture (
        val name: String,
        var id: String,
        var path: String,
        var previewImagePath: String? = null,
        var modelFiles: ArrayList<String>? = null
): Parcelable