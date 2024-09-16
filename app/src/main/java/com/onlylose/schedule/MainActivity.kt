package com.onlylose.schedule

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainActivity : AppCompatActivity() {
    private lateinit var bottomBar: AnimatedBottomBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBar = findViewById(R.id.bottom_bar)
        replaceFragment(ScheduleFragment())
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                Toast.makeText(this@MainActivity, "$newTab", Toast.LENGTH_SHORT).show()

                when(newIndex){
                    0 -> replaceFragment(ScheduleFragment())
                    1 -> replaceFragment(MarksFragment())
                    2 -> replaceFragment(AboutFragment())
                    3 -> replaceFragment(SettingsFragment())
                }
            }

        })
    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragments, fragment).commit()
    }

}