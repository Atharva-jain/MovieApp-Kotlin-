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

class SearchMovieAdapter(val listener: SearchMovieHasClicked): RecyclerView.Adapter<SearchMovieAdapter.SearchMovieHolder>() {

    class SearchMovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchView: CardView = itemView.findViewById(R.id.searchItemView)
        val movieImage: ImageView = itemView.findViewById(R.id.ivSearchMovieImage)
        val movieTitle: TextView = itemView.findViewById(R.id.tvSearchMovieTitle)
        val movieReleaseDate: TextView = itemView.findViewById(R.id.tvSearchMovieReleaseDate)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.poster_path == newItem.poster_path
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieHolder {
        return SearchMovieHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_movie_layout, parent, false))
    }
    override fun onBindViewHolder(holder: SearchMovieHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.poster_path}"
        Glide.with(holder.movieImage.context).load(url).into(holder.movieImage)
        holder.movieTitle.text = result.title
        holder.movieReleaseDate.text = result.release_date
        holder.searchView.setOnClickListener {
            listener.searchMovieHasClicked(result)
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
interface SearchMovieHasClicked{
    fun searchMovieHasClicked(result: Result)
}