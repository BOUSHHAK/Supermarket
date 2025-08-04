package com.example.supermarket.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.supermarket.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : BaseActivity() {

    private lateinit var profileUserEmail: TextView
    private lateinit var profileHistoryBtn: Button
    private lateinit var logOutBtn: Button
    private lateinit var languageSpinner: Spinner
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()

        profileUserEmail = findViewById(R.id.profileUserEmail)
        profileHistoryBtn = findViewById(R.id.profileHistoryBtn)
        languageSpinner = findViewById(R.id.spinner_language)
        logOutBtn = findViewById(R.id.logOutBtn)
        auth = FirebaseAuth.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmailEnc = currentUser?.email ?: return
        val userEmail = userEmailEnc.replace(",", ".")
        profileUserEmail.text = userEmail

        profileHistoryBtn.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        setupLanguageSpinner()

        logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }
    }
    private fun setupLanguageSpinner() {
        val languageList = arrayListOf("English", "Ελληνικά")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languageList)
        languageSpinner.adapter = adapter

        val sharedPrefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val currentLang = sharedPrefs.getString("My_Lang", "en")
        languageSpinner.setSelection(if (currentLang == "el") 1 else 0)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedLang = if (position == 0) "en" else "el"
                val savedLang = sharedPrefs.getString("My_Lang", "en")

                if (savedLang != selectedLang) {
                    val editor = sharedPrefs.edit()
                    editor.putString("My_Lang", selectedLang)
                    editor.apply()

                    val intent = intent
                    finish()
                    startActivity(intent)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}