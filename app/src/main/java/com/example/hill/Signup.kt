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

        binding.SignupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword= binding.signupPassword.text.toString()

            if(signupUsername.isNotEmpty() && signupPassword.isNotEmpty()){
                signup(signupUsername,signupPassword)
            }else{
                Toast.makeText(this@Signup,"All fields are mandentory",Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginredirect.setOnClickListener{

            startActivity(Intent(this@Signup,LoginActivity::class.java))
            finish()

        }



    }

    private fun signup(username:String, password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val id=databaseReference.push().key
                    val userData=UserData(id,username,password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@Signup,"Signup Successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Signup,LoginActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@Signup,"User Already Exist",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Signup,"Database Error: ${databaseError.message}",Toast.LENGTH_SHORT).show()
            }


        })

    }
}