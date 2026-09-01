package com.example.chambaya.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chambaya.BienvenidaActivity
import com.example.chambaya.RegistroActivity
import com.example.chambaya.data.auth.FirebaseGestorAutenticacion
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
    private lateinit var authGestor: FirebaseGestorAutenticacion
    private lateinit var reviewsAdapter: AdaptadorReseñas

    /** true = el usuario ingresó como invitado (sin cuenta) */
    private var isGuestMode: Boolean = false

    companion object {
        private const val ARG_GUEST_MODE = "arg_guest_mode"

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
        authGestor = FirebaseGestorAutenticacion.getInstance(requireContext())
        determinarModoVisual()
    }

    override fun onResume() {
        super.onResume()
        determinarModoVisual()
    }

    private fun determinarModoVisual() {
        if (isGuestMode) {
            showGuestMode()
        } else if (authGestor.estaAutenticado) {
            showProfileMode()
        } else {
            showGuestMode()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MODO INVITADO
    // ─────────────────────────────────────────────────────────────

    private fun showGuestMode() {
        binding.layoutGuestMode.visibility = View.VISIBLE
        binding.layoutProfileContent.visibility = View.GONE

        val openRegisterWorker = View.OnClickListener {
            val intent = Intent(requireContext(), RegistroActivity::class.java).apply {
                putExtra(RegistroActivity.EXTRA_USER_TYPE, RegistroActivity.USER_TYPE_WORKER)
            }
            startActivity(intent)
        }
        binding.cardGuestWorker.setOnClickListener(openRegisterWorker)
        binding.btnGuestWorkerRegister.setOnClickListener(openRegisterWorker)

        val openRegisterEmployer = View.OnClickListener {
            val intent = Intent(requireContext(), RegistroActivity::class.java).apply {
                putExtra(RegistroActivity.EXTRA_USER_TYPE, RegistroActivity.USER_TYPE_EMPLOYER)
            }
            startActivity(intent)
        }
        binding.cardGuestEmployer.setOnClickListener(openRegisterEmployer)
        binding.btnGuestEmployerRegister.setOnClickListener(openRegisterEmployer)
    }

    // ─────────────────────────────────────────────────────────────
    // MODO CON CUENTA (FIREBASE)
    // ─────────────────────────────────────────────────────────────

    private fun showProfileMode() {
        binding.layoutGuestMode.visibility = View.GONE
        binding.layoutProfileContent.visibility = View.VISIBLE

        repository = ChambayaRepository.getInstance(requireContext())

        setupReviews()
        setupListeners()
        loadProfileData()

        // Actualizar nombre y rol desde Firestore en segundo plano
        cargarDatosFirestore()
    }

    private fun setupReviews() {
        reviewsAdapter = AdaptadorReseñas(emptyList())
        binding.rvProfileReviews.adapter = reviewsAdapter
    }

    private fun setupListeners() {
        binding.btnSwitchRole.setOnClickListener {
            val newRole = repository.switchRole()
            val roleText = if (newRole == ChambayaRepository.ROLE_WORKER)
                "Trabajador (Buscar Chambas)"
            else
                "Contratante (Publicar)"
            Toast.makeText(requireContext(), "Modo cambiado a: $roleText", Toast.LENGTH_SHORT).show()
            loadProfileData()
        }

        binding.btnLeaveReview.setOnClickListener {
            val dialog = DialogoFragmentoCalificarTrabajo.newInstance { _ -> loadProfileData() }
            dialog.show(parentFragmentManager, "RateDialog")
        }

        binding.btnOpenLocalAds.setOnClickListener {
            DialogoFragmentoAnunciosNegocios().show(parentFragmentManager, "AdsDialog")
        }

        binding.btnOpenPremiumPlan.setOnClickListener {
            DialogoFragmentoPlanPremium().show(parentFragmentManager, "PremiumDialog")
        }

        binding.btnSignOut.setOnClickListener {
            authGestor.cerrarSesion()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), BienvenidaActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun loadProfileData() {
        val fbUser = authGestor.usuarioActual ?: return
        val isWorker = repository.currentRole == ChambayaRepository.ROLE_WORKER

        // Nombre: primero de Firebase Auth, sino del email
        val displayName = fbUser.displayName?.takeIf { it.isNotBlank() }
            ?: fbUser.email?.substringBefore("@")
            ?: "Usuario ChambAYA"

        binding.tvProfileName.text = displayName
        binding.tvProfileAvatarInitial.text = displayName.take(1).uppercase()

        if (isWorker) {
            val worker = repository.currentWorkerProfile
            binding.tvProfileDistrict.text = worker.district.ifBlank { "Huamanga, Ayacucho" }
            binding.tvProfileRating.text = String.format("%.1f", worker.rating)
            binding.tvProfileReviewsCount.text = "${worker.reviewsCount} opiniones"
            binding.tvProfileJobsDone.text = "${worker.completedJobsCount}"
            binding.tvProfileExp.text = "${worker.experienceYears} años"
            binding.tvProfileBio.text = worker.bio.ifBlank { "¡Hola! Soy nuevo en ChambAYA y estoy listo para trabajar." }
            binding.tvCurrentRoleLabel.text = "Modo: Buscando Chambas (Trabajador)"
            binding.btnSwitchRole.text = "Cambiar a Contratante"
        } else {
            val emp = repository.currentEmployerProfile
            binding.tvProfileDistrict.text = emp.district.ifBlank { "Huamanga, Ayacucho" }
            binding.tvProfileRating.text = String.format("%.1f", emp.rating)
            binding.tvProfileReviewsCount.text = "Contratante verificado"
            binding.tvProfileJobsDone.text = "${emp.jobsPostedCount}"
            binding.tvProfileExp.text = "Activo"
            binding.tvProfileBio.text = "Contratante en ChambAYA. Publicando oportunidades laborales en Ayacucho."
            binding.tvCurrentRoleLabel.text = "Modo: Publicando Trabajos (Contratante)"
            binding.btnSwitchRole.text = "Cambiar a Trabajador"
        }

        val reviews = repository.getReviewsForUser(repository.currentWorkerProfile.id)
        reviewsAdapter.updateReviews(reviews)
    }

    /**
     * Carga datos reales del perfil desde Firestore y actualiza la UI.
     */
    private fun cargarDatosFirestore() {
        val fbUser = authGestor.usuarioActual ?: return

        authGestor.firestore.collection("usuarios")
            .document(fbUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (!isAdded || _binding == null) return@addOnSuccessListener
                if (document == null || !document.exists()) return@addOnSuccessListener

                val nombre = document.getString("nombre")
                    ?: fbUser.displayName
                    ?: "Usuario ChambAYA"
                val rol = document.getString("rol") ?: RegistroActivity.USER_TYPE_WORKER

                // Sincronizar rol del repositorio local con el de Firestore
                val nuevoRol = if (rol == RegistroActivity.USER_TYPE_EMPLOYER)
                    ChambayaRepository.ROLE_EMPLOYER
                else
                    ChambayaRepository.ROLE_WORKER

                if (repository.currentRole != nuevoRol) {
                    repository.currentRole = nuevoRol
                }

                // Actualizar nombre y avatar con datos reales de Firestore
                binding.tvProfileName.text = nombre
                binding.tvProfileAvatarInitial.text = nombre.take(1).uppercase()

                if (nuevoRol == ChambayaRepository.ROLE_EMPLOYER) {
                    binding.tvCurrentRoleLabel.text = "Modo: Publicando Trabajos (Contratante)"
                    binding.btnSwitchRole.text = "Cambiar a Trabajador"
                } else {
                    binding.tvCurrentRoleLabel.text = "Modo: Buscando Chambas (Trabajador)"
                    binding.btnSwitchRole.text = "Cambiar a Contratante"
                }
            }
            .addOnFailureListener {
                // Si falla Firestore, los datos de Auth ya están mostrados
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
