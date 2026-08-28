package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.R
import com.example.chambaya.databinding.ItemChipCategoriaBinding

class AdaptadorChipCategoria(
    private val categories: List<String>,
    private var selectedCategory: String = "Todas",
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<AdaptadorChipCategoria.ChipViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemChipCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    override fun getItemId(position: Int): Long = categories[position].hashCode().toLong()

    inner class ChipViewHolder(private val binding: ItemChipCategoriaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.tvChipLabel.text = category
            val isSelected = category.equals(selectedCategory, ignoreCase = true)

            if (isSelected) {
                binding.tvChipLabel.setBackgroundResource(R.drawable.bg_tag_category)
                binding.tvChipLabel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.primary))
            } else {
                binding.tvChipLabel.setBackgroundResource(R.drawable.bg_pill_chip)
                binding.tvChipLabel.setTextColor(ContextCompat.getColor(binding.root.context, R.color.text_secondary))
            }

            binding.tvChipLabel.setOnClickListener {
                if (category == selectedCategory) return@setOnClickListener
                val oldPosition = categories.indexOf(selectedCategory)
                val newPosition = bindingAdapterPosition
                if (newPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                selectedCategory = category
                if (oldPosition >= 0) notifyItemChanged(oldPosition)
                notifyItemChanged(newPosition)
                onCategorySelected(category)
            }
        }
    }
}
