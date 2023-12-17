package com.yogaap.tellme.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogaap.tellme.Data.di.timeStamptoString
import com.yogaap.tellme.R
import com.yogaap.tellme.Response.ListStoryItem
import com.yogaap.tellme.View.detail.DetailStoryActivity
import com.yogaap.tellme.databinding.StoriesRowBinding

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_ITEM_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            StoriesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class ListViewHolder(private val binding: StoriesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                tvTitleRow.text = story.name
                tvDescRow.text = story.description
                tvDateRow.text = story.createdAt.timeStamptoString()
                Glide.with(itemView.context)
                    .load(story.photo)
                    .fitCenter()
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    ).into(ivStoryRow)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)

                    intent.putExtra(DetailStoryActivity.NAME, story.name)
                    intent.putExtra(DetailStoryActivity.DESCRIPTION, story.description)
                    intent.putExtra(DetailStoryActivity.PHOTO, story.photo)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivStoryRow, "story"),
                            Pair(tvTitleRow, "name"),
                            Pair(tvDescRow, "desc")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldStory: ListStoryItem,
                newStory: ListStoryItem
            ): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(
                oldStory: ListStoryItem,
                newStory: ListStoryItem
            ): Boolean {
                return oldStory.name == newStory.name &&
                        oldStory.description == newStory.description &&
                        oldStory.photo == newStory.photo
            }
        }
    }
}