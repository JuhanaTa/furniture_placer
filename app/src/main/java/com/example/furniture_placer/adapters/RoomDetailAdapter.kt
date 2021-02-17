package com.example.furniture_placer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneRoomDetail
import com.example.furniture_placer.R
import kotlinx.android.synthetic.main.room_detail_list_item.view.*

class RoomDetailAdapter(
    private val details: ArrayList<OneRoomDetail>
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
        val detail: OneRoomDetail = details[position]
        vh.view.modelName.text = "detail: ${detail.name} "
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val view: TextView = itemView.findViewById(R.id.modelName)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")

        }
    }



}