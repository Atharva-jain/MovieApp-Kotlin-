package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.model.Result

class UpcomingAdapter(val listener: OnClickedUpcoming): RecyclerView.Adapter<UpcomingAdapter.UpcomingHolder>() {
    class UpcomingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val upcomingView: CardView = itemView.findViewById(R.id.upcomingView)
        val upcomingImage: ImageView = itemView.findViewById(R.id.ivUpcoming)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingHolder {
        return UpcomingHolder(LayoutInflater.from(parent.context).inflate(R.layout.upcoming_movie_layout, parent, false))
    }

    override fun onBindViewHolder(holder: UpcomingHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.backdrop_path}"
        Glide.with(holder.upcomingImage.context).load(url).into(holder.upcomingImage)
        holder.upcomingView.setOnClickListener {
            listener.onClickedUpcoming(result)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
interface OnClickedUpcoming{
    fun onClickedUpcoming(result: Result)
}