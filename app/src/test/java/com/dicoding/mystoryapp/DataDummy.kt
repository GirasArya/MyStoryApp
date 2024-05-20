package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.data.remote.response.StoryResponse

object DataDummy {
    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "photoUrl $i",
                createdAt = "created at  $i",
                name = "name  $i",
                description = "description $i",
                lon = i.toDouble(),
                id = "id $i",
                lat = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}