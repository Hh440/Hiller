package com.example.hill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String? = null

    companion object {
        fun newInstance(userId: String?): Profile {
            val fragment = Profile()
            val args = Bundle()
            args.putString("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var nameProfile:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_profile, container, false)
         nameProfile= view.findViewById(R.id.name_profile)
        val settingButton: TextView = view.findViewById(R.id.setting)

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        // Retrieve userId from arguments
        userId = arguments?.getString("userId")

        // Fetch and display the name
        fetchAndDisplayUserName()

        settingButton.setOnClickListener {
            val intent = Intent(activity, Edit_profile::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }

        return view
    }

    private fun fetchAndDisplayUserName() {
        userId?.let { uid ->
            // Attach a listener to read the data at the user's node
            databaseReference.child(uid).child("details").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java)
                    if (userName != null) {
                        nameProfile.text = userName
                    } else {
                        Log.e("ProfileFragment", "Name not found in details node")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Log.e("ProfileFragment", "Database Error: ${error.message}")
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // You can also use userId here if needed
    }
}

