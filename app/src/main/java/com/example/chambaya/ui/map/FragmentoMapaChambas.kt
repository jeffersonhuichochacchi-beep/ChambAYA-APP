package com.example.chambaya.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import java.io.File

class FragmentoMapaChambas : Fragment() {

    private var _binding: FragmentoMapaChambasBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var carouselAdapter: AdaptadorTrabajo
    private var selectedDistrict: String = "Todos"
    private var selectedJobId: String? = null

    // Servidor de mapas moderno, rápido y sin bloqueos (CartoDB Voyager)
    private val cartoVoyagerTileSource = XYTileSource(
        "CartoVoyager",
        0, 20, 256, ".png",
        arrayOf(
            "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://c.basemaps.cartocdn.com/rastertiles/voyager/"
        )
    )

    // Coordenadas de Huamanga / Plaza Mayor de Ayacucho
    private val centerHuamanga = GeoPoint(-13.1631, -74.2236)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val ctx = requireContext().applicationContext
        val config = Configuration.getInstance()
        val osmBaseDir = File(ctx.cacheDir, "osmdroid")
        if (!osmBaseDir.exists()) osmBaseDir.mkdirs()

        config.osmdroidBasePath = osmBaseDir
        config.osmdroidTileCache = File(osmBaseDir, "tiles")
        config.userAgentValue = "ChambAYA_Ayacucho_Android_App/1.0"
        config.load(ctx, ctx.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE))
    }

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

        setupMap()
        setupCarousel()
        setupListeners()
        loadMapData()
    }

    private fun setupMap() {
        val mapView = binding.osmMapView
        // Usar fuente de mosaicos CartoVoyager (estilo moderno y libre de restricciones)
        mapView.setTileSource(cartoVoyagerTileSource)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setDestroyMode(false)

        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(centerHuamanga)
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

        // Si se seleccionó desde el mapa, scrollear carrusel
        val jobs = repository.getJobs(district = selectedDistrict)
        val index = jobs.indexOfFirst { it.id == job.id }
        if (index >= 0 && fromMapMarker) {
            binding.rvMapJobsCarousel.smoothScrollToPosition(index)
        }

        // Mover mapa hacia la ubicación de la oferta
        val targetPoint = GeoPoint(job.latitude, job.longitude)
        binding.osmMapView.controller.animateTo(targetPoint, 16.0, 500L)

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

        // Ajuste seguro de cámara
        binding.osmMapView.post {
            if (_binding == null) return@post
            val mapView = binding.osmMapView
            if (mapView.width <= 0 || mapView.height <= 0) return@post

            if (jobs.size == 1) {
                val singleJob = jobs.first()
                mapView.controller.animateTo(
                    GeoPoint(singleJob.latitude, singleJob.longitude),
                    15.5,
                    500L
                )
            } else if (jobs.isNotEmpty()) {
                val latitudes = jobs.map { it.latitude }
                val longitudes = jobs.map { it.longitude }
                val maxLat = latitudes.maxOrNull() ?: centerHuamanga.latitude
                val minLat = latitudes.minOrNull() ?: centerHuamanga.latitude
                val maxLon = longitudes.maxOrNull() ?: centerHuamanga.longitude
                val minLon = longitudes.minOrNull() ?: centerHuamanga.longitude

                try {
                    val boundingBox = BoundingBox(
                        maxLat + 0.008,
                        maxLon + 0.008,
                        minLat - 0.008,
                        minLon - 0.008
                    )
                    mapView.zoomToBoundingBox(boundingBox, true, 80)
                } catch (e: Exception) {
                    mapView.controller.setCenter(centerHuamanga)
                }
            } else {
                mapView.controller.setCenter(centerHuamanga)
            }
        }
    }

    private fun refreshMarkers() {
        val mapView = _binding?.osmMapView ?: return
        mapView.overlays.clear()

        val jobs = repository.getJobs(district = selectedDistrict)
        val context = context ?: return

        for (job in jobs) {
            val isSelected = job.id == selectedJobId
            val marker = Marker(mapView)
            marker.position = GeoPoint(job.latitude, job.longitude)
            marker.title = job.title
            marker.snippet = "S/ ${job.payment.toInt()} • ${job.district}"
            marker.relatedObject = job

            // Generar icono personalizado con diseño ChambAYA
            marker.icon = createCustomMarkerDrawable(
                context = context,
                price = job.payment,
                isFeatured = job.isFeatured,
                isSelected = isSelected
            )
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            marker.setOnMarkerClickListener { m, _ ->
                val clickedJob = m.relatedObject as? JobOffer
                if (clickedJob != null) {
                    selectJob(clickedJob, fromMapMarker = true)
                }
                true
            }

            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    /**
     * Genera un pin de mapa personalizado estilo Badge/Pill con el precio (S/ ...)
     * y paleta de colores ChambAYA (Índigo para normal, Cian para destacada, Índigo profundo para seleccionada).
     */
    private fun createCustomMarkerDrawable(
        context: Context,
        price: Double,
        isFeatured: Boolean,
        isSelected: Boolean
    ): Drawable {
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

        return BitmapDrawable(context.resources, bitmap)
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    override fun onResume() {
        super.onResume()
        binding.osmMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.osmMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.osmMapView.onDetach()
        _binding = null
    }
}
