package com.example.hill

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hill.databinding.ActivityDetailsBinding
import com.example.hill.databinding.ActivityEditProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue



class Edit_profile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding



    private lateinit var databaseReference: DatabaseReference
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        databaseReference = FirebaseDatabase.getInstance().reference

       userId = intent.getStringExtra("userId")

        // Initialize SharedPreferencesManager
        userId?.let { fetchAndDisplayUserData(it) }
    }

    private fun fetchAndDisplayUserData(userId: String) {
        databaseReference.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val detailsSnapshot = snapshot.child("details")
                        val name = detailsSnapshot.child("name").getValue(String::class.java)
                        val email = detailsSnapshot.child("email").getValue(String::class.java)
                        val gender = detailsSnapshot.child("gender").getValue(String::class.java)
                        val username = snapshot.child("username").getValue(String::class.java)

                        // Display user data
                        binding.editName.hint = name
                        binding.editEmail.hint = email
                        binding.editGender.hint = gender
                        binding.editUsername.hint = username

                        // Set click listeners to make the fields editable
                        binding.editName.setOnClickListener { makeFieldEditable(binding.editName, "name", name) }
                        binding.editEmail.setOnClickListener { makeFieldEditable(binding.editEmail, "email", email) }
                        binding.editGender.setOnClickListener { makeFieldEditable(binding.editGender, "gender", gender) }
                        binding.editUsername.setOnClickListener { makeFieldEditable(binding.editUsername, "username", username) }
                    } else {
                        Toast.makeText(this@Edit_profile, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase Database Error: ${error.message}")
                    Toast.makeText(this@Edit_profile, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun makeFieldEditable(editText: EditText, fieldName: String, originalValue: String?) {
        // Enable editing by making the EditText focusable
        if (fieldName == "gender") {
            val context: Context = this@Edit_profile  // Replace with your actual activity class

            // Create a custom dialog with a dynamic cursor movement for gender selection
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Gender")

            val genderOptions = arrayOf("Male", "Female", "Other")
            val checkedItem = genderOptions.indexOf(originalValue)

            builder.setSingleChoiceItems(genderOptions, checkedItem) { dialog, which ->
                val selectedGender = genderOptions[which]

                // Save the selected gender to the database
                saveUpdatedValueToDatabase(fieldName, selectedGender)

                // Update the displayed text in the EditText
                editText.setText(selectedGender)

                // Dismiss the dialog
                dialog.dismiss()
            }

            // Show the dialog when the gender text is clicked
            val dialog = builder.create()
            editText.setOnClickListener {
                dialog.show()
            }
        }  else {
            // Create a dialog with an EditText for other fields
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Edit $fieldName")

            // Set up the input
            val input = EditText(this)
            input.setText(originalValue)
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton("OK") { dialog, _ ->
                val newValue = input.text.toString()
                if (newValue != originalValue) {
                    // Save the updated value to the database
                    saveUpdatedValueToDatabase(fieldName, newValue)
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            val dialog = builder.create()

            // Enable editing when the dialog is shown
            dialog.setOnShowListener {
                input.requestFocus()
                input.setSelection(input.text.length)
                input.isCursorVisible = true
            }

            dialog.show()
        }
    }

    private fun saveUpdatedValueToDatabase(fieldName: String, newValue: String) {
        // Implement the logic to update the database with the new value
        // For simplicity, let's assume you have a function to update the value
        // in the "details" node under the user's ID
        userId?.let {
            val userRef = databaseReference.child("users").child(it)

            // Check if the field is "gender" to handle it differently
            if (fieldName == "gender" || fieldName == "name" || fieldName == "email") {
                // For "gender," "name," and "email," update under "details" node
                userRef.child("details").child(fieldName).setValue(newValue)
            } else if (fieldName == "username") {
                // For "username," update directly under the user's ID
                userRef.child(fieldName).setValue(newValue)
            }

            // Fetch and display the updated user data
            fetchAndDisplayUserData(it)

            Log.d("EditProfile", "Updated $fieldName to $newValue")

            // Show a toast for successful update
            Toast.makeText(this, "Updated $fieldName to $newValue", Toast.LENGTH_SHORT).show()
        } ?: run {
            // Log an error if userId is null
            Log.e("EditProfile", "User ID is null. Unable to update data.")
            Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show()
        }
        }
    }


class GenderAdapter(private val context: Context, private val options: Array<String>, private val checkedItem: Int) :
    ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        if (position == checkedItem) {
            // Move cursor to a different option while selecting
            view.postDelayed({
                val editText = view.findViewById<EditText>(android.R.id.text1)
                editText.setSelection(editText.text.length)
            }, 100)
        }

        return view
    }
}



