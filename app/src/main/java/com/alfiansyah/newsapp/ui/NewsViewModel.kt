package com.alfiansyah.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alfiansyah.NewsApplication
import com.alfiansyah.newsapp.models.ArticlesItem
import com.alfiansyah.newsapp.models.NewsResponse
import com.alfiansyah.newsapp.repository.NewsRepository
import com.alfiansyah.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application,private val newsRepository: NewsRepository) : AndroidViewModel(app) {
    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews
    private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>> = _searchNews
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticlesItem = breakingNewsResponse?.articles
                    val newArticleItem = resultResponse.articles
                    oldArticlesItem?.addAll(newArticleItem)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticlesItem = searchNewsResponse?.articles
                    val newArticleItem = resultResponse.articles
                    oldArticlesItem?.addAll(newArticleItem)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(articlesItem: ArticlesItem) =
        viewModelScope.launch { newsRepository.upsertArticle(articlesItem) }

    fun getSavedNews() = newsRepository.getSavedNews()
    fun deleteArticle(articlesItem: ArticlesItem) =
        viewModelScope.launch { newsRepository.deleteArticle(articlesItem) }
    private suspend fun safeBreakingNewsCall(countryCode: String){
        _breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                _breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                _breakingNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t : Throwable){
            when(t){
                is IOException -> _breakingNews.postValue(Resource.Error("Network Failure"))
                else ->_breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun safeSearchNewsCall(searchQuery: String){
        _searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                _searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                _searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t : Throwable){
            when(t){
                is IOException -> _searchNews.postValue(Resource.Error("Network Failure"))
                else ->_searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}