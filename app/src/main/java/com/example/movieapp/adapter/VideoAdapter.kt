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
import com.example.movieapp.model.ResultX
import kotlin.concurrent.fixedRateTimer

class VideoAdapter(val listener: OnVideoWatchClicked) :
    RecyclerView.Adapter<VideoAdapter.VideoAdapterHolder>() {
    class VideoAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: CardView = itemView.findViewById(R.id.cvVideoView)
        val videoImage: ImageView = itemView.findViewById(R.id.ivVideoImage)
    }

    val differCallback = object : DiffUtil.ItemCallback<ResultX>() {
        override fun areItemsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapterHolder {
        return VideoAdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.video_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoAdapterHolder, position: Int) {
        val result = differ.currentList[position]
        val url = "https://img.youtube.com/vi/${result.key}/sddefault.jpg"
        Glide.with(holder.videoImage.context).load(url).into(holder.videoImage)
        holder.videoView.setOnClickListener {
            listener.onVideoWatchClicked(result)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

interface OnVideoWatchClicked {
    fun onVideoWatchClicked(result: ResultX)
}