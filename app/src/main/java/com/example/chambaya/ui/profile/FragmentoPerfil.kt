package com.example.chambaya.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chambaya.R
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.FragmentoPerfilBinding
import com.example.chambaya.ui.adapters.AdaptadorReseñas
import com.example.chambaya.ui.dialogs.DialogoFragmentoAnunciosNegocios
import com.example.chambaya.ui.dialogs.DialogoFragmentoPlanPremium
import com.example.chambaya.ui.dialogs.DialogoFragmentoCalificarTrabajo

class FragmentoPerfil : Fragment() {

    private var _binding: FragmentoPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var reviewsAdapter: AdaptadorReseñas

    /** true = el usuario ingresó con "Empezar" (sin cuenta) */
    private var isGuestMode: Boolean = false

    companion object {
        private const val ARG_GUEST_MODE = "arg_guest_mode"

        /** Crea una instancia con el modo invitado configurado */
        fun newInstance(isGuestMode: Boolean): FragmentoPerfil {
            return FragmentoPerfil().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_GUEST_MODE, isGuestMode)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isGuestMode = arguments?.getBoolean(ARG_GUEST_MODE, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentoPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isGuestMode) {
            showGuestMode()
        } else {
            showProfileMode()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MODO INVITADO
    // ─────────────────────────────────────────────────────────────

    private fun showGuestMode() {
        binding.layoutGuestMode.visibility = View.VISIBLE
        binding.layoutProfileContent.visibility = View.GONE

        // Tarjeta y botón: Registrarme como Trabajador
        val openRegisterWorker = View.OnClickListener {
            val intent = Intent(requireContext(), com.example.chambaya.RegistroActivity::class.java).apply {
                putExtra(com.example.chambaya.RegistroActivity.EXTRA_USER_TYPE, com.example.chambaya.RegistroActivity.USER_TYPE_WORKER)
            }
            startActivity(intent)
        }
        binding.cardGuestWorker.setOnClickListener(openRegisterWorker)
        binding.btnGuestWorkerRegister.setOnClickListener(openRegisterWorker)

        // Tarjeta y botón: Registrarme como Empresa / Contratante
        val openRegisterEmployer = View.OnClickListener {
            val intent = Intent(requireContext(), com.example.chambaya.RegistroActivity::class.java).apply {
                putExtra(com.example.chambaya.RegistroActivity.EXTRA_USER_TYPE, com.example.chambaya.RegistroActivity.USER_TYPE_EMPLOYER)
            }
            startActivity(intent)
        }
        binding.cardGuestEmployer.setOnClickListener(openRegisterEmployer)
        binding.btnGuestEmployerRegister.setOnClickListener(openRegisterEmployer)

        // Enlace de inicio de sesión
        binding.tvGuestLoginLink.setOnClickListener {
            val intent = Intent(requireContext(), com.example.chambaya.LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MODO CON CUENTA
    // ─────────────────────────────────────────────────────────────

    private fun showProfileMode() {
        binding.layoutGuestMode.visibility = View.GONE
        binding.layoutProfileContent.visibility = View.VISIBLE

        repository = ChambayaRepository.getInstance(requireContext())

        setupReviews()
        setupListeners()
        loadProfileData()
    }

    override fun onResume() {
        super.onResume()
        if (!isGuestMode && ::repository.isInitialized) {
            loadProfileData()
        }
    }

    private fun setupReviews() {
        reviewsAdapter = AdaptadorReseñas(emptyList())
        binding.rvProfileReviews.adapter = reviewsAdapter
    }

    private fun setupListeners() {
        binding.btnSwitchRole.setOnClickListener {
            val newRole = repository.switchRole()
            val roleText = if (newRole == ChambayaRepository.ROLE_WORKER) "Trabajador (Buscar Chambas)" else "Contratante (Publicar)"
            Toast.makeText(requireContext(), "Modo cambiado a: $roleText", Toast.LENGTH_SHORT).show()
            loadProfileData()
        }

        binding.btnLeaveReview.setOnClickListener {
            val dialog = DialogoFragmentoCalificarTrabajo.newInstance { _ ->
                loadProfileData()
            }
            dialog.show(parentFragmentManager, "RateDialog")
        }

        binding.btnOpenLocalAds.setOnClickListener {
            val dialog = DialogoFragmentoAnunciosNegocios()
            dialog.show(parentFragmentManager, "AdsDialog")
        }

        binding.btnOpenPremiumPlan.setOnClickListener {
            val dialog = DialogoFragmentoPlanPremium()
            dialog.show(parentFragmentManager, "PremiumDialog")
        }
    }

    private fun loadProfileData() {
        val isWorker = repository.currentRole == ChambayaRepository.ROLE_WORKER

        if (isWorker) {
            val worker = repository.currentWorkerProfile
            binding.tvProfileName.text = worker.fullName
            binding.tvProfileAvatarInitial.text = worker.fullName.take(1).uppercase()
            binding.tvProfileDistrict.text = "${worker.district}, Ayacucho"
            binding.tvProfileRating.text = String.format("%.1f", worker.rating)
            binding.tvProfileReviewsCount.text = "${worker.reviewsCount} opiniones"
            binding.tvProfileJobsDone.text = "${worker.completedJobsCount}"
            binding.tvProfileExp.text = "${worker.experienceYears} años"
            binding.tvProfileBio.text = worker.bio
            binding.tvCurrentRoleLabel.text = "Modo: Buscando Chambas (Trabajador)"
            binding.btnSwitchRole.text = "Cambiar a Contratante"
        } else {
            val emp = repository.currentEmployerProfile
            binding.tvProfileName.text = emp.fullName
            binding.tvProfileAvatarInitial.text = emp.fullName.take(1).uppercase()
            binding.tvProfileDistrict.text = "${emp.district}, Ayacucho"
            binding.tvProfileRating.text = String.format("%.1f", emp.rating)
            binding.tvProfileReviewsCount.text = "Contratante verificado"
            binding.tvProfileJobsDone.text = "${emp.jobsPostedCount}"
            binding.tvProfileExp.text = "Activo"
            binding.tvProfileBio.text = "Contratante verificado en la ciudad de Huamanga. Publicando oportunidades para maestros y ayudantes."
            binding.tvCurrentRoleLabel.text = "Modo: Publicando Trabajos (Contratante)"
            binding.btnSwitchRole.text = "Cambiar a Trabajador"
        }

        val reviews = repository.getReviewsForUser(repository.currentWorkerProfile.id)
        reviewsAdapter.updateReviews(reviews)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
