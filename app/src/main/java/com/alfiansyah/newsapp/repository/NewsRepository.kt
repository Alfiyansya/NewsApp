package com.alfiansyah.newsapp.repository

import com.alfiansyah.newsapp.api.RetrofitInstance
import com.alfiansyah.newsapp.database.ArticleDatabase

class NewsRepository(val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
}
