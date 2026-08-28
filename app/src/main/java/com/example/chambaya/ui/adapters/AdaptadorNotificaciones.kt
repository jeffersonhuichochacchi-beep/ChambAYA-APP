package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.data.model.AppNotification
import com.example.chambaya.databinding.ItemNotificacionBinding

class AdaptadorNotificaciones(
    private val notifications: List<AppNotification>
) : RecyclerView.Adapter<AdaptadorNotificaciones.NotifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val binding = ItemNotificacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    inner class NotifViewHolder(private val binding: ItemNotificacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: AppNotification) {
            binding.tvNotifTitle.text = notif.title
            binding.tvNotifBody.text = notif.message
            binding.tvNotifTime.text = notif.timeAgo
        }
    }
}
