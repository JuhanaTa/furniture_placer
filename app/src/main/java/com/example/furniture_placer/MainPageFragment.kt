package com.example.furniture_placer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main_page.view.*


class MainPageFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var recyclerView: RecyclerView

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        recyclerView = view.findViewById(R.id.rv_rooms)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val roomList = ArrayList<OneRoom>()
        roomList.add(OneRoom("kitchen", 1))
        roomList.add(OneRoom("living room", 2))
        roomList.add(OneRoom("bedroom", 3))
        recyclerView.adapter = RoomAdapter(roomList)

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
            val intent = Intent(activity, ArFragmentView::class.java).apply {
            }
            startActivity(intent)
        }

        return view
    }


}