package com.alfiansyah.newsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alfiansyah.newsapp.models.ArticlesItem


@Database(
    entities = [ArticlesItem::class],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao


    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).fallbackToDestructiveMigration()
                .build()
    }

}