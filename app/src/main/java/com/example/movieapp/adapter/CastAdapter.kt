package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.model.CastX

class CastAdapter : RecyclerView.Adapter<CastAdapter.CastHolder>() {
    class CastHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val castImage: ImageView = itemView.findViewById(R.id.ivCastImage)
        val castOriginalName: TextView = itemView.findViewById(R.id.tvCastOriginalName)
    }

    val differCallback = object : DiffUtil.ItemCallback<CastX>() {
        override fun areItemsTheSame(oldItem: CastX, newItem: CastX): Boolean {
            return oldItem.profile_path == newItem.profile_path
        }

        override fun areContentsTheSame(oldItem: CastX, newItem: CastX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastHolder {
        return CastHolder(LayoutInflater.from(parent.context).inflate(R.layout.cast_layout, parent, false))
    }

    override fun onBindViewHolder(holder: CastHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://image.tmdb.org/t/p/original/${result.profile_path}"
        Glide.with(holder.castImage.context).load(url).into(holder.castImage)
        holder.castOriginalName.text = result.original_name
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}