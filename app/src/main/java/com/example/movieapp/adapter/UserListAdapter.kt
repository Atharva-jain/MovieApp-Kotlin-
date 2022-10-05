package com.example.movieapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.model.Result
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class UserListAdapter(
    options: FirestoreRecyclerOptions<Result>,
    val listener: OnClickedListMovie
): FirestoreRecyclerAdapter<Result, UserListAdapter.UserListHolder>(options){
    private val TAG = "UserListAdapter"
    inner class UserListHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val searchView: CardView = itemView.findViewById(R.id.searchItemView)
        val movieImage: ImageView = itemView.findViewById(R.id.ivSearchMovieImage)
        val movieTitle: TextView = itemView.findViewById(R.id.tvSearchMovieTitle)
        val movieReleaseDate: TextView = itemView.findViewById(R.id.tvSearchMovieReleaseDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        return UserListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_movie_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserListHolder, position: Int, model: Result) {
        val url = "https://image.tmdb.org/t/p/original/${model.poster_path}"
        Glide.with(holder.movieImage.context).load(url).into(holder.movieImage)
        holder.movieTitle.text = model.title
        holder.movieReleaseDate.text = model.release_date
        holder.searchView.setOnClickListener {
            listener.onClickedListMovie(model)
        }
    }

    override fun onDataChanged() {
        super.onDataChanged()
        Log.d(TAG,"OnDataChanged View holder has called")
        if(snapshots.isEmpty()){
            Log.d(TAG,"List is empty")
            listener.isListEmpty(true)
        }else{
            Log.d(TAG,"List is not empty")
            listener.isListEmpty(false)
        }
    }
}
interface OnClickedListMovie{
    fun onClickedListMovie(result: Result)
    fun isListEmpty(value: Boolean)
}
