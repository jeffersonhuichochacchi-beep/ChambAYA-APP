package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.data.model.ChatConversation
import com.example.chambaya.databinding.ItemConversacionBinding
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorConversacionChat(
    private var conversations: List<ChatConversation>,
    private val onConversationClick: (ChatConversation) -> Unit
) : RecyclerView.Adapter<AdaptadorConversacionChat.ConvViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun updateData(newList: List<ChatConversation>) {
        this.conversations = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConvViewHolder {
        val binding = ItemConversacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConvViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    inner class ConvViewHolder(private val binding: ItemConversacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conv: ChatConversation) {
            binding.tvUserName.text = conv.otherUserName
            binding.tvAvatarInitial.text = conv.otherUserName.take(1).uppercase()
            binding.tvJobReference.text = "Ref: ${conv.jobTitle}"
            binding.tvLastMessage.text = conv.lastMessage
            binding.tvTime.text = timeFormat.format(Date(conv.lastMessageTime))

            if (conv.unreadCount > 0) {
                binding.tvUnreadBadge.visibility = View.VISIBLE
                binding.tvUnreadBadge.text = conv.unreadCount.toString()
            } else {
                binding.tvUnreadBadge.visibility = View.GONE
            }

            binding.layoutConversationItem.setOnClickListener {
                onConversationClick(conv)
            }
        }
    }
}
