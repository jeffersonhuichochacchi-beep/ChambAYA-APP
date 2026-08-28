package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.R
import com.example.chambaya.data.model.JobOffer
import com.example.chambaya.databinding.ItemTarjetaTrabajoBinding

class AdaptadorTrabajo(
    private var jobs: List<JobOffer>,
    private val onJobClick: (JobOffer) -> Unit,
    private val onApplyClick: (JobOffer) -> Unit
) : RecyclerView.Adapter<AdaptadorTrabajo.JobViewHolder>() {

    init {
        setHasStableIds(true)
    }

    fun updateData(newJobs: List<JobOffer>) {
        val snapshot = newJobs.map { it.copy() }
        val diff = DiffUtil.calculateDiff(JobDiffCallback(jobs, snapshot))
        jobs = snapshot
        diff.dispatchUpdatesTo(this)
    }

    override fun getItemId(position: Int): Long = jobs[position].id.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemTarjetaTrabajoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobs[position])
    }

    override fun getItemCount(): Int = jobs.size

    inner class JobViewHolder(private val binding: ItemTarjetaTrabajoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: JobOffer) {
            binding.tvCategory.text = job.category
            binding.tvJobTitle.text = job.title
            binding.tvDistrict.text = job.district
            binding.tvDistance.text = "• A ${String.format("%.1f", job.distanceKm)} km"
            binding.tvWorkersNeeded.text = "${job.workersNeeded} vacante(s)"
            binding.tvPayment.text = "S/ ${String.format("%.2f", job.payment)}"
            binding.tvPaymentType.text = "${job.paymentType} • ${job.duration}"

            // Featured badge & card styling
            if (job.isFeatured) {
                binding.tvFeaturedBadge.visibility = View.VISIBLE
                binding.cardJob.strokeColor = binding.root.context.getColor(R.color.accent_gold)
                binding.cardJob.strokeWidth = 3
            } else {
                binding.tvFeaturedBadge.visibility = View.GONE
                binding.cardJob.strokeColor = binding.root.context.getColor(R.color.divider)
                binding.cardJob.strokeWidth = 2
            }

            // Apply button state
            if (job.isAppliedByMe) {
                binding.btnApplyQuick.text = "Postulado ✅"
                binding.btnApplyQuick.setBackgroundColor(binding.root.context.getColor(R.color.success))
            } else {
                binding.btnApplyQuick.text = "Postular"
                binding.btnApplyQuick.setBackgroundColor(binding.root.context.getColor(R.color.primary))
            }

            binding.cardJob.setOnClickListener { onJobClick(job) }
            binding.btnApplyQuick.setOnClickListener { onApplyClick(job) }
        }
    }

    private class JobDiffCallback(
        private val oldItems: List<JobOffer>,
        private val newItems: List<JobOffer>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].id == newItems[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
}
