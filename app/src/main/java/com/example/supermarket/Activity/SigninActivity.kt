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


class SigninActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val SigninBtn = findViewById<Button>(R.id.SignBtn)
        val signupLink = findViewById<TextView>(R.id.tvLinkSignup)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        signupLink.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }

        SigninBtn.setOnClickListener {

            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(this, "Add Email!", Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "Add Password!", Toast.LENGTH_SHORT).show()
            }else {
                progressBar.visibility = View.VISIBLE

                FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Log in failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}