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
        "Mudanzas", "Cocina", "Jardinería", "Carpintería", "Ventas", "Cerrajería", "Otro Oficio"
    )

    private val districtsEmployer = arrayOf(
        "Ayacucho Centro", "Carmen Alto", "San Juan Bautista", "Jesús Nazareno", "Andrés Avelino Cáceres"
    )

    private val freeDaysOptions = arrayOf(
        "Fines de semana (Sáb - Dom)",
        "Lunes a Viernes",
        "Solo Tardes (todos los días)",
        "Todos los días (Inmediato)",
        "Días a coordinar"
    )

    private val scheduleOptions = arrayOf(
        "Jornada completa (8am - 6pm)",
        "Mañanas (8am - 1pm)",
        "Tardes (2pm - 7pm)",
        "Horario flexible",
        "Noches / Emergencias"
    )

    private val paymentTypes = arrayOf("por día", "por tarea / obra", "por hora", "por jornada")

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
        setupPlanListeners()
        setupSubmitListeners()
        adaptarVistaSegunRol()
    }

    override fun onResume() {
        super.onResume()
        adaptarVistaSegunRol()
    }

    private fun adaptarVistaSegunRol() {
        val isWorker = repository.currentRole == ChambayaRepository.ROLE_WORKER
        binding.layoutWorkerPublish.visibility = if (isWorker) View.VISIBLE else View.GONE
        binding.layoutEmployerPublish.visibility = if (isWorker) View.GONE else View.VISIBLE
    }

    private fun setupDropdowns() {
        // Empresa
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvEmpCategory.setAdapter(catAdapter)

        val distEmpAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districtsEmployer)
        binding.actvEmpDistrict.setAdapter(distEmpAdapter)

        val payEmpAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentTypes)
        binding.actvEmpPaymentType.setAdapter(payEmpAdapter)

        // Trabajador
        binding.actvWorkerCategory.setAdapter(catAdapter)

        val freeDaysAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, freeDaysOptions)
        binding.actvWorkerFreeDays.setAdapter(freeDaysAdapter)

        val schedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, scheduleOptions)
        binding.actvWorkerSchedule.setAdapter(schedAdapter)
    }

    private fun setupPlanListeners() {
        // Plan listeners – Empresa
        binding.cardEmpOptionBasic.setOnClickListener {
            binding.rbEmpBasic.isChecked = true
            binding.rbEmpFeatured.isChecked = false
        }
        binding.cardEmpOptionFeatured.setOnClickListener {
            binding.rbEmpFeatured.isChecked = true
            binding.rbEmpBasic.isChecked = false
        }
        binding.rbEmpBasic.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.rbEmpFeatured.isChecked = false
        }
        binding.rbEmpFeatured.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.rbEmpBasic.isChecked = false
        }

        // Plan listeners – Trabajador
        binding.cardWorkerOptionBasic.setOnClickListener {
            binding.rbWorkerBasic.isChecked = true
            binding.rbWorkerFeatured.isChecked = false
        }
        binding.cardWorkerOptionFeatured.setOnClickListener {
            binding.rbWorkerFeatured.isChecked = true
            binding.rbWorkerBasic.isChecked = false
        }
        binding.rbWorkerBasic.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.rbWorkerFeatured.isChecked = false
        }
        binding.rbWorkerFeatured.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.rbWorkerBasic.isChecked = false
        }
    }

    private fun setupSubmitListeners() {
        binding.btnEmpPublishSubmit.setOnClickListener { publishEmployerJob() }
        binding.btnWorkerPublishSubmit.setOnClickListener { publishWorkerAvailability() }
    }

    // ─────────────────────────────────────────────────────────────
    // PUBLICAR COMO EMPRESA
    // ─────────────────────────────────────────────────────────────
    private fun publishEmployerJob() {
        val title = binding.etEmpJobTitle.text.toString().trim()
        val category = binding.actvEmpCategory.text.toString().trim()
        val district = binding.actvEmpDistrict.text.toString().trim()
        val address = binding.etEmpAddress.text.toString().trim()
        val paymentStr = binding.etEmpPayment.text.toString().trim()
        val paymentType = binding.actvEmpPaymentType.text.toString().trim()
        val workersStr = binding.etEmpWorkersNeeded.text.toString().trim()
        val startDate = binding.etEmpStartDate.text.toString().trim()
        val schedule = binding.etEmpSchedule.text.toString().trim()
        val reqs = binding.etEmpRequirements.text.toString().trim()
        val isFeatured = binding.rbEmpFeatured.isChecked

        if (title.isEmpty()) {
            binding.etEmpJobTitle.error = "Ingresa el título del trabajo"
            binding.etEmpJobTitle.requestFocus()
            return
        }
        if (address.isEmpty()) {
            binding.etEmpAddress.error = "Ingresa la dirección exacta o punto de referencia"
            binding.etEmpAddress.requestFocus()
            return
        }

        val payment = paymentStr.toDoubleOrNull() ?: 100.0
        val workers = workersStr.toIntOrNull() ?: 1

        val finalDesc = if (reqs.isEmpty())
            "Se requiere $workers persona(s) para $title en $district. Pago de S/ $payment $paymentType."
        else reqs

        repository.publishJob(
            title = title,
            category = category,
            district = district,
            address = address,
            payment = payment,
            paymentType = paymentType,
            duration = "Por coordinar",
            schedule = if (schedule.isEmpty()) "Jornada completa" else schedule,
            workersNeeded = workers,
            date = if (startDate.isEmpty()) "Inmediato" else startDate,
            description = finalDesc,
            isFeatured = isFeatured
        )

        val fee = if (isFeatured) "S/ 5.00 (Destacada 🔥)" else "S/ 2.00 (Básica)"
        Toast.makeText(requireContext(), "¡Oferta publicada con éxito!\nTarifa: $fee", Toast.LENGTH_LONG).show()

        binding.etEmpJobTitle.text?.clear()
        binding.etEmpAddress.text?.clear()
        binding.etEmpRequirements.text?.clear()

        (activity as? com.example.chambaya.MainActivity)?.navigateToTab(R.id.nav_jobs)
    }

    // ─────────────────────────────────────────────────────────────
    // PUBLICAR COMO TRABAJADOR
    // ─────────────────────────────────────────────────────────────
    private fun publishWorkerAvailability() {
        val category = binding.actvWorkerCategory.text.toString().trim()
        val freeDays = binding.actvWorkerFreeDays.text.toString().trim()
        val schedule = binding.actvWorkerSchedule.text.toString().trim()
        val bioDesc = binding.etWorkerBioDesc.text.toString().trim()
        val isFeatured = binding.rbWorkerFeatured.isChecked

        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona la categoría de tu oficio", Toast.LENGTH_SHORT).show()
            return
        }

        val descCompleta = if (bioDesc.isEmpty())
            "Trabajador disponible en $category. Disponibilidad: $freeDays en horario $schedule."
        else
            "$bioDesc\n\nDisponibilidad: $freeDays ($schedule)."

        repository.publishJob(
            title = "⭐ DISPONIBLE: $category",
            category = category,
            district = "Huamanga",
            address = "Zonas de Huamanga",
            payment = 0.0,
            paymentType = "a coordinar",
            duration = freeDays,
            schedule = schedule,
            workersNeeded = 1,
            date = "Disponible $freeDays",
            description = descCompleta,
            isFeatured = isFeatured
        )

        val fee = if (isFeatured) "S/ 5.00 (Destacado 🔥)" else "S/ 2.00 (Básico)"
        Toast.makeText(
            requireContext(),
            "¡Tu disponibilidad fue publicada!\nTarifa: $fee\nAhora las empresas podrán encontrarte.",
            Toast.LENGTH_LONG
        ).show()

        binding.etWorkerBioDesc.text?.clear()

        (activity as? com.example.chambaya.MainActivity)?.navigateToTab(R.id.nav_jobs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
