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
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.model.Result

class SeeMoreMovieAdapter(val listener : SeeMoreMovieTap): RecyclerView.Adapter<SeeMoreMovieAdapter.SeeMoreMovieHolder>() {
    class SeeMoreMovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieView: CardView = itemView.findViewById(R.id.cvSeeMoreMovieTap)
        val movieImage: ImageView = itemView.findViewById(R.id.ivSeeMoreMovieImage)
        val movieTitle: TextView = itemView.findViewById(R.id.tvSeeMoreMovieTitle)
        val movieReleaseDate: TextView = itemView.findViewById(R.id.tvSeeMoreMovieReleaseDate)
    }

    private val TAG: String = "SeeMoreMovieAdapter"
    private val differCallback = object : DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.poster_path == newItem.poster_path
        }
        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeMoreMovieHolder {
        return SeeMoreMovieHolder(LayoutInflater.from(parent.context).inflate(R.layout.see_more_movie_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SeeMoreMovieHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.poster_path}"
        Glide.with(holder.movieImage.context).load(url).into(holder.movieImage)
        holder.movieTitle.text = result.title
        holder.movieReleaseDate.text = result.release_date
        holder.movieView.setOnClickListener {
            listener.movieTap(result)
        }
    }
}
interface SeeMoreMovieTap{
    fun movieTap(result: Result)
}
