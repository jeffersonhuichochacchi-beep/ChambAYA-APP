package com.example.chambaya.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chambaya.data.model.ChatMessage
import com.example.chambaya.databinding.ItemMensajeChatBinding
import java.text.SimpleDateFormat
import java.util.*

class AdaptadorMensajeChat(
    private var messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<AdaptadorMensajeChat.MessageViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun updateMessages(newMessages: List<ChatMessage>) {
        this.messages = newMessages.toMutableList()
        notifyDataSetChanged()
    }

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMensajeChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(private val binding: ItemMensajeChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            val formattedTime = timeFormat.format(Date(msg.timestamp))

            if (msg.isMine) {
                binding.layoutOutgoing.visibility = View.VISIBLE
                binding.layoutIncoming.visibility = View.GONE
                binding.tvOutgoingText.text = msg.messageText
                binding.tvOutgoingTime.text = formattedTime
            } else {
                binding.layoutOutgoing.visibility = View.GONE
                binding.layoutIncoming.visibility = View.VISIBLE
                binding.tvIncomingSender.text = msg.senderName
                binding.tvIncomingText.text = msg.messageText
                binding.tvIncomingTime.text = formattedTime
            }
        }
    }
}
