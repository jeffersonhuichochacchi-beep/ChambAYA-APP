package com.example.chambaya.ui.jobs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chambaya.R
import com.example.chambaya.data.model.JobOffer
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.FragmentoFeedTrabajosBinding
import com.example.chambaya.ui.adapters.AdaptadorChipCategoria
import com.example.chambaya.ui.adapters.AdaptadorTrabajo

class FragmentoFeedTrabajos : Fragment() {

    private var _binding: FragmentoFeedTrabajosBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var jobAdapter: AdaptadorTrabajo
    private lateinit var categoryAdapter: AdaptadorChipCategoria

    private var currentDistrict: String = "Todos"
    private var currentCategory: String = "Todas"
    private var currentQuery: String = ""
    private var isFirstResume = true

    private val categoriesList = listOf(
        "Todas", "Albañilería", "Pintura", "Limpieza", "Gasfitería",
        "Electricidad", "Mudanzas", "Cocina", "Jardinería", "Carpintería", "Ventas"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoFeedTrabajosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ChambayaRepository.getInstance(requireContext())

        setupAdapters()
        setupListeners()
        loadJobs()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            loadJobs()
        }
    }

    private fun setupAdapters() {
        jobAdapter = AdaptadorTrabajo(
            jobs = emptyList(),
            onJobClick = { job ->
                val intent = Intent(requireContext(), ActividadDetalleTrabajo::class.java).apply {
                    putExtra("JOB_ID", job.id)
                }
                startActivity(intent)
            },
            onApplyClick = { job ->
                val isApplied = repository.toggleApplyJob(job.id)
                val msg = if (isApplied) "¡Postulaste a '${job.title}' con éxito!" else "Postulación cancelada"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                loadJobs()
            }
        )
        binding.rvJobs.adapter = jobAdapter

        categoryAdapter = AdaptadorChipCategoria(
            categories = categoriesList,
            selectedCategory = currentCategory,
            onCategorySelected = { category ->
                currentCategory = category
                loadJobs()
            }
        )
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun setupListeners() {
        // District filter chips
        binding.chipGroupDistricts.setOnCheckedStateChangeListener { _, checkedIds ->
            currentDistrict = when (checkedIds.firstOrNull()) {
                R.id.chipDistCentro -> "Ayacucho Centro"
                R.id.chipDistCarmenAlto -> "Carmen Alto"
                R.id.chipDistSanJuan -> "San Juan Bautista"
                R.id.chipDistJesusNazareno -> "Jesús Nazareno"
                R.id.chipDistAndresAvelino -> "Andrés Avelino Cáceres"
                else -> "Todos"
            }
            loadJobs()
        }

        // Search text watcher
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString()?.trim() ?: ""
                binding.btnClearSearch.visibility = if (currentQuery.isNotEmpty()) View.VISIBLE else View.GONE
                loadJobs()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        binding.btnBannerPublish.setOnClickListener {
            // Switch to publish tab on main activity
            (activity as? com.example.chambaya.MainActivity)?.navigateToTab(R.id.nav_publish)
        }
    }

    private fun loadJobs() {
        val jobs = repository.getJobs(
            query = currentQuery,
            district = currentDistrict,
            category = currentCategory
        )

        jobAdapter.updateData(jobs)
        binding.tvResultsCount.text = "Ofertas Disponibles (${jobs.size})"
        binding.tvActiveFilters.text = if (currentDistrict == "Todos") "Todo Ayacucho" else currentDistrict

        if (jobs.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvJobs.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvJobs.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
