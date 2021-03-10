package com.example.furniture_placer.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.services.StorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScreenshotModelAdapter(
        private val furnitures: ArrayList<Furniture>,
        private val furnitureImages: ArrayList<String>
): RecyclerView.Adapter<ScreenshotModelAdapter.MyViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return R.layout.room_detail_list_item
    }

    override fun getItemCount(): Int {
        return furnitures.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_detail_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        val furnitureName: String = furnitures[position].name

        //first furniture is always default and that we don't want to show
        if (furnitureName == "default"){
            vh.imageview.visibility = View.GONE
            vh.modelPrice.visibility = View.GONE
        } else {
            vh.modelPrice.text = vh.itemView.context.getString(R.string.price, furnitures[position].price)
        }

        //If there is no furniture images then only "no models" string will be displayed
        if (furnitureImages.isEmpty()){
            vh.modelText.text = vh.itemView.context.getString(R.string.no_models)
        } else{
            vh.modelText.text = furnitureName
        }

        // image loaded in coroutine
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val imageData = loadImage(furnitureImages[position])
                Log.d("DATA", imageData.toString())
                vh.imageview.setImageBitmap(imageData)

            } catch (e: Exception) {
                Log.d("ERROR", "image load failed, $e")
            }
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
        val view: TextView = itemView.findViewById(R.id.modelName)
        val imageview: ImageView = itemView.findViewById(R.id.modelImage)
        val modelText: TextView = itemView.findViewById(R.id.modelName)
        val modelPrice: TextView = itemView.findViewById(R.id.modelPrice)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")
        }
    }

    //suspended function for loading the image
    //parameter path passed and bitmap returned
    private suspend fun loadImage(path: String): Bitmap {
        val imageData = StorageService().loadPicture(path)
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }



}