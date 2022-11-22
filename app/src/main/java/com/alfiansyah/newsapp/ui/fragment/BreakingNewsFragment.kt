package com.alfiansyah.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
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