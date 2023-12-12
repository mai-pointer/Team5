package com.example.didaktikapp

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class AdminLoginActivity : AppCompatActivity() {

    //Temporal, sin seguridad
    val contraseña = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        //Boton de login
        findViewById<Button>(R.id.btnValidate).setOnClickListener {
            if (contraseña == findViewById<EditText>(R.id.editTextPassword).text.toString()){
                val intent = Intent(this@AdminLoginActivity, MapsActivity::class.java)
                intent.putExtra("admin", true)
                startActivity(intent)
            }
            else{
                //Mensaje de error
                Toast.makeText(this@AdminLoginActivity, getString(R.string.incorrecto), Toast.LENGTH_SHORT).show()
            }
        }
        //Salir
        findViewById<Button>(R.id.btnBack).setOnClickListener{
            val intent = Intent(this@AdminLoginActivity, Ajustes::class.java)
            startActivity(intent)
        }
    }
}