package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.data.model.Review
import com.example.chambaya.databinding.ItemResenaBinding

class AdaptadorReseñas(
    private var reviews: List<Review>
) : RecyclerView.Adapter<AdaptadorReseñas.ReviewViewHolder>() {

    fun updateReviews(newReviews: List<Review>) {
        this.reviews = newReviews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemResenaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    inner class ReviewViewHolder(private val binding: ItemResenaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvReviewerName.text = "${review.reviewerName} (${review.reviewerRole})"
            binding.tvReviewDate.text = review.date
            binding.tvRatingScore.text = String.format("%.1f", review.rating)
            binding.tvJobTitleTag.text = "• ${review.jobTitle}"
            binding.tvComment.text = "\"${review.comment}\""
        }
    }
}
