package com.alfiansyah.newsapp.repository

import com.alfiansyah.newsapp.api.RetrofitInstance
import com.alfiansyah.newsapp.database.ArticleDatabase
import com.alfiansyah.newsapp.models.ArticlesItem

class NewsRepository(val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
    suspend fun upsertArticle(article: ArticlesItem) = db.getArticleDao().upsert(article)
    fun getSavedNews() = db.getArticleDao().getAllArticles()
    suspend fun deleteArticle(article: ArticlesItem) = db.getArticleDao().deleteArticle(article)
}
