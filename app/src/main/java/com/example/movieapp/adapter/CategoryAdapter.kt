package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.model.Category
import com.example.movieapp.model.GenreX

class CategoryAdapter(val listener: OnClickCategory): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryView: CardView = itemView.findViewById(R.id.categoryView)
        val categoryImage: ImageView = itemView.findViewById(R.id.ivCategory)
        val categoryText: TextView = itemView.findViewById(R.id.tvCategory)
    }

    private val differCallback = object : DiffUtil.ItemCallback<GenreX>(){
        override fun areItemsTheSame(oldItem: GenreX, newItem: GenreX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GenreX, newItem: GenreX): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_layout, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val genre = differ.currentList[position]
        when (genre.name) {
            "Action" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_action)
            }
            "Adventure" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_adventure)
            }
            "Animation" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_animation)
            }
            "Comedy" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_comedy)
            }
            "Crime" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_crime)
            }
            "Documentary" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_documnetation)
            }
            "Drama" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_drama)
            }
            "Family" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_family)
            }
            "Fantasy" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_fantasy)
            }
            "History" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_history)
            }
            "Horror" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_horror)
            }
            "Music" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_music)
            }
            "Mystery" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_mystery)
            }
            "Romance" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_romance)
            }
            "Science Fiction" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_science_fiction)
            }
            "TV Movie" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_tv_movie)
            }
            "Thriller" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_thriller)
            }
            "War" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_war)
            }
            "Western" -> {
                holder.categoryImage.setImageResource(R.drawable.ic_western)
            }
        }

        holder.categoryText.text = genre.name
        holder.categoryView.setOnClickListener {
            listener.onClickCategory(genre)
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}
interface OnClickCategory{
    fun onClickCategory(genreX: GenreX)
}