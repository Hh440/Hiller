package com.example.hill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hill.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
           binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


           firebaseDatabase=FirebaseDatabase.getInstance()
           databaseReference=firebaseDatabase.reference.child("users")

           binding.loginButton.setOnClickListener {

               val loginPassword= binding.loginPassword.text.toString()
               val loginMobileNumber = binding.loginNumber.text.toString()

               if( loginMobileNumber.isNotEmpty() && loginPassword.isNotEmpty()){
                   login(loginMobileNumber,loginPassword)
               }else{
                   Toast.makeText(this@LoginActivity,"All fields are mandentory",Toast.LENGTH_SHORT).show()
               }
           }

           binding.signupredirect.setOnClickListener {
               startActivity(Intent(this@LoginActivity,Signup::class.java))
               finish()
           }

    }
    private fun login(number: String, password: String) {
        databaseReference.child(number).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserData::class.java)

                    if (userData != null && userData.password == password) {
                        val userId = number
                        checkDetailsAndNavigate(userId)
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun checkDetailsAndNavigate(userId: String?) {
        if (userId != null) {
            databaseReference.child(userId).child("details").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Details exist, navigate to main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // Details don't exist, navigate to details activity
                        val detailsIntent = Intent(this@LoginActivity, details::class.java)
                        detailsIntent.putExtra("userId", userId)
                        startActivity(detailsIntent)
                        finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@LoginActivity, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }






}