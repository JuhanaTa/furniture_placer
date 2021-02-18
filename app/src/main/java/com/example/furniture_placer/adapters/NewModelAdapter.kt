package com.example.furniture_placer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneModel
import com.example.furniture_placer.R
import com.example.furniture_placer.data_models.Furniture
import kotlinx.android.synthetic.main.model_list_item.view.*

class NewModelAdapter(
    private val models: ArrayList<Furniture>,
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
        val model: Furniture = models[position]
        vh.view.modelName.text = "${model.name}"
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