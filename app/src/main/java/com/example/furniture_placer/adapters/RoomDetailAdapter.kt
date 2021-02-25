package com.example.furniture_placer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.R
import kotlinx.android.synthetic.main.room_detail_list_item.view.*

class RoomDetailAdapter(
    private val details: ArrayList<String>
): RecyclerView.Adapter<RoomDetailAdapter.MyViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return R.layout.room_detail_list_item
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_detail_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        val furnitureName: String = details[position]
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