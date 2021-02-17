package com.example.furniture_placer.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furniture_placer.*
import com.example.furniture_placer.adapters.RoomAdapter
import com.example.furniture_placer.data_models.Room
import com.example.furniture_placer.interfaces.Communicator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main_page.view.*


class MainPageFragment(private val listener: RoomAdapter.OnItemClickListener) : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var recyclerView: RecyclerView
    private lateinit var communicator: Communicator
    //private  var _roomsLiveData: MutableLiveData<ArrayList<Room>> = MutableLiveData<ArrayList<Room>>()
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // call update listener
        //listenToRooms()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        communicator = activity as Communicator

        recyclerView = view.findViewById(R.id.rv_rooms)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val roomList = ArrayList<Room>()
        recyclerView.adapter =
            RoomAdapter(roomList, listener)

        //_roomsLiveData.observe(this, Observer { recyclerView.adapter = RoomAdapter(it) })

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
            communicator.openDialog(null, "")

           /* var dialog = CreateRoomFragment()

            dialog.show(supporFragmentManager)*/
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ump = ViewModelProviders.of(this).get(MainViewModel::class.java) //ViewModelProvider(this).get(MainViewModel::class.java)
        ump.listenToRooms()
        ump.getRooms().observe( this, Observer{
            recyclerView.adapter =
                RoomAdapter(it, listener)
        })
    }

   /* private fun listenToRooms() {
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
        set(value) {_roomsLiveData = value}*/
}