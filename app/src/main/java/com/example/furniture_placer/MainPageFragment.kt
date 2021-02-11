package com.example.furniture_placer

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.data_models.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_main_page.view.*


class MainPageFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var recyclerView: RecyclerView
    private lateinit var communicator: Communicator
    private  var _roomsLiveData: MutableLiveData<ArrayList<Room>> = MutableLiveData<ArrayList<Room>>()
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // call update listener
        listenToRooms()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        communicator = activity as Communicator

        recyclerView = view.findViewById(R.id.rv_rooms)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        _roomsLiveData.observe(this, Observer { recyclerView.adapter = RoomAdapter(it) })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.request_id_token))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)

        view.logOutBtn.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent= Intent(activity, LoginScreen::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        view.addNewRoomBtn.setOnClickListener {
            communicator.nextFragment()
        }

        return view
    }

    private fun listenToRooms() {
        FirebaseService().getUserRoomsCollection()?.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val rooms = ArrayList<Room>()
            value?.documents?.forEach{
                val room = it.toObject(Room::class.java)
                room?.id = it.id
                rooms.add(room!!)
            }

           _roomsLiveData.value = rooms
        }
    }

    internal var roomsLiveData:MutableLiveData<ArrayList<Room>>
        get() {return _roomsLiveData}
        set(value) {_roomsLiveData = value}
}