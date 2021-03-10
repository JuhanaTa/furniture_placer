package com.example.furniture_placer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.furniture_placer.data_models.Room
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_decoration_snapshot_comparision.*


class DecorationSnapshotComparision : AppCompatActivity() {
    private lateinit var editedRoom: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val room = intent.getParcelableExtra<Room>("EDITED_ROOM")
        if (room != null) {
            editedRoom = room
        }
        setContentView(R.layout.activity_decoration_snapshot_comparision)

        val barChart: BarChart = barchart

        val entries: ArrayList<BarEntry> = ArrayList()
        val labels = ArrayList<String>()
        if (room != null) {
            room.decorationSnapshots?.forEachIndexed{index,snapshot ->
                var price = 0F
                snapshot.itemsInScene?.forEach {price += it.price.toFloat()}
                entries.add(BarEntry(price, index))
            }
            room.decorationSnapshots?.forEachIndexed{_,snapshot ->
                var price = 0F
                snapshot.itemsInScene?.forEach {price += it.price.toFloat()}
                labels.add(snapshot.name!!)
            }
        }

        val bardataset = BarDataSet(entries, "Cells")



        val data = BarData(labels, bardataset)
        barChart.data = data // set the data and list of labels into chart
        barChart.setDescription("Set Bar Chart Description Here")  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
        barChart.animateY(400)
    }
}