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
    private val toolOptions = arrayOf("Propias completas", "Herramientas básicas", "Sin herramientas")

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
        adaptarVistaSegunRol()
    }

    override fun onResume() {
        super.onResume()
        adaptarVistaSegunRol()
    }

    private fun adaptarVistaSegunRol() {
        val isWorker = repository.currentRole == ChambayaRepository.ROLE_WORKER

        if (isWorker) {
            // MODO TRABAJADOR: Publicar tiempo libre / disponibilidad
            binding.tvPublishHeaderTitle.text = "Publicar mi Disponibilidad"
            binding.tvPublishHeaderSubtitle.text = "Publica tus días y horarios libres para que las empresas y clientes te contraten"

            binding.tilPublishTitle.hint = "Oficio / Servicio que ofreces (Ej: Maestro Pintor, Albañil, Electricista...)"
            binding.tilCategory.hint = "Tu Especialidad Principal"
            binding.tilDistrict.hint = "Distrito / Zona de tu disponibilidad"
            binding.tilAddress.hint = "Zonas de cobertura (Ej: Huamanga centro, Carmen Alto)"
            binding.tilPayment.hint = "Tarifa estimada (S/.)"
            binding.tilPaymentType.hint = "Cobro"
            
            // Campos exclusivos de trabajador
            binding.layoutWorkerExtraFields.visibility = View.VISIBLE
            binding.tilWorkers.visibility = View.GONE

            binding.tilDate.hint = "Días libres"
            binding.etPublishDate.setText("Fines de semana y tardes")
            binding.tilSchedule.hint = "Horarios disponibles"
            binding.etPublishSchedule.setText("2:00 PM - 7:00 PM")
            binding.tilDuration.hint = "Tipo de contrato"
            binding.etPublishDuration.setText("Por día o tarea")
            binding.tilDesc.hint = "Describe tu experiencia, herramientas que tienes y garantía de tu trabajo"

            binding.btnSubmitPublish.text = "⭐ Publicar mi Disponibilidad"
        } else {
            // MODO EMPRESA / CONTRATANTE: Publicar oferta de trabajo
            binding.tvPublishHeaderTitle.text = "Publicar Oferta de Chamba"
            binding.tvPublishHeaderSubtitle.text = "Conecta con trabajadores independientes de Ayacucho en minutos"

            binding.tilPublishTitle.hint = "Título del trabajo (Ej: Pintar fachada 2 pisos, Maestro Albañil...)"
            binding.tilCategory.hint = "Categoría / Oficio requerido"
            binding.tilDistrict.hint = "Distrito donde se realizará el trabajo"
            binding.tilAddress.hint = "Dirección exacta o referencia del lugar"
            binding.tilPayment.hint = "Pago Ofrecido (S/.)"
            binding.tilPaymentType.hint = "Modalidad"
            
            // Campos exclusivos de contratante
            binding.layoutWorkerExtraFields.visibility = View.GONE
            binding.tilWorkers.visibility = View.VISIBLE
            binding.etPublishWorkers.setText("2")

            binding.tilDate.hint = "Fecha de inicio"
            binding.etPublishDate.setText("Mañana")
            binding.tilSchedule.hint = "Horario"
            binding.etPublishSchedule.setText("8:00 AM - 5:00 PM")
            binding.tilDuration.hint = "Duración"
            binding.etPublishDuration.setText("1 a 2 días")
            binding.tilDesc.hint = "Descripción detallada del trabajo y requisitos"

            binding.btnSubmitPublish.text = "📢 Publicar Oferta de Chamba"
        }
    }

    private fun setupDropdowns() {
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(catAdapter)

        val distAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, districts)
        binding.actvDistrict.setAdapter(distAdapter)

        val payAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentTypes)
        binding.actvPaymentType.setAdapter(payAdapter)

        val toolAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, toolOptions)
        binding.actvWorkerTools.setAdapter(toolAdapter)
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
        val isWorker = repository.currentRole == ChambayaRepository.ROLE_WORKER
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
            val errorMsg = if (isWorker) "Ingresa el oficio o servicio que ofreces" else "Ingresa el título del trabajo"
            binding.etPublishTitle.error = errorMsg
            return
        }
        if (address.isEmpty()) {
            val errorMsg = if (isWorker) "Ingresa tu zona de cobertura en Ayacucho" else "Ingresa la dirección o referencia en Ayacucho"
            binding.etPublishAddress.error = errorMsg
            return
        }
        val payment = paymentStr.toDoubleOrNull() ?: 50.0
        val workers = workersStr.toIntOrNull() ?: 1

        val finalDesc = if (desc.isEmpty()) {
            if (isWorker)
                "Trabajador disponible en $district. Tarifa estimada S/ $payment $paymentType."
            else
                "Trabajo en $district, pago puntual de S/ $payment $paymentType."
        } else desc

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
        val exitoMsg = if (isWorker)
            "¡Disponibilidad publicada con éxito!\nAhora los contratantes podrán ver tu perfil disponible."
        else
            "¡Chamba publicada con éxito!\nTarifa aplicada: $fee"

        Toast.makeText(requireContext(), exitoMsg, Toast.LENGTH_LONG).show()

        // Limpiar campos
        binding.etPublishTitle.text?.clear()
        binding.etPublishAddress.text?.clear()
        binding.etPublishDesc.text?.clear()

        // Cambiar al feed principal
        (activity as? com.example.chambaya.MainActivity)?.navigateToTab(R.id.nav_jobs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
