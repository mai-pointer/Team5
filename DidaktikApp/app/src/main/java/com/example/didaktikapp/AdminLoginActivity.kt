package com.example.didaktikapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AdminLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orientation = resources.configuration.orientation

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_admin_login_portrait)
        } else {
            setContentView(R.layout.activity_admin_login_landscape)
        }
    }
}