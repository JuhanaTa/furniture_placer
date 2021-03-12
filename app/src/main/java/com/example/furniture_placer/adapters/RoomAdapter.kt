package com.example.furniture_placer.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.services.StorageService
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.services.FirebaseService
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

        Log.d("FYI", "${room.name}")
        if (room.name != null){
            //image path from firebase service
            val imagePath = "${FirebaseService().getCurrentUser()?.uid}/${room.name}/previewImage.jpg"
            Log.d("FYI", imagePath)
            //coroutine for loading the image
            GlobalScope.launch(Dispatchers.Main) {

                try {
                    vh.text.roomName.text = vh.itemView.context.getString(R.string.room, room.name)
                    vh.modelCount.text = vh.itemView.context.getString(R.string.model_count, room.recentFurniture?.size)
                    val imageData = loadImage(imagePath)
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
    //suspended function that loads and returns image
    //returns bitmap
    private suspend fun loadImage(path: String): Bitmap{
        val imageData = StorageService().loadPicture(path)
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val text: TextView = itemView.findViewById(R.id.roomName)
        val image: ImageView = itemView.findViewById(R.id.roomImage)
        val modelCount: TextView = itemView.findViewById(R.id.modelCount)
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
    //OnItemClickListener used in MainActivity
    //used by overriding it
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }



}