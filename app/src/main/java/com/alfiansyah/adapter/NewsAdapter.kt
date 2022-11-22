package com.alfiansyah.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alfiansyah.newsapp.databinding.ItemArticlePreviewBinding
import com.alfiansyah.newsapp.models.ArticlesItem
import com.bumptech.glide.Glide

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) : ViewHolder(binding.root) {
        fun bind(article: ArticlesItem?) {
            binding.apply {
                tvDescription.text = article?.description
                tvSource.text = article?.source?.name
                tvTitle.text = article?.title
                tvPublishedAt.text = article?.publishedAt
                Glide.with(root.context).load(article?.urlToImage).into(ivArticleImage)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(article) }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
    private val differCallback = object : DiffUtil.ItemCallback<ArticlesItem>(){
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,differCallback)
    private var onItemClickListener: ((ArticlesItem) -> Unit)? = null
    fun setOnItemClickListener(listener: (ArticlesItem) -> Unit){
        onItemClickListener = listener
    }

}