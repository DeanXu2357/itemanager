package com.example.personalassetmanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.personalassetmanagement.ui.CategoriesFragment
import com.example.personalassetmanagement.ui.HomeFragment
//import com.example.personalassetmanagement.ui.SettingsFragment
import com.example.personalassetmanagement.ui.StatisticsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.navigation_categories -> {
                    loadFragment(CategoriesFragment())
                    true
                }

                R.id.navigation_statistics -> {
                    loadFragment(StatisticsFragment())
                    true
                }
//        R.id.navigation_settings -> {
//            loadFragment(SettingsFragment())
//            true
//        }
                else -> false
            }
        }


        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }
}
