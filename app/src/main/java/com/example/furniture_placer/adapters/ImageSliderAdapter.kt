package com.example.furniture_placer.adapters



import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.StorageService
import kotlinx.android.synthetic.main.slider_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImageSliderAdapter(
    private val room: Room,
    private val listener: OnImageClickListener
): RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    override fun getItemViewType(position: Int): Int {
        return R.layout.slider_list_item
    }

    override fun getItemCount(): Int {
        val count = room.decorationSnapshots?.size
        return if (count != null) {
            count
        } else {
            Log.d("FYI","slider creation failed")
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh:MyViewHolder, position:Int) {

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
            vh.description.text = vh.itemView.context.getString(R.string.room_slider_type_first)
            vh.hoverText.text = vh.itemView.context.getString(R.string.room, room.name)
        } else {
            vh.hoverText.text = vh.itemView.context.getString(R.string.screenshot, position)
            vh.description.text = vh.itemView.context.getString(R.string.room_slider_type_second)
        }
        val modelImageList = ArrayList<String>()
        if (room.decorationSnapshots != null) {
            if (room.decorationSnapshots!![position].itemsInScene != null){
                for (path in room.decorationSnapshots!![position].itemsInScene!!) {
                    Log.d("FYI", "model image path: " + path.previewImagePath)
                    path.previewImagePath?.let { modelImageList.add(it) }
                }
            }
        }
        Log.d("FYI", "model list: "+modelImageList.toString())

        recyclerView = vh.itemView.findViewById(R.id.rv_screenshotModels)
        recyclerView.layoutManager = LinearLayoutManager(vh.itemView.context)
        recyclerView.adapter = items?.let { ScreenshotModelAdapter(it, modelImageList) }

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

        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    interface OnImageClickListener {
        fun onImageClick(imageData: Bitmap)
    }



}