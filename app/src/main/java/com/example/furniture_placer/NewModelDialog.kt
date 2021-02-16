package com.example.furniture_placer

import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_new_model_dialog.*


class NewModelDialog(modelList: ArrayList<OneModel>) : DialogFragment(), NewModelAdapter.OnItemClickListener {


    private val models = modelList
    private lateinit var communicator: ModelChangeCommunicator
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_model_dialog, container, false)
        communicator = activity as ModelChangeCommunicator


        recyclerView = view.findViewById(R.id.rv_models)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        /*modelList.add(OneModel("Tuoli asdasdasdsadsad", 0))
        modelList.add(OneModel("pöytä", 1))
        modelList.add(OneModel("kello", 2))
        modelList.add(OneModel("sohva", 3))*/
        recyclerView.adapter = NewModelAdapter(models, this)

        return  view
    }

    override fun onItemClick(position: Int) {
        val clickedItem: OneModel = models[position]
        communicator.changeModel(clickedItem.modelName)
        Log.d("FYI", clickedItem.modelName)
    }




}