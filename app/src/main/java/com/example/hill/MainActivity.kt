package com.example.hill

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.hill.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId= intent.getStringExtra("userId")


        loadFragment(Home.newInstance(userId), false)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(Home.newInstance(userId), false)
                R.id.search -> loadFragment(Search.newInstance(userId), false)
                R.id.profile -> loadFragment(Profile.newInstance(userId), false)
            }
            true
        }




    }

    private fun loadFragment(fragment: Fragment, flag: Boolean) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        if (flag) {
            ft.add(R.id.containerframe, fragment)
        } else {
            ft.replace(R.id.containerframe, fragment)
        }
        ft.commit()
    }
}