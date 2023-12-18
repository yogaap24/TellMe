package com.yogaap.tellme.UI.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogaap.tellme.Response.ListStoryItem
import com.yogaap.tellme.UI.View.detail.DetailStoryActivity
import com.yogaap.tellme.databinding.StoriesRowBinding

class UnitTestAdapter : PagingDataAdapter<ListStoryItem, UnitTestAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            StoriesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    inner class MyViewHolder(private val binding: StoriesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemName: ListStoryItem) {
            binding.tvTitleRow.text = itemName.name
            Glide.with(binding.root)
                .load(itemName.photo)
                .into(binding.ivStoryRow)
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(DetailStoryActivity.ID, itemName.id)
                intentDetail.putExtra(DetailStoryActivity.NAME, itemName.name)
                intentDetail.putExtra(DetailStoryActivity.DESCRIPTION, itemName.description)
                intentDetail.putExtra(DetailStoryActivity.PHOTO, itemName.photo)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}