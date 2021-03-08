package com.example.furniture_placer.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.camera.CameraService.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.FirebaseService
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.room_list_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RoomAdapter(
    private val rooms: ArrayList<Room>,
    private val listener: OnItemClickListener
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
        val room: Room = rooms[position]
        //vh.view.roomName.text = "Room: ${room.name} and id of room:  ${room.id}"
        //vh.view.roomId.text = "asdasdasda"
        //val imageview = vh.view.roomImage
        Log.d("FYI", "${room.name}")
        if (room.name != null){
            //loader.loadRoomImage(room.name)
            Log.d("FYI", "called loader")

            val imagePath = "${FirebaseService().getCurrentUser()?.uid}/${room.name}/previewImage.jpg"
            Log.d("FYI", imagePath)
            GlobalScope.launch(Dispatchers.Main) {
                //val imageData = StorageService().loadPicture(imagePath)
                //Log.d("FYI", "image loaded")
                try {
                    val imageData = loadImage(imagePath)
                    Log.d("asd", imageData.toString())


                    vh.text.roomName.text = "Room: ${room.name}"
                    vh.id.roomId.text = "id: ${room.id}"
                    vh.modelCount.text = "${room.recentFurniture?.size} models in this room."

                    vh.image.roomImage.setImageBitmap(imageData)
                }catch (e:Exception){
                    Log.d("ERROR", "image load failed, $e")
                }

            }
            vh.deleteButton.setOnClickListener {
                Log.w("StorageService", "path: ${FirebaseService().getCurrentUser()?.uid}/${room.name}")
                StorageService().deleteFileSync("${FirebaseService().getCurrentUser()?.uid}/${room.name}/previewImage.jpg")
                room.decorationSnapshots?.forEach{
                    if(it.photoPath != null) {
                        StorageService().deleteFileSync(it.photoPath!!)
                    }
                }
                FirebaseService().deleteRoom(room)

            }
        }
    }

    private suspend fun loadImage(path: String): Bitmap{
        Log.d("FYI", path)
        val imageData = StorageService().loadPicture(path)
        Log.d("FYI", "image loaded")
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        //val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        //vh.view.roomImage.setImageBitmap(bitmap)
        return bitmap
    }




    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val text: TextView = itemView.findViewById(R.id.roomName)
        val image: ImageView = itemView.findViewById(R.id.roomImage)
        val modelCount: TextView = itemView.findViewById(R.id.modelCount)
        val id: TextView = itemView.findViewById(R.id.roomId)
        val deleteButton: Button = itemView.findViewById(R.id.deleteBtn)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("FYI", "item clicked")
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }



}