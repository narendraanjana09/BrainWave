package com.nsa.brainwave

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.nsa.brainwave.home.HomeActivity
import com.nsa.brainwave.util.Util

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        analytics = Firebase.analytics
        Util.loadNoInternet(this,lifecycle)
    }

    fun goToHome(){
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

}