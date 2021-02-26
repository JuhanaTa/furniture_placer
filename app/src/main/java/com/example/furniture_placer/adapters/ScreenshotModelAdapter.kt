package com.example.furniture_placer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Furniture
import kotlinx.android.synthetic.main.room_detail_list_item.view.*

class ScreenshotModelAdapter(
        private val furnitures: ArrayList<Furniture>
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
        if (furnitureName == "No furnitures added"){
            vh.imageview.visibility = View.GONE
        }

        vh.view.modelName.text = "$furnitureName "
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
        val view: TextView = itemView.findViewById(R.id.modelName)
        val imageview: ImageView = itemView.findViewById(R.id.modelImage)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")

        }
    }



}