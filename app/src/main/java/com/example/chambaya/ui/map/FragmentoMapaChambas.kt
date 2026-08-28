package com.example.chambaya.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chambaya.R
import com.example.chambaya.data.model.JobOffer
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.FragmentoMapaChambasBinding
import com.example.chambaya.ui.adapters.AdaptadorTrabajo
import com.example.chambaya.ui.jobs.ActividadDetalleTrabajo

class FragmentoMapaChambas : Fragment() {

    private var _binding: FragmentoMapaChambasBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var carouselAdapter: AdaptadorTrabajo
    private var selectedDistrict: String = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoMapaChambasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ChambayaRepository.getInstance(requireContext())

        setupCarousel()
        setupMap()
        setupListeners()
        loadMapData()
    }

    override fun onResume() {
        super.onResume()
        loadMapData()
    }

    private fun setupCarousel() {
        carouselAdapter = AdaptadorTrabajo(
            jobs = emptyList(),
            onJobClick = { job ->
                val intent = Intent(requireContext(), ActividadDetalleTrabajo::class.java).apply {
                    putExtra("JOB_ID", job.id)
                }
                startActivity(intent)
            },
            onApplyClick = { job ->
                val isApplied = repository.toggleApplyJob(job.id)
                val msg = if (isApplied) "¡Postulaste a '${job.title}'!" else "Postulación retirada"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                loadMapData()
            }
        )
        binding.rvMapJobsCarousel.adapter = carouselAdapter
    }

    private fun setupMap() {
        binding.ayacuchoMapView.onJobPinClickListener = { job ->
            binding.tvMapHint.text = "🎯 Chamba seleccionada: ${job.title}"
            val jobs = repository.getJobs(district = selectedDistrict)
            val index = jobs.indexOfFirst { it.id == job.id }
            if (index >= 0) {
                binding.rvMapJobsCarousel.smoothScrollToPosition(index)
            }
        }
    }

    private fun setupListeners() {
        binding.mapChipGroupDistricts.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedDistrict = when (checkedIds.firstOrNull()) {
                R.id.mapChipCentro -> "Ayacucho Centro"
                R.id.mapChipCarmen -> "Carmen Alto"
                R.id.mapChipSanJuan -> "San Juan Bautista"
                R.id.mapChipJesus -> "Jesús Nazareno"
                else -> "Todos"
            }
            loadMapData()
        }
    }

    private fun loadMapData() {
        val jobs = repository.getJobs(district = selectedDistrict)
        binding.ayacuchoMapView.setJobs(jobs)
        carouselAdapter.updateData(jobs)
        binding.tvActivePinCount.text = "${jobs.size} ofertas"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
