package com.dicoding.mystoryapp.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.ui.detail.DetailActivity

class MainListAdapter : PagingDataAdapter<ListStoryItem, MainListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(storyItem: ListStoryItem?) {
            var image : ImageView = itemView.findViewById(R.id.img_story)
            var tvTitle : TextView = itemView.findViewById(R.id.tv_title)
            var tvDescription : TextView = itemView.findViewById(R.id.tv_description)

            Glide.with(itemView.context)
                .load(storyItem?.photoUrl)
                .into(image)
            tvTitle.text = storyItem?.name
            tvDescription.text = storyItem?.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("storyItem", storyItem)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(image, "image"),
                    Pair(tvTitle, "title"),
                    Pair(tvDescription, "description")
                )
                itemView.context.startActivity(intent, optionsCompat.toBundle())

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyPosition = getItem(position)
        holder.bind(storyPosition)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}