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
               val loginUsername = binding.loginUsername.text.toString()
               val loginPassword= binding.loginPassword.text.toString()

               if(loginUsername.isNotEmpty() && loginPassword.isNotEmpty()){
                   login(loginUsername,loginPassword)
               }else{
                   Toast.makeText(this@LoginActivity,"All fields are mandentory",Toast.LENGTH_SHORT).show()
               }
           }

           binding.signupredirect.setOnClickListener {
               startActivity(Intent(this@LoginActivity,Signup::class.java))
               finish()
           }

    }



    private fun login(username:String,password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val userData = userSnapshot.getValue(UserData::class.java)

                        if(userData != null && userData.password==password){
                            Toast.makeText(this@LoginActivity,"Login Successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                            return
                        }
                    }

                }

                Toast.makeText(this@LoginActivity,"Login Failed",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity,"Database Error: ${databaseError.message}",Toast.LENGTH_SHORT).show()
            }
        })
    }
}