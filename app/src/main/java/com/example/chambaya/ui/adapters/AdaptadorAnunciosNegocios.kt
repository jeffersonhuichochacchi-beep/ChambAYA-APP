package com.example.chambaya.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.data.model.BusinessAd
import com.example.chambaya.databinding.ItemAnuncioNegocioBinding

class AdaptadorAnunciosNegocios(
    private val ads: List<BusinessAd>
) : RecyclerView.Adapter<AdaptadorAnunciosNegocios.AdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val binding = ItemAnuncioNegocioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        holder.bind(ads[position])
    }

    override fun getItemCount(): Int = ads.size

    inner class AdViewHolder(private val binding: ItemAnuncioNegocioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: BusinessAd) {
            binding.tvAdBusinessName.text = ad.name
            binding.tvAdPromoBadge.text = ad.promoBadge
            binding.tvAdTagline.text = ad.tagline
            binding.tvAdAddress.text = "${ad.district} • ${ad.address}"

            binding.btnAdCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${ad.phone}")
                }
                binding.root.context.startActivity(intent)
            }
        }
    }
}
