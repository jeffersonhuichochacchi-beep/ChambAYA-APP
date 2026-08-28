package com.example.chambaya.ui.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chambaya.R
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.FragmentoPublicarTrabajoBinding

class FragmentoPublicarTrabajo : Fragment() {

    private var _binding: FragmentoPublicarTrabajoBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository

    private val categories = arrayOf(
        "Albañilería", "Pintura", "Limpieza", "Gasfitería", "Electricidad",
        "Mudanzas", "Cocina", "Jardinería", "Carpintería", "Ventas", "Otro Oficio"
    )

    private val districts = arrayOf(
        "Ayacucho Centro", "Carmen Alto", "San Juan Bautista", "Jesús Nazareno", "Andrés Avelino Cáceres"
    )

    private val paymentTypes = arrayOf("por día", "por tarea", "por hora", "por jornada")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoPublicarTrabajoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ChambayaRepository.getInstance(requireContext())

        setupDropdowns()
        setupListeners()
    }

    private fun setupDropdowns() {
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(catAdapter)

        val distAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districts)
        binding.actvDistrict.setAdapter(distAdapter)

        val payAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentTypes)
        binding.actvPaymentType.setAdapter(payAdapter)
    }

    private fun setupListeners() {
        binding.cardOptionBasic.setOnClickListener {
            binding.rbBasic.isChecked = true
            binding.rbFeatured.isChecked = false
        }

        binding.cardOptionFeatured.setOnClickListener {
            binding.rbFeatured.isChecked = true
            binding.rbBasic.isChecked = false
        }

        binding.rbBasic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.rbFeatured.isChecked = false
        }

        binding.rbFeatured.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.rbBasic.isChecked = false
        }

        binding.btnSubmitPublish.setOnClickListener {
            publishJob()
        }
    }

    private fun publishJob() {
        val title = binding.etPublishTitle.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val district = binding.actvDistrict.text.toString().trim()
        val address = binding.etPublishAddress.text.toString().trim()
        val paymentStr = binding.etPublishPayment.text.toString().trim()
        val paymentType = binding.actvPaymentType.text.toString().trim()
        val workersStr = binding.etPublishWorkers.text.toString().trim()
        val date = binding.etPublishDate.text.toString().trim()
        val schedule = binding.etPublishSchedule.text.toString().trim()
        val duration = binding.etPublishDuration.text.toString().trim()
        val desc = binding.etPublishDesc.text.toString().trim()
        val isFeatured = binding.rbFeatured.isChecked

        if (title.isEmpty()) {
            binding.etPublishTitle.error = "Ingresa el título del trabajo"
            return
        }
        if (address.isEmpty()) {
            binding.etPublishAddress.error = "Ingresa la dirección de referencia en Ayacucho"
            return
        }
        val payment = paymentStr.toDoubleOrNull() ?: 50.0
        val workers = workersStr.toIntOrNull() ?: 1

        val finalDesc = if (desc.isEmpty()) "Trabajo en $district, pago puntual de S/ $payment $paymentType." else desc

        repository.publishJob(
            title = title,
            category = category,
            district = district,
            address = address,
            payment = payment,
            paymentType = paymentType,
            duration = if (duration.isEmpty()) "1 día" else duration,
            schedule = if (schedule.isEmpty()) "Jornada completa" else schedule,
            workersNeeded = workers,
            date = if (date.isEmpty()) "Inmediato" else date,
            description = finalDesc,
            isFeatured = isFeatured
        )

        val fee = if (isFeatured) "S/ 5.00 (Destacada 🔥)" else "S/ 2.00 (Básica)"
        Toast.makeText(requireContext(), "¡Chamba publicada con éxito!\nTarifa aplicada: $fee", Toast.LENGTH_LONG).show()

        // Clear fields
        binding.etPublishTitle.text?.clear()
        binding.etPublishAddress.text?.clear()
        binding.etPublishDesc.text?.clear()

        // Switch to feed
        (activity as? com.example.chambaya.MainActivity)?.navigateToTab(R.id.nav_jobs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
