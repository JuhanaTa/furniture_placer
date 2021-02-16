package com.example.furniture_placer

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_model_dialog.view.*
import kotlinx.android.synthetic.main.model_list_item.view.*
import kotlinx.android.synthetic.main.room_list_item.view.*

class NewModelAdapter(
    private val models: ArrayList<OneModel>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<NewModelAdapter.MyViewHolder>() {



    override fun getItemViewType(position: Int): Int {
        return R.layout.model_list_item
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(vh: MyViewHolder, position: Int) {
        val model: OneModel = models[position]
        vh.view.modelName.text = "${model.modelName}"
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),

        View.OnClickListener {
        val view: TextView = itemView.findViewById(R.id.modelName)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
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