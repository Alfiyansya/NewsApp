package com.alfiansyah.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alfiansyah.newsapp.R
import com.alfiansyah.newsapp.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private var _binding: ActivityNewsBinding?=null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding?.apply {
            bottomNavigationView.setupWithNavController(navController)
        }
    }
}