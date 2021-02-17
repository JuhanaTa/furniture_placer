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
import kotlinx.android.synthetic.main.fragment_room_detail.view.*


class RoomDetailFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_detail, container, false)

        view.modifyRoomBtn.setOnClickListener {
            val intent = Intent(activity, ArFragmentView::class.java).apply {
            }
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.rv_roomDetails)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val detailList = ArrayList<OneRoomDetail>()
        detailList.add(OneRoomDetail("tuoli", 1))
        detailList.add(OneRoomDetail("tuoli", 5))
        detailList.add(OneRoomDetail("tuoli", 1))
        detailList.add(OneRoomDetail("tuoli", 7))
        detailList.add(OneRoomDetail("tuoli", 1))
        detailList.add(OneRoomDetail("tuoli", 8))
        detailList.add(OneRoomDetail("tuoli", 1))

        recyclerView.adapter =
            RoomDetailAdapter(detailList)

        return view
    }

}