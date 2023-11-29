package com.example.didaktikapp.navigation

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.example.didaktikapp.MainMenuActivity

object NavigationUtil {

    fun navigateToMainMenu(activity: AppCompatActivity) {
        val intent = Intent(activity, MainMenuActivity::class.java)
        activity.startActivity(intent)
    }
}
