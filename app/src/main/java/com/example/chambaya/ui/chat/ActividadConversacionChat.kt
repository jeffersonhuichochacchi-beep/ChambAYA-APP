package com.example.chambaya.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.ActividadConversacionChatBinding
import com.example.chambaya.ui.adapters.AdaptadorMensajeChat

class ActividadConversacionChat : AppCompatActivity() {

    private lateinit var binding: ActividadConversacionChatBinding
    private lateinit var repository: ChambayaRepository
    private lateinit var messageAdapter: AdaptadorMensajeChat

    private var conversationId: String = ""
    private var otherUserName: String = ""
    private var jobTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadConversacionChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = ChambayaRepository.getInstance(this)

        conversationId = intent.getStringExtra("CONVERSATION_ID") ?: ""
        otherUserName = intent.getStringExtra("OTHER_USER_NAME") ?: "Contratante"
        jobTitle = intent.getStringExtra("JOB_TITLE") ?: "Chamba"

        setupToolbar()
        setupMessages()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbarChat.setNavigationOnClickListener { finish() }
        binding.tvChatUserName.text = otherUserName
        binding.tvChatJobRef.text = "Ref: $jobTitle"

        binding.btnChatCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:966451230")
            }
            startActivity(intent)
        }
    }

    private fun setupMessages() {
        val messages = repository.getMessages(conversationId)
        messageAdapter = AdaptadorMensajeChat(messages.toMutableList())
        binding.rvMessages.adapter = messageAdapter
        scrollToBottom()
    }

    private fun setupListeners() {
        binding.fabSendMessage.setOnClickListener {
            val text = binding.etChatMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.etChatMessage.text.clear()
            }
        }

        // Quick chip responses
        binding.chipQuickLocation.setOnClickListener {
            val locationMsg = "📍 Mi ubicación actual: Jr. 28 de Julio, cerca a Plaza Mayor de Huamanga."
            sendMessage(locationMsg, isLocation = true, location = "Plaza Mayor Huamanga")
        }

        binding.chipQuickConfirm.setOnClickListener {
            val confirmMsg = "🤝 Trato acordado para mañana a primera hora. ¡Muchas gracias!"
            sendMessage(confirmMsg)
        }

        binding.chipQuickPrice.setOnClickListener {
            val priceMsg = "💰 Mi propuesta de pago es de S/ 100 por día completo con herramientas propias."
            sendMessage(priceMsg, isProposal = true, price = 100.0)
        }
    }

    private fun sendMessage(
        text: String,
        isLocation: Boolean = false,
        location: String? = null,
        isProposal: Boolean = false,
        price: Double? = null
    ) {
        val newMsg = repository.sendMessage(
            conversationId = conversationId,
            text = text,
            isLocation = isLocation,
            locationAddress = location,
            isProposal = isProposal,
            proposedPrice = price
        )
        messageAdapter.addMessage(newMsg)
        scrollToBottom()

        // Simulate reply from the other person after 1.5 seconds if sent
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                simulateReply()
            }
        }, 1600)
    }

    private fun simulateReply() {
        val simulatedReplies = listOf(
            "Perfecto, anotado. Nos encontramos en la dirección acordada.",
            "De acuerdo, nos vemos mañana puntual a las 7:30 AM en obra.",
            "Listo, cuento contigo para este trabajo. ¡Gracias!",
            "Excelente, cualquier cosa te llamo al celular antes de las 8 AM."
        )
        val replyText = simulatedReplies.random()

        val replyMsg = repository.sendMessage(
            conversationId = conversationId,
            text = replyText
        ).copy(
            senderId = "OTHER",
            senderName = otherUserName,
            isMine = false
        )

        messageAdapter.addMessage(replyMsg)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        if (messageAdapter.itemCount > 0) {
            binding.rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }
    }
}
