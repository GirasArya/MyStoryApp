package com.dicoding.mystoryapp.ui.detail

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storiesData= intent.getParcelableExtra<ListStoryItem>("storyItem") as ListStoryItem
        Glide.with(applicationContext)
            .load(storiesData.photoUrl)
            .into(findViewById(R.id.img_story))
        findViewById<TextView>(R.id.tv_title).text = storiesData.name
        findViewById<TextView>(R.id.tv_description).text = storiesData.description
    }
}