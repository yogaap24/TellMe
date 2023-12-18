package com.yogaap.tellme.UI.View.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogaap.tellme.R
import com.yogaap.tellme.databinding.ActivityDetailStoryBinding

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupData()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val ID = "ID"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val PHOTO = "PHOTO"

        var id: String? = null
        var name: String? = null
        var description: String? = null
        var photo: String? = null
    }

    private fun setupView() {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.story_detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupData() {
        val name = intent.getStringExtra(NAME)
        val description = intent.getStringExtra(DESCRIPTION)
        val photo = intent.getStringExtra(PHOTO)

        binding.apply {
            tvNameDetail.text = name
            tvDescDetail.text = description
            Glide.with(this@DetailStoryActivity)
                .load(photo)
                .fitCenter()
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                ).into(ivStoryDetail)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}