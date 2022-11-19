package com.alfiansyah.newsapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alfiansyah.newsapp.R
import com.alfiansyah.newsapp.database.ArticleDatabase
import com.alfiansyah.newsapp.databinding.ActivityNewsBinding
import com.alfiansyah.newsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {
    private var _binding: ActivityNewsBinding?=null
    private val binding get() = _binding
    private val viewModel by viewModels<NewsViewModel> {
        NewsViewModelProviderFactory(NewsRepository(ArticleDatabase(this)))
    }
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