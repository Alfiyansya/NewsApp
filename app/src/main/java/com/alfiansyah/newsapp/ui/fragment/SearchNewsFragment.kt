package com.alfiansyah.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfiansyah.adapter.NewsAdapter
import com.alfiansyah.newsapp.R
import com.alfiansyah.newsapp.database.ArticleDatabase
import com.alfiansyah.newsapp.databinding.FragmentSearchNewsBinding
import com.alfiansyah.newsapp.repository.NewsRepository
import com.alfiansyah.newsapp.ui.NewsViewModel
import com.alfiansyah.newsapp.ui.NewsViewModelProviderFactory
import com.alfiansyah.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.alfiansyah.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {
    private var _binding:FragmentSearchNewsBinding?=null
    private val binding get() = _binding
    private val sharedViewModel: NewsViewModel by activityViewModels(){
        NewsViewModelProviderFactory(NewsRepository(ArticleDatabase(requireActivity())))
    }
    private val TAG = "SearchNewsFragment"
    lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
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
                R.id.action_searchNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
        var job: Job? = null
        binding?.etSearch?.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        sharedViewModel.searchNews(editable.toString())
                    }
                }
            }
        }
        sharedViewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    showProgressbar(false)
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error ->{
                    showProgressbar(false)
                    response.message?.let { message ->
                        Log.e(TAG,"An error occured: $message")
                    }
                }
                is Resource.Loading ->{
                    showProgressbar(true)
                }

                else -> {
                    //Do nothing

                }
            }

        }
    }
    private fun showProgressbar(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) {
                paginationProgressBar.visibility = View.VISIBLE
            } else {
                paginationProgressBar.visibility = View.INVISIBLE
            }

        }

    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding?.apply {
            rvSearchNews.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

    }
}