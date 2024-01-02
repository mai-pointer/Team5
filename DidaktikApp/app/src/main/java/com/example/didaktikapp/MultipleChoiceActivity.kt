package com.example.didaktikapp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MultipleChoiceActivity : AppCompatActivity() {

    private var isPictureAnswer = false
    private var question: String? = null
    private var answer1: String? = null
    private var answer2: String? = null
    private var answer3: String? = null

    private var picture1: Bitmap? = null
    private var picture2: Bitmap? = null
    private var picture3: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)


        initializeVariables()


    }

    private fun initializeVariables(){
        question = intent.getStringExtra("question")
        answer1 = intent.getStringExtra("answer1")
        answer2 = intent.getStringExtra("answer2")
        answer3 = intent.getStringExtra("answer3")
        picture1 = intent.getParcelableExtra("picture1")
        picture2 = intent.getParcelableExtra("picture2")
        picture3 = intent.getParcelableExtra("picture3")
    }
}