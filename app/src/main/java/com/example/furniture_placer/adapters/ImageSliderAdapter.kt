package com.example.furniture_placer.adapters


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.services.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import kotlinx.android.synthetic.main.slider_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageSliderAdapter(
    private val room: Room,
    private val listener: OnImageClickListener
): RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    lateinit var recyclerView: RecyclerView

    override fun getItemViewType(position: Int): Int {
        return R.layout.slider_list_item
    }

    override fun getItemCount(): Int {
        val count = room.decorationSnapshots?.size
        if (count != null) {
            return count
        } else {
            Log.d("FYI","slider creation failed")
            return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {

        val imagePath = room.decorationSnapshots?.get(position)?.photoPath
        if (imagePath != null) {
            Log.d("ROOM", imagePath)
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val imageData = loadImage(imagePath)
                    Log.d("ROOM", "loaded image")
                    Log.d("DATA", imageData.toString())
                    vh.itemView.sliderImage.setImageBitmap(imageData)
                    vh.itemView.sliderImage.setOnClickListener {
                        listener.onImageClick(imageData)
                    }

                } catch (e: Exception) {
                    Log.d("ERROR", "image load failed, $e")
                }
            }
        }
        val items = room.decorationSnapshots?.get(position)?.itemsInScene
        if (position == 0){
            vh.description.text = "Whole Room contains these models"
            vh.hoverText.text = "Room: ${room.name}"
        } else {
            vh.hoverText.text = "Screenshot $position"
            vh.description.text = "Screenshot contains these models"
        }
        recyclerView = vh.itemView.findViewById(R.id.rv_screenshotModels)
        recyclerView.layoutManager = LinearLayoutManager(vh.itemView.context)
        recyclerView.adapter = items?.let { ScreenshotModelAdapter(it) }

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val text: ImageView = itemView.findViewById(R.id.sliderImage)
        val description: TextView = itemView.findViewById(R.id.description)
        val hoverText: TextView = itemView.findViewById(R.id.imageHoverTxt)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")
        }
    }

    private suspend fun loadImage(path: String): Bitmap {
        Log.d("ROOM", "loading image")
        val imageData = StorageService().loadPicture(path)
        Log.d("FYI", "image loaded")
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

        return bitmap
    }

    interface OnImageClickListener {
        fun onImageClick(imageData: Bitmap)
    }



}