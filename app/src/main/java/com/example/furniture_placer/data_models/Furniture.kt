package com.example.furniture_placer.data_models

data class Furniture (
        val name: String,
        var id: String,
        var previewImagePath: String? = null,
        var modelFiles: MutableList<String>? = null
)