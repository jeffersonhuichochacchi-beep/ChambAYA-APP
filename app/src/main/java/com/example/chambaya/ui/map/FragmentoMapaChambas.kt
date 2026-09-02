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
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import androidx.transition.TransitionManager
import androidx.transition.Slide
import android.view.Gravity
import java.io.File

class FragmentoMapaChambas : Fragment() {

    private var _binding: FragmentoMapaChambasBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ChambayaRepository
    private lateinit var carouselAdapter: AdaptadorTrabajo
    private var selectedDistrict: String = "Todos"
    private var selectedJobId: String? = null
    private var isProgrammaticPan: Boolean = false

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

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                if (!isProgrammaticPan) hideCarousel()
                return false
            }
            override fun onZoom(event: ZoomEvent): Boolean {
                if (!isProgrammaticPan) hideCarousel()
                return false
            }
        })

        mapView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                isProgrammaticPan = false
            }
            false
        }

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
            },
            onShareClick = { job ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "¡Oye, mira esta chamba en ChambAYA! Buscan: ${job.title} en ${job.district}, pagando S/ ${job.payment}. ¡Postula ya!")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Compartir chamba"))
            }
        )
        binding.rvMapJobsCarousel.adapter = carouselAdapter
    }

    private fun selectJob(job: JobOffer, fromMapMarker: Boolean) {
        setCarouselVisible(true)
        selectedJobId = job.id

        // Mostrar un solo dato, no hacerlo deslizable con todos
        carouselAdapter.updateData(listOf(job))

        // Mover mapa hacia la ubicación de la oferta
        isProgrammaticPan = true
        val targetPoint = GeoPoint(job.latitude, job.longitude)
        binding.osmMapView.controller.animateTo(targetPoint, 16.0, 500L)
        binding.osmMapView.postDelayed({ isProgrammaticPan = false }, 600L)

        refreshMarkers()
    }

    private fun setupListeners() {
        binding.mapChipGroupDistricts.setOnCheckedStateChangeListener { _, checkedIds ->
            hideCarousel()
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
        binding.tvActivePinCount.text = "${jobs.size} ofertas"

        if (jobs.none { it.id == selectedJobId }) {
            hideCarousel()
        }

        refreshMarkers()

        // Ajuste seguro de cámara
        binding.osmMapView.post {
            if (_binding == null) return@post
            val mapView = binding.osmMapView
            if (mapView.width <= 0 || mapView.height <= 0) return@post

            if (jobs.size == 1) {
                val singleJob = jobs.first()
                isProgrammaticPan = true
                mapView.controller.animateTo(
                    GeoPoint(singleJob.latitude, singleJob.longitude),
                    15.5,
                    500L
                )
                mapView.postDelayed({ isProgrammaticPan = false }, 600L)
            } else if (jobs.isNotEmpty()) {
                val latitudes = jobs.map { it.latitude }
                val longitudes = jobs.map { it.longitude }
                val maxLat = latitudes.maxOrNull() ?: centerHuamanga.latitude
                val minLat = latitudes.minOrNull() ?: centerHuamanga.latitude
                val maxLon = longitudes.maxOrNull() ?: centerHuamanga.longitude
                val minLon = longitudes.minOrNull() ?: centerHuamanga.longitude

                try {
                    isProgrammaticPan = true
                    val boundingBox = BoundingBox(
                        maxLat + 0.008,
                        maxLon + 0.008,
                        minLat - 0.008,
                        minLon - 0.008
                    )
                    mapView.zoomToBoundingBox(boundingBox, true, 80)
                    mapView.postDelayed({ isProgrammaticPan = false }, 1000L)
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
        val iconDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_location)?.mutate()
        
        val bgColor = when {
            isSelected -> Color.parseColor("#B71C1C") // Rojo oscuro intenso (seleccionado)
            else -> Color.parseColor("#E53935")       // Rojo estándar clásico (normal/destacado)
        }
        iconDrawable?.setTint(bgColor)
        
        val size = dpToPx(context, if (isSelected) 46f else 36f).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        iconDrawable?.setBounds(0, 0, size, size)
        iconDrawable?.draw(canvas)

        return BitmapDrawable(context.resources, bitmap)
    }

    private fun dpToPx(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun setCarouselVisible(visible: Boolean) {
        val container = _binding?.bottomCarouselContainer ?: return
        if ((container.visibility == View.VISIBLE) == visible) return

        val transition = Slide(Gravity.BOTTOM).apply {
            duration = 180
            addTarget(container)
        }
        TransitionManager.beginDelayedTransition(_binding?.root as ViewGroup, transition)
        container.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun hideCarousel() {
        val container = _binding?.bottomCarouselContainer ?: return
        if (container.visibility == View.VISIBLE) {
            setCarouselVisible(false)
            selectedJobId = null
            refreshMarkers()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            hideCarousel()
        }
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
