package com.example.supermarket.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.supermarket.Helper.LocaleHelper
import com.example.supermarket.R
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val sharedPrefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE)
        val lang = sharedPrefs.getString("My_Lang", "en") ?: "en"
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    protected fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                R.id.wishlist -> {
                    if (this !is WishListActivity) {
                        startActivity(Intent(this, WishListActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                R.id.cart -> {
                    if (this !is CartActivity) {
                        startActivity(Intent(this, CartActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                R.id.profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                else -> false
            }
        }
    }
}