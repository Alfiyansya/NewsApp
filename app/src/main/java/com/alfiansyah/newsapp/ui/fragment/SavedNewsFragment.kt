package com.alfiansyah.newsapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfiansyah.adapter.NewsAdapter
import com.alfiansyah.newsapp.R
import com.alfiansyah.newsapp.database.ArticleDatabase
import com.alfiansyah.newsapp.databinding.FragmentSavedNewsBinding
import com.alfiansyah.newsapp.repository.NewsRepository
import com.alfiansyah.newsapp.ui.NewsViewModel
import com.alfiansyah.newsapp.ui.NewsViewModelProviderFactory

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {
    private var _binding:FragmentSavedNewsBinding?=null
    private val binding get() = _binding
    private val sharedViewModel: NewsViewModel by activityViewModels(){
        NewsViewModelProviderFactory(NewsRepository(ArticleDatabase(requireActivity())))
    }
    lateinit var newsAdapter : NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding?.apply {
            rvSavedNews.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

    }
}