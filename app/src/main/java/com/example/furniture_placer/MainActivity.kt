package com.example.furniture_placer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), Communicator {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainPage = MainPageFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mainPage).commit()

    }

    override fun nextFragment() {
        val transaction = this.supportFragmentManager.beginTransaction()
        val createRoomFrag = CreateRoomFragment()
        transaction.replace(R.id.fragment_container, createRoomFrag)
        transaction.addToBackStack(null)
        transaction.commit()

    }

}