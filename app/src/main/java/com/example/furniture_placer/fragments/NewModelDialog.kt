package com.example.furniture_placer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.OneModel
import com.example.furniture_placer.R
import com.example.furniture_placer.adapters.NewModelAdapter
import com.example.furniture_placer.data_models.Furniture
import com.example.furniture_placer.interfaces.ModelChangeCommunicator
import kotlinx.android.synthetic.main.fragment_new_model_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NewModelDialog(modelList: ArrayList<Furniture>) : DialogFragment(), NewModelAdapter.OnItemClickListener {


    private val models = modelList
    private lateinit var communicator: ModelChangeCommunicator
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_model_dialog, container, false)
        communicator = activity as ModelChangeCommunicator

        view.cancelBtn.setOnClickListener {
            dismiss()
        }

        recyclerView = view.findViewById(R.id.rv_models)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        /*modelList.add(OneModel("Tuoli asdasdasdsadsad", 0))
        modelList.add(OneModel("pöytä", 1))
        modelList.add(OneModel("kello", 2))
        modelList.add(OneModel("sohva", 3))*/
        recyclerView.adapter =
            NewModelAdapter(models, this)

        return  view
    }

    override fun onItemClick(position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val clickedItem: Furniture = models[position]
            communicator.changeModel(clickedItem)
            Log.d("FYI", clickedItem.name)
            dismiss()
        }

    }




}