package com.yogaap.tellme

import com.yogaap.tellme.Response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "id $i",
                "name + $i",
                "desc $i",
                "photoUrl $i",
                -7.569247,
                110.71106,
                "createdAt $i"
            )
            items.add(story)
        }
        return items
    }
}