package com.example.chambaya.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.*
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class FragmentoMapaChambas : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentoMapaChambasBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var carouselAdapter: AdaptadorTrabajo
    private var selectedDistrict: String = "Todos"
    
    private var googleMap: GoogleMap? = null
    private val markersMap = mutableMapOf<String, Marker>()
    private var selectedJobId: String? = null

    // Centro de referencia: Huamanga / Plaza Mayor de Ayacucho
    private val centerHuamanga = LatLng(-13.1631, -74.2236)

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
        setupListeners()
        initGoogleMap()
    }

    override fun onResume() {
        super.onResume()
        loadMapData()
    }

    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configuración visual y controles del mapa
        with(map.uiSettings) {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = false
            isMapToolbarEnabled = false
            isCompassEnabled = true
        }

        // Mover cámara a Huamanga, Ayacucho
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerHuamanga, 14.5f))

        // Interacción con marcadores
        map.setOnMarkerClickListener { marker ->
            val job = marker.tag as? JobOffer
            if (job != null) {
                selectJob(job, fromMapMarker = true)
                true
            } else {
                false
            }
        }

        map.setOnMapClickListener {
            selectedJobId = null
            binding.tvMapHint.text = "💡 Toca cualquier punto en el mapa para ver detalles"
            refreshMarkers()
        }

        loadMapData()
    }

    private fun setupCarousel() {
        carouselAdapter = AdaptadorTrabajo(
            jobs = emptyList(),
            onJobClick = { job ->
                selectJob(job, fromMapMarker = false)
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

    private fun selectJob(job: JobOffer, fromMapMarker: Boolean) {
        selectedJobId = job.id
        binding.tvMapHint.text = "🎯 Chamba seleccionada: ${job.title}"

        // Si se hizo click desde el marcador, scrollear carrusel
        val jobs = repository.getJobs(district = selectedDistrict)
        val index = jobs.indexOfFirst { it.id == job.id }
        if (index >= 0 && fromMapMarker) {
            binding.rvMapJobsCarousel.smoothScrollToPosition(index)
        }

        // Animar cámara hacia el punto
        val targetPos = LatLng(job.latitude, job.longitude)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPos, 16f))

        refreshMarkers()
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
        carouselAdapter.updateData(jobs)
        binding.tvActivePinCount.text = "${jobs.size} ofertas"

        refreshMarkers()

        // Centrar vista en los resultados
        if (jobs.isNotEmpty() && googleMap != null) {
            if (jobs.size == 1) {
                val singleJob = jobs.first()
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(singleJob.latitude, singleJob.longitude),
                        15.5f
                    )
                )
            } else {
                val boundsBuilder = LatLngBounds.Builder()
                jobs.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
                try {
                    val bounds = boundsBuilder.build()
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
                } catch (e: Exception) {
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(centerHuamanga, 14f))
                }
            }
        }
    }

    private fun refreshMarkers() {
        val map = googleMap ?: return
        map.clear()
        markersMap.clear()

        val jobs = repository.getJobs(district = selectedDistrict)
        val context = context ?: return

        for (job in jobs) {
            val isSelected = job.id == selectedJobId
            val position = LatLng(job.latitude, job.longitude)
            val icon = createCustomMarkerBitmap(
                context = context,
                price = job.payment,
                isFeatured = job.isFeatured,
                isSelected = isSelected
            )

            val markerOptions = MarkerOptions()
                .position(position)
                .title(job.title)
                .snippet("S/ ${job.payment.toInt()} • ${job.district}")
                .icon(icon)
                .anchor(0.5f, 1.0f)
                .zIndex(if (isSelected) 10f else if (job.isFeatured) 5f else 1f)

            val marker = map.addMarker(markerOptions)
            if (marker != null) {
                marker.tag = job
                markersMap[job.id] = marker
            }
        }
    }

    /**
     * Genera un pin de mapa personalizado estilo Badge/Pill con el precio (S/ ...)
     * y paleta de colores ChambAYA (Índigo para normal, Cian para destacada, Índigo profundo para seleccionada).
     */
    private fun createCustomMarkerBitmap(
        context: Context,
        price: Double,
        isFeatured: Boolean,
        isSelected: Boolean
    ): BitmapDescriptor {
        val text = "S/${price.toInt()}"
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = dpToPx(context, 12f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.WHITE
        }

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val paddingH = dpToPx(context, 10f)
        val paddingV = dpToPx(context, 6f)
        val pointerHeight = dpToPx(context, 6f)
        val strokeWidth = dpToPx(context, 2f)

        val pillWidth = textBounds.width() + paddingH * 2
        val pillHeight = textBounds.height() + paddingV * 2
        val totalWidth = (pillWidth + strokeWidth * 2).toInt().coerceAtLeast(dpToPx(context, 48f).toInt())
        val totalHeight = (pillHeight + pointerHeight + strokeWidth * 2).toInt()

        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgColor = when {
            isSelected -> Color.parseColor("#312E81") // Índigo oscuro destacado
            isFeatured -> Color.parseColor("#0891B2") // Cian vibrante destacada
            else -> Color.parseColor("#4F46E5")       // Color primario ChambAYA
        }

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            style = Paint.Style.FILL
        }

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }

        val rectF = RectF(
            strokeWidth,
            strokeWidth,
            totalWidth.toFloat() - strokeWidth,
            pillHeight + strokeWidth
        )
        val cornerRadius = dpToPx(context, 12f)

        // Dibujar burbuja redondeada
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)

        // Dibujar indicador triangular en la parte inferior
        val centerX = totalWidth / 2f
        val pointerTop = pillHeight + strokeWidth
        val path = Path().apply {
            moveTo(centerX - dpToPx(context, 5f), pointerTop - dpToPx(context, 1f))
            lineTo(centerX + dpToPx(context, 5f), pointerTop - dpToPx(context, 1f))
            lineTo(centerX, totalHeight.toFloat() - strokeWidth)
            close()
        }
        canvas.drawPath(path, bgPaint)

        // Dibujar texto del precio centrado
        val textX = (totalWidth - textBounds.width()) / 2f - textBounds.left
        val textY = (pillHeight + strokeWidth + textBounds.height()) / 2f - dpToPx(context, 1f)
        canvas.drawText(text, textX, textY, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
