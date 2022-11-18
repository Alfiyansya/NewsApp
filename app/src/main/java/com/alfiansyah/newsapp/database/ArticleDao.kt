package com.alfiansyah.newsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alfiansyah.newsapp.ArticlesItem

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(articlesItem: ArticlesItem): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<ArticlesItem>>

    @Delete
    suspend fun deleteArticle(articlesItem: ArticlesItem)
}