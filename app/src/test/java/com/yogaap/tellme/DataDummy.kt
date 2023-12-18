package com.yogaap.tellme

import com.yogaap.tellme.Response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "name $i",
                "description $i",
                i.toDouble(),
                i.toDouble(),
                "createdAt $i"
            )
            items.add(story)
        }
        return items
    }
}