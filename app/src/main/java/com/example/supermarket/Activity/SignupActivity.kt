package com.example.supermarket.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supermarket.R
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val SignupBtn = findViewById<Button>(R.id.regButton)
        val username = findViewById<EditText>(R.id.etUsername)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginLink = findViewById<TextView>(R.id.tvLinkLogin)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE


        loginLink.setOnClickListener{
            startActivity(Intent(this, SigninActivity::class.java))
        }

        SignupBtn.setOnClickListener{

            val username = username.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if (TextUtils.isEmpty(username)){
                Toast.makeText(this, "Add Username!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(userEmail)){
                Toast.makeText(this, "Add Email!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(userPassword)){
                Toast.makeText(this, "Add Password!", Toast.LENGTH_SHORT).show()
            }else {
                progressBar.visibility = View.VISIBLE

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Successful registration!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SigninActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}