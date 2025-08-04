package com.example.supermarket.Activity

import android.os.Bundle
import com.example.supermarket.R

class HistoryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setupBottomNavigation()
    }
}