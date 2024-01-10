package com.example.didaktikapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.didaktikapp.navigation.NavigationUtil
import com.example.didaktikapp.titleFragment.TitleFragment

class MultipleChoiceActivity : AppCompatActivity() {
    private val repeatActivityMenu = RepeatActivityMenu(this)

    private var isPictureAnswer: Boolean? = null
    private var question: String? = null
    private var correctAnswer: Char? = null
    private var answers: MutableMap<Char, String?> = mutableMapOf()
    private var pictures: MutableMap<Char, Bitmap?> = mutableMapOf()
    private var chosenAnswer: Char? = null

    private lateinit var galderaText: TextView
    private lateinit var aukerak: RecyclerView
    private lateinit var baieztatu: Button
    private lateinit var atzera: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

        initializeVariables()
        setupHeaderFragment(savedInstanceState)
        adaptAnswers()

        baieztatu.setOnClickListener{
            if (chosenAnswer?.equals(correctAnswer) == true){
                val intent = Intent(this ,  MultipleChoiceActivity::class.java)
                repeatActivityMenu.showGameOverDialog(this, intent)
            } else{

            }
        }
        atzera.setOnClickListener{

        }
    }
    private fun setupHeaderFragment(savedInstanceState: Bundle?) {
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainerView)
        if (savedInstanceState == null) {
            val titleFragment = TitleFragment.newInstance("Aukeratu erantzun zuzena")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, titleFragment, "titleFragmentTag")
                .commit()
        }
        val titleFragment =
            supportFragmentManager.findFragmentByTag("titleFragmentTag") as TitleFragment?
        titleFragment?.setOnHomeButtonClickListener {
            onHomeButtonClicked()
        }
    }

    private fun onHomeButtonClicked() {
        NavigationUtil.navigateToMainMenu(this)
    }

    private fun initializeVariables(){
        galderaText = findViewById(R.id.galdera)
        aukerak = findViewById(R.id.aukerak)
        baieztatu = findViewById(R.id.btnValidate)
        atzera = findViewById(R.id.btnBack)

        isPictureAnswer = intent.getBooleanExtra("isPicture", false)
        question = intent.getStringExtra("question")
        correctAnswer = intent.getCharExtra("correctAnswer", 'a')
        answers['a'] = intent.getStringExtra("answer1")
        answers['b'] = intent.getStringExtra("answer2")
        answers['c'] = intent.getStringExtra("answer3")

        pictures['a'] = intent.getParcelableExtra("picture1")
        pictures['b'] = intent.getParcelableExtra("picture2")
        pictures['c'] = intent.getParcelableExtra("picture3")
    }

    private fun adaptAnswers(){
        if(isPictureAnswer == true) {
            val adapter = PictureAnswerAdapter(this, pictures, object : PictureAnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                }
            })
            aukerak.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>

        }else{
            val adapter = AnswerAdapter(this, answers, object : AnswerAdapter.OnButtonClickListener {
                override fun onButtonClick(key: Char) {
                    chosenAnswer = key
                }
            })
            aukerak.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        }
    }

}