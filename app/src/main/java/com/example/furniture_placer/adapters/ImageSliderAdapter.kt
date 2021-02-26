package com.example.furniture_placer.adapters


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.OneImage
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.FirebaseService
import kotlinx.android.synthetic.main.room_list_item.view.*
import kotlinx.android.synthetic.main.slider_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageSliderAdapter(
    private val images: ArrayList<OneImage>,
    private val room: Room
): RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    private var hasInitParentDimensions = false
    private var maxImageWidth: Int = 0
    private var maxImageHeight: Int = 0
    private var maxImageAspectRatio: Float = 1f
    lateinit var recyclerView: RecyclerView

    override fun getItemViewType(position: Int): Int {
        return R.layout.slider_list_item
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_list_item, parent, false)

        if (!hasInitParentDimensions) {
            maxImageWidth = parent.width
            maxImageHeight = parent.height
            maxImageAspectRatio = maxImageWidth.toFloat() / maxImageHeight.toFloat()
            hasInitParentDimensions = true
        }



        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {

        val imagePath = images[position].image
        Log.d("ROOM", imagePath)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val imageData = loadImage(imagePath)
                Log.d("ROOM", "loaded image")
                Log.d("DATA", imageData.toString())
                vh.itemView.sliderImage.setImageBitmap(imageData)

            }catch (e:Exception){
                Log.d("ERROR", "image load failed, $e")
            }
        }
        val items = room.decorationSnapshots?.get(position)?.itemsInScene

        recyclerView = vh.itemView.findViewById(R.id.rv_screenshotModels)
        recyclerView.layoutManager = LinearLayoutManager(vh.itemView.context)
        recyclerView.adapter = items?.let { ScreenshotModelAdapter(it) }
        //vh.view.roomName.text = "Room: ${room.name} and id of room:  ${room.id}"
        //vh.view.roomId.text = "asdasdasda"
        //vh.itemView.sliderImage.setImageResource(R.drawable.homer)
        //Log.d("FYI", "${room.name}")


    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val text: ImageView = itemView.findViewById(R.id.sliderImage)
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
        //val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        //vh.view.roomImage.setImageBitmap(bitmap)
        return bitmap
    }



}