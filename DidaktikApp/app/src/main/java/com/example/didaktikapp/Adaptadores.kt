package com.example.didaktikapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView


class PictureAnswerAdapter(private val context: Context, private var pictures: MutableMap<Char, Bitmap?>, private val callback: OnButtonClickListener) : BaseAdapter() {
    //Funciones del adaptador
    override fun getCount(): Int {
        return pictures.size
    }
    override fun getItem(position: Int): Any {
        return pictures.keys.toList()[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    interface OnButtonClickListener {
        fun onButtonClick(key: Char)
    }

    //Edita la view
    @SuppressLint("ResourceType")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        //Declara la view y el alamcen de los datos
        val view: View
        val elementos: Elementos

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.picture_choice, parent, false)
            elementos = Elementos() // declara la clase

            // Obtener las vistas del diseño
            elementos.image = view.findViewById(R.id.image)
            elementos.button = view.findViewById(R.id.radioBtn)
            view.tag = elementos
        } else {
            view = convertView
            elementos = view.tag as Elementos
        }

        val key = pictures.keys.toList()[position]
        val image = pictures[key]

        elementos.image.setImageBitmap(image)

        elementos.button.setOnClickListener{
            callback.onButtonClick(key)
        }

        return view
    }

    //Clase para guardar los elementos de la view
    private class Elementos {
        lateinit var image: ImageView
        lateinit var button: RadioButton
    }
}

class AnswerAdapter(private val context: Context, private var answers: MutableMap<Char, String?>, private val callback: OnButtonClickListener) : BaseAdapter() {
    //Funciones del adaptador
    override fun getCount(): Int {
        return answers.size
    }
    override fun getItem(position: Int): Any {
        return answers.keys.toList()[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    interface OnButtonClickListener {
        fun onButtonClick(key: Char)
    }

    //Edita la view
    @SuppressLint("ResourceType")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        //Declara la view y el alamcen de los datos
        val view: View
        val elementos: Elementos

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.answer_choice, parent, false)
            elementos = Elementos() // declara la clase

            // Obtener las vistas del diseño
            elementos.answer = view.findViewById(R.id.answerText)
            elementos.button = view.findViewById(R.id.radioBtn)
            view.tag = elementos
        } else {
            view = convertView
            elementos = view.tag as Elementos
        }

        val key = answers.keys.toList()[position]
        val respuesta = answers[key]

        elementos.answer.text = respuesta

        elementos.button.setOnClickListener{
            callback.onButtonClick(key)
        }

        return view
    }

    //Clase para guardar los elementos de la view
    private class Elementos {
        lateinit var answer: TextView
        lateinit var button: RadioButton
    }
}