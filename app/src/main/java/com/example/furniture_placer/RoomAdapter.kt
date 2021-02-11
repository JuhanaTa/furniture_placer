package com.example.furniture_placer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.room_list_item.view.*

class RoomAdapter(
    private val rooms: List<OneRoom>
): RecyclerView.Adapter<RoomAdapter.MyViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return R.layout.room_list_item
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        val room: OneRoom = rooms[position]
        vh.view.roomName.text = "Room: ${room.roomName} and id of room:  ${room.id}"
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val view: TextView = itemView.findViewById(R.id.roomName)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")
        }
    }
}