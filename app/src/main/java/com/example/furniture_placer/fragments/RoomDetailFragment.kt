package com.example.furniture_placer.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneRoomDetail
import com.example.furniture_placer.R
import com.example.furniture_placer.adapters.RoomDetailAdapter
import com.example.furniture_placer.data_models.Room
import kotlinx.android.synthetic.main.fragment_room_detail.view.*


class RoomDetailFragment(room: Room) : Fragment() {

    lateinit var recyclerView: RecyclerView
    var myRoom = room

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_detail, container, false)

        view.modifyRoomBtn.setOnClickListener {
            val intent = Intent(activity, ArFragmentView::class.java).apply {
                putExtra("EDITED_ROOM",myRoom)
            }
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.rv_roomDetails)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val detailList = myRoom.recentFurniture
        recyclerView.adapter =
                detailList?.let { RoomDetailAdapter(it) }

        return view
    }

}