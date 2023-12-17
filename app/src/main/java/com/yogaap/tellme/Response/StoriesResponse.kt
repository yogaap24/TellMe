package com.yogaap.tellme.Response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class StoriesResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>
)

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class ListStoryItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photo: String,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("createdAt")
    val createdAt: String,
) : Parcelable
