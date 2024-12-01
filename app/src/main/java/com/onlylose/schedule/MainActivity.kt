package com.onlylose.schedule

import SettingsFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainActivity : AppCompatActivity() {
    private lateinit var bottomBar: AnimatedBottomBar
    private var helper: ActivityHelper = ActivityHelper
    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        helper.initialize(Activity())
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val switchState = sharedPreferences.getBoolean("switchState", false)
        if (switchState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomBar = findViewById(R.id.bottom_bar)
        if (sharedPreferences.getInt("howmany", 0) > 0){
            replaceFragment(SettingsFragment())
            bottomBar.selectTabAt(3)
        }
        else{
            replaceFragment(ScheduleFragment())
        }
        changeStatusBarColor("#000000")
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            )
            {
                when (newIndex) {
                    0 -> replaceFragment(ScheduleFragment())
                    1 -> replaceFragment(MarksFragment())
                    2 -> replaceFragment(AboutFragment())
                    3 -> replaceFragment(SettingsFragment())
                }
            }
        })
    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragments, fragment)
            .commit()
    }

    private fun changeStatusBarColor(color: String) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = android.graphics.Color.parseColor(color)
    }
}