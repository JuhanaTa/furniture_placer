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
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.fragment_main_page.view.*



class MainPageFragment(private val listener: RoomAdapter.OnItemClickListener) : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var communicator: Communicator
    private var isOpen: Boolean = true
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        view.menuBtn.setOnClickListener {
            //menu visible or gone logic
            if (isOpen){
                view.logOutBtn.visibility = View.VISIBLE
                view.logOutText.visibility = View.VISIBLE
                view.addNewRoomBtn.visibility = View.VISIBLE
                view.addNewRoomText.visibility = View.VISIBLE

                view.addNewRoomBtn.setOnClickListener {
                        communicator.openDialog(null, "")
                }
                isOpen = false

            } else {
                view.logOutBtn.visibility = View.GONE
                view.logOutText.visibility = View.GONE
                view.addNewRoomBtn.visibility = View.GONE
                view.addNewRoomText.visibility = View.GONE

                view.addNewRoomBtn.setOnClickListener(null)
                isOpen = true
            }
        }

        view.secondAddNewRoomBtn.setOnClickListener {
            communicator.openDialog(null, "")
        }


        communicator = activity as Communicator

        //Room data passed to RoomAdapter
        recyclerView = view.findViewById(R.id.rv_rooms)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val roomList = ArrayList<Room>()
        recyclerView.adapter =
            RoomAdapter(roomList, listener)

        //request id token passed
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ump = ViewModelProviders.of(this).get(MainViewModel::class.java) //ViewModelProvider(this).get(MainViewModel::class.java)
        ump.listenToRooms()
        ump.getRooms().observe( viewLifecycleOwner, Observer{

            recyclerView.adapter = RoomAdapter(it, listener)

            if (it.isEmpty()){
                noRoomsCard.visibility = View.VISIBLE
                rv_rooms.visibility = View.GONE
                yourRoomsCard.visibility = View.GONE
            } else {
                noRoomsCard.visibility = View.GONE
                rv_rooms.visibility = View.VISIBLE
                yourRoomsCard.visibility = View.VISIBLE
            }
        })
    }

}