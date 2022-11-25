package com.alfiansyah.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfiansyah.adapter.NewsAdapter
import com.alfiansyah.newsapp.R
import com.alfiansyah.newsapp.database.ArticleDatabase
import com.alfiansyah.newsapp.databinding.FragmentBreakingNewsBinding
import com.alfiansyah.newsapp.repository.NewsRepository
import com.alfiansyah.newsapp.ui.NewsViewModel
import com.alfiansyah.newsapp.ui.NewsViewModelProviderFactory
import com.alfiansyah.newsapp.util.Resource

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding
    private val sharedViewModel: NewsViewModel by activityViewModels {
        NewsViewModelProviderFactory(NewsRepository(ArticleDatabase(requireActivity())))
    }
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
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
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
        sharedViewModel.breakingNews.observe(viewLifecycleOwner) { response ->
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

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLastPage
        }

    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding?.apply {
            rvBreakingNews.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

    }
}