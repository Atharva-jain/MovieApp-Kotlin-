package com.example.movieapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.model.Result

class RecommendationAdapter(val listener: OnClickedRecommendation): RecyclerView.Adapter<RecommendationAdapter.RecommendationHolder>() {
    class RecommendationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recommendationView: CardView = itemView.findViewById(R.id.cvRecommendationView)
        val recommendationDetails: CardView = itemView.findViewById(R.id.cvRecommendationDetails)
        val recommendationImage: ImageView = itemView.findViewById(R.id.ivRecommendation)
        val recommendationTitle: TextView = itemView.findViewById(R.id.tvRecommendationTitle)
        val recommendationAvgVote: TextView = itemView.findViewById(R.id.tvRecommendationAvgVote)
    }

    val differCallback = object : DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.poster_path == newItem.poster_path
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationHolder {
        return RecommendationHolder(LayoutInflater.from(parent.context).inflate(R.layout.recommendation_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecommendationHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.backdrop_path}"
        Glide.with(holder.recommendationImage.context).load(url).into(holder.recommendationImage)
        holder.recommendationTitle.text = result.title
        try {
            val voteAverage = result.vote_average.toString().replace(".", "")
            holder.recommendationAvgVote.text = "$voteAverage%"
            val voteInteger = Integer.parseInt(voteAverage)
            if (voteInteger == 0) {
                holder.recommendationAvgVote.text = "NA"
            }
        } catch (e: Exception) { holder.recommendationAvgVote.text = "NA" }
        holder.recommendationView.setOnClickListener {
            listener.onClickedRecommendation(result)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
interface OnClickedRecommendation{
    fun onClickedRecommendation(result: Result)
}