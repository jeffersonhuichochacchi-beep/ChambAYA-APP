package com.example.chambaya.ui.jobs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.R
import com.example.chambaya.data.model.JobOffer
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.ActividadDetalleTrabajoBinding
import com.example.chambaya.ui.chat.ActividadConversacionChat
import com.example.chambaya.ui.dialogs.DialogoFragmentoReportar

class ActividadDetalleTrabajo : AppCompatActivity() {

    private lateinit var binding: ActividadDetalleTrabajoBinding
    private lateinit var repository: ChambayaRepository
    private var currentJob: JobOffer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadDetalleTrabajoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = ChambayaRepository.getInstance(this)

        val jobId = intent.getStringExtra("JOB_ID")
        currentJob = repository.getJobById(jobId ?: "")

        if (currentJob == null) {
            Toast.makeText(this, "Chamba no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        val job = currentJob ?: return

        binding.toolbarDetail.setNavigationOnClickListener { finish() }

        binding.tvDetailCategory.text = job.category
        binding.tvDetailTitle.text = job.title
        binding.tvDetailPayment.text = "S/ ${String.format("%.2f", job.payment)}"
        binding.tvDetailPaymentType.text = "Pago ${job.paymentType} • Se paga puntual"

        binding.tvDetailDistrict.text = "${job.district} (A ${String.format("%.1f", job.distanceKm)} km)"
        binding.tvDetailAddress.text = job.address
        binding.tvDetailSchedule.text = job.schedule
        binding.tvDetailDuration.text = "Duración: ${job.duration} • Inicia: ${job.date}"
        binding.tvDetailVacancies.text = "${job.workersNeeded} trabajador(es) requerido(s)"
        binding.tvDetailApplicants.text = "${job.applicantsCount} personas ya postularon"
        binding.tvDetailDescription.text = job.description

        // Employer info
        binding.tvDetailEmployerName.text = job.employerName
        binding.tvEmployerAvatarInitial.text = job.employerName.take(1).uppercase()
        binding.tvDetailEmployerRating.text = String.format("%.1f", job.employerRating)
        binding.tvDetailEmployerJobs.text = "• ${job.employerCompletedJobs} contrataciones"

        if (job.isFeatured) {
            binding.tvDetailFeatured.visibility = View.VISIBLE
        } else {
            binding.tvDetailFeatured.visibility = View.GONE
        }

        updateApplyButton()
    }

    private fun updateApplyButton() {
        val job = currentJob ?: return
        if (job.isAppliedByMe) {
            binding.btnDetailApply.text = "✅ Postulado (Cancelar postulación)"
            binding.btnDetailApply.setBackgroundColor(getColor(R.color.success))
        } else {
            binding.btnDetailApply.text = "Postular con un toque"
            binding.btnDetailApply.setBackgroundColor(getColor(R.color.primary))
        }
    }

    private fun setupListeners() {
        val job = currentJob ?: return

        binding.btnDetailApply.setOnClickListener {
            val isApplied = repository.toggleApplyJob(job.id)
            updateApplyButton()
            binding.tvDetailApplicants.text = "${job.applicantsCount} personas ya postularon"
            val msg = if (isApplied) "¡Postulaste a '${job.title}' con éxito!" else "Postulación retirada"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnChatEmployer.setOnClickListener {
            val conv = repository.getOrCreateConversation(job)
            val intent = Intent(this, ActividadConversacionChat::class.java).apply {
                putExtra("CONVERSATION_ID", conv.id)
                putExtra("OTHER_USER_NAME", conv.otherUserName)
                putExtra("JOB_TITLE", conv.jobTitle)
            }
            startActivity(intent)
        }

        binding.btnCallEmployer.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${job.employerPhone}")
            }
            startActivity(intent)
        }

        binding.tvReportJob.setOnClickListener {
            val dialog = DialogoFragmentoReportar.newInstance(job.title)
            dialog.show(supportFragmentManager, "ReportDialog")
        }
    }
}
