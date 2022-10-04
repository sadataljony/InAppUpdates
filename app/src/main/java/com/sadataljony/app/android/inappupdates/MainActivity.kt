package com.sadataljony.app.android.inappupdates

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sadataljony.app.android.inappupdates.utils.AppUpdater

class MainActivity : AppCompatActivity() {

    private lateinit var appUpdater: AppUpdater//add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateApp()//add this line
    }

    private fun updateApp() {//add this function
        appUpdater = AppUpdater(this)
        appUpdater.updateApp()
    }

    override fun onStop() {//add this function
        appUpdater.stop()
        super.onStop()
    }
}