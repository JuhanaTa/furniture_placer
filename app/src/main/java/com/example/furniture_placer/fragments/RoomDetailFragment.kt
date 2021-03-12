package com.example.furniture_placer.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.furniture_placer.DecorationSnapshotComparision
import com.example.furniture_placer.R
import com.example.furniture_placer.adapters.ImageSliderAdapter
import com.example.furniture_placer.data_models.DecorationSnapshot
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.data_models.roomFromFirestore
import com.example.furniture_placer.services.FirebaseService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_room_detail.view.*


class RoomDetailFragment(room: Room, private val imageListener: ImageSliderAdapter.OnImageClickListener) : Fragment() {

    private lateinit var recyclerViewSlider: RecyclerView
    private var myRoom = room
    private var isOpen: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_detail, container, false)
        listenToRoom(view)
        view.roomMenuBtn.setOnClickListener {

            if (isOpen){
                view.compareBtn.visibility = View.VISIBLE
                view.compareText.visibility = View.VISIBLE
                view.editRoomBtn.visibility = View.VISIBLE
                view.editRoomText.visibility = View.VISIBLE

                view.editRoomBtn.setOnClickListener {
                    val roomToEdit: Room = myRoom
                    //new intent for ar view
                    val intent = Intent(activity, ArFragmentView::class.java).apply {
                        putExtra("EDITED_ROOM",roomToEdit)
                    }
                    startActivity(intent)
                }

                view.compareBtn.setOnClickListener{
                    val roomToEdit: Room = myRoom
                    //new intent for compare view
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


        //scroll listener
        //when user scrolls layoutmanager is used to check what item position is visible
        //dot position assigned with that position
        recyclerViewSlider.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos = layoutManagerSlider.findFirstVisibleItemPosition()
                view.dots_indicator.setDotSelection(pos)
            }
        })

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewSlider)

        return view
    }

    private fun listenToRoom(view: View) {
        FirebaseService().getUserRoomReference(myRoom)?.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("Firebase", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (value != null) {
                myRoom = roomFromFirestore(value)
                val json = Gson().toJson(myRoom)
                val newRoom = Gson().fromJson(json, Room::class.java)

                val default = newRoom.recentFurniture
                if (newRoom.decorationSnapshots!!.isNotEmpty()) {
                    if (newRoom.decorationSnapshots!![0].itemsInScene?.isNotEmpty()!!) {
                        if (newRoom.decorationSnapshots?.get(0)?.itemsInScene?.get(0)?.id != "default") {

                            newRoom.decorationSnapshots?.add(
                                0,
                                DecorationSnapshot("default", myRoom.previewPhotoPath, default)
                            )

                            //dot navigation menu setup
                            val dots = view.dots_indicator
                            dots.initDots(newRoom.decorationSnapshots!!.size)
                            dots.setDotSelection(0)

                            //listener if user taps one of the dots
                            //scrolls to right position
                            dots.onSelectListener = {
                                Log.d("FYI", "page $it selected")
                                recyclerViewSlider.smoothScrollToPosition(it)
                            }
                            //adapter setup
                            recyclerViewSlider.adapter = ImageSliderAdapter(newRoom, imageListener)
                        }
                    }
                } else {
                    Log.d("FYI", "decorationSnapshots was empty")
                    if (default != null) {
                        default.add(Furniture("default", "default", "default", "0"))
                    }
                    newRoom.decorationSnapshots?.add(0, DecorationSnapshot("default", myRoom.previewPhotoPath, default))
                    recyclerViewSlider.adapter = ImageSliderAdapter(newRoom, imageListener)
                }
            }
        }
    }

}