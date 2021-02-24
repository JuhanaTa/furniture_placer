package com.example.furniture_placer.adapters


import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneImage
import com.example.furniture_placer.R
import kotlinx.android.synthetic.main.slider_list_item.view.*


class ImageSliderAdapter(
    private val images: ArrayList<OneImage>
): RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder>() {

    private var hasInitParentDimensions = false
    private var maxImageWidth: Int = 0
    private var maxImageHeight: Int = 0
    private var maxImageAspectRatio: Float = 1f


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
        val image: OneImage = images[position]
        //val ImageUri = image.image

        //vh.view.roomName.text = "Room: ${room.name} and id of room:  ${room.id}"
        //vh.view.roomId.text = "asdasdasda"
        vh.itemView.sliderImage.setImageResource(R.drawable.homer)
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




}