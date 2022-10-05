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

class MovieAdapter(private val listener: MovieTap): RecyclerView.Adapter<MovieAdapter.MovieHolder>() {
    class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieView: CardView = itemView.findViewById(R.id.cvMovieTap)
        val movieImage: ImageView = itemView.findViewById(R.id.ivMovieImage)
        val movieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        val movieReleaseDate: TextView = itemView.findViewById(R.id.tvMovieReleaseDate)
    }

    private val TAG: String = "MovieAdapter"
    private val differCallback = object : DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.poster_path == newItem.poster_path
        }
        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        return MovieHolder(LayoutInflater.from(parent.context).inflate(R.layout.show_movie_layout, parent, false))
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.poster_path}"
        Glide.with(holder.movieImage.context).load(url).into(holder.movieImage)
        holder.movieTitle.text = result.title
        holder.movieReleaseDate.text = result.release_date
        holder.movieView.setOnClickListener {
            listener.movieTap(result)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
interface MovieTap{
    fun movieTap(result: Result)
}