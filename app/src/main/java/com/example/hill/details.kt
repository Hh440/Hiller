package com.example.hill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hill.databinding.ActivityDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class details : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        binding.continueBttn.setOnClickListener {
            saveDetails()
        }



    }


    private fun saveDetails() {
        val userId = intent.getStringExtra("userId") // Get userId from the login activity

        val name = binding.nameDeatils.text.toString().trim()
        val email = binding.EmailAddress.text.toString().trim()
        val gender = if (binding.radioButtonMale.isChecked) "Male" else "Female"

        if (name.isNotEmpty() && email.isNotEmpty() && gender.isNotEmpty()) {
            val userDetails = UserDetails(userId, name, email, gender)

            databaseReference.child(userId!!).child("details").setValue(userDetails)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@details, "Details Saved Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@details, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@details, "Failed to save details", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this@details, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
    }




}