package com.example.furniture_placer.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.furniture_placer.DecorationSnapshotComparision
import com.example.furniture_placer.OneImage
import com.example.furniture_placer.R
import com.example.furniture_placer.adapters.ImageSliderAdapter
import com.example.furniture_placer.data_models.DecorationSnapshot
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.data_models.Room
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_room_detail.view.*


class RoomDetailFragment(room: Room) : Fragment() {

    lateinit var recyclerViewSlider: RecyclerView
    private val myRoom = room
    private var isOpen: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_detail, container, false)

        view.roomMenuBtn.setOnClickListener {
            if (isOpen){
                view.compareBtn.visibility = View.VISIBLE
                view.compareText.visibility = View.VISIBLE
                view.editRoomBtn.visibility = View.VISIBLE
                view.editRoomText.visibility = View.VISIBLE

                view.editRoomBtn.setOnClickListener {
                    val roomToEdit: Room = myRoom
                    //roomToEdit.decorationSnapshots?.removeAt(0)
                    val intent = Intent(activity, ArFragmentView::class.java).apply {
                        putExtra("EDITED_ROOM",roomToEdit)
                    }
                    startActivity(intent)
                }

                view.compareBtn.setOnClickListener{
                    val roomToEdit: Room = myRoom
                    //roomToEdit.decorationSnapshots?.removeAt(0)
                    val intent = Intent(activity, DecorationSnapshotComparision::class.java).apply {
                        putExtra("EDITED_ROOM",roomToEdit)
                    }
                    startActivity(intent)
                }

                isOpen = false
            } else {
                view.compareBtn.visibility = View.GONE
                view.compareText.visibility = View.GONE
                view.editRoomBtn.visibility = View.GONE
                view.editRoomText.visibility = View.GONE
                view.editRoomBtn.setOnClickListener(null)
                view.compareBtn.setOnClickListener(null)
                isOpen = true
            }

        }


        /// Setup for image slider
        val layoutManagerSlider = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSlider = view.findViewById(R.id.sliderList)
        recyclerViewSlider.setLayoutManager(layoutManagerSlider)



        //val roomList = myRoom
        val json = Gson().toJson(myRoom)
        val roomList = Gson().fromJson(json, Room::class.java)

        val default = ArrayList<Furniture>()
        val recents = roomList.recentFurniture
        if(roomList.decorationSnapshots?.get(0)?.itemsInScene?.get(0)?.id != "default") {

            if (recents != null) {
                for (item in recents) {
                    default.add(Furniture(item, "default", "default", "0"))
                }
            }
            roomList.decorationSnapshots?.add(0, DecorationSnapshot("default", myRoom.previewPhotoPath, default))
        }



        Log.d("FYI", "list contains: ${roomList}")


        //dot navigation menu setup
        val dots = view.dots_indicator
        dots.initDots(roomList.decorationSnapshots!!.size)
        dots.setDotSelection(0)

        //listener if user taps one of the dots
        //scrolls to right position
        dots.onSelectListener = {
            Log.d("FYI", "page $it selected")
            recyclerViewSlider.smoothScrollToPosition(it)
        }
        //adapter setup
        recyclerViewSlider.adapter =  ImageSliderAdapter(roomList)

        //scroll listener
        //when user scrolls layoutmanager is used to check what item position is visible
        //dot position assigned with that position
        recyclerViewSlider.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("FYI", "scrolled")
                val pos = layoutManagerSlider.findFirstVisibleItemPosition()
                dots.setDotSelection(pos)
            }
        })

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewSlider)



        ///setup for model list

       /* recyclerView = view.findViewById(R.id.rv_roomDetails)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val detailList = myRoom.recentFurniture
        if (detailList?.size == 0){
            detailList.add("No furnitures added")
        }
        recyclerView.adapter =
                detailList?.let { RoomDetailAdapter(it) }*/

        return view
    }

}