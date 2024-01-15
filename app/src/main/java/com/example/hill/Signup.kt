package com.example.hill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hill.databinding.ActivitySignupBinding
import com.google.firebase.database.*

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference= firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupNumber = binding.signupNumber.text.toString()
            val signupPassword= binding.signupPassword.text.toString()

            if(signupUsername.isNotEmpty() && signupNumber.isNotEmpty() && signupPassword.isNotEmpty()){
                checkUserExists(signupNumber, signupUsername, signupPassword)
            }else{
                Toast.makeText(this@Signup,"All fields are mandentory",Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginredirect.setOnClickListener{

            startActivity(Intent(this@Signup,LoginActivity::class.java))
            finish()

        }



    }

    private fun checkUserExists(mobileNumber: String, username: String, password: String) {
        databaseReference.child(mobileNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@Signup, "User with this mobile number already exists", Toast.LENGTH_SHORT).show()
                } else {
                    signup(username, password, mobileNumber)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Signup, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signup(username: String, password: String, mobileNumber: String) {
        val userData = UserData(username = username, mobilenumber = mobileNumber, password = password )

        // Use mobile number as the key (ID) when saving to Firebase
        databaseReference.child(mobileNumber).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this@Signup, "Signup Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Signup, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@Signup, "Failed to sign up", Toast.LENGTH_SHORT).show()
            }
    }
}