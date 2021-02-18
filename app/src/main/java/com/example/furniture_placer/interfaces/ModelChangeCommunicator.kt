package com.example.furniture_placer.interfaces
import com.example.furniture_placer.data_models.Furniture

interface ModelChangeCommunicator {
    suspend fun changeModel(file: Furniture)
}
