package com.example.chambaya.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.FragmentoListaChatsBinding
import com.example.chambaya.ui.adapters.AdaptadorConversacionChat

class FragmentoListaChats : Fragment() {

    private var _binding: FragmentoListaChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var conversationAdapter: AdaptadorConversacionChat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoListaChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ChambayaRepository.getInstance(requireContext())

        conversationAdapter = AdaptadorConversacionChat(
            conversations = emptyList(),
            onConversationClick = { conv ->
                val intent = Intent(requireContext(), ActividadConversacionChat::class.java).apply {
                    putExtra("CONVERSATION_ID", conv.id)
                    putExtra("OTHER_USER_NAME", conv.otherUserName)
                    putExtra("JOB_TITLE", conv.jobTitle)
                }
                startActivity(intent)
            }
        )
        binding.rvConversations.adapter = conversationAdapter

        loadConversations()
    }

    override fun onResume() {
        super.onResume()
        loadConversations()
    }

    private fun loadConversations() {
        val list = repository.getConversations()
        conversationAdapter.updateData(list)

        if (list.isEmpty()) {
            binding.layoutEmptyChat.visibility = View.VISIBLE
            binding.rvConversations.visibility = View.GONE
        } else {
            binding.layoutEmptyChat.visibility = View.GONE
            binding.rvConversations.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
