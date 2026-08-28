package com.example.chambaya.ui.map

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.chambaya.data.model.JobOffer
import kotlin.math.cos
import kotlin.math.sin

class AyacuchoMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var jobs: List<JobOffer> = emptyList()
    private var selectedJobId: String? = null
    var onJobPinClickListener: ((JobOffer) -> Unit)? = null

    // Drawing paints
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4F6F9")
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E2E8F0")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val districtAreaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EDE9FE")
        style = Paint.Style.FILL
    }

    private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CBD5E1")
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#475569")
        textSize = 32f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E65100")
        style = Paint.Style.FILL
    }

    private val pinFeaturedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF3D00")
        style = Paint.Style.FILL
    }

    private val pinSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A237E")
        style = Paint.Style.FILL
    }

    private val pinTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    private val pulsePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFCC80")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    // Animation radar pulse
    private var pulseRadius = 0f
    private var pulseAlpha = 255
    private var animator: ValueAnimator? = null

    // Stored pin coordinates for touch detection
    private val pinRects = mutableListOf<Triple<RectF, JobOffer, PointF>>()

    init {
        startPulseAnimation()
    }

    private fun startPulseAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2400
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                pulseRadius = progress * 70f
                pulseAlpha = ((1f - progress) * 200).toInt()
                invalidate()
            }
            start()
        }
    }

    fun setJobs(newJobs: List<JobOffer>) {
        this.jobs = newJobs
        invalidate()
    }

    fun setSelectedJob(jobId: String?) {
        this.selectedJobId = jobId
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return

        // 1. Background
        canvas.drawRect(0f, 0f, w, h, backgroundPaint)

        // 2. Draw topographic concentric radar circles centered around Huamanga Plaza
        val centerX = w * 0.5f
        val centerY = h * 0.42f

        for (r in listOf(120f, 240f, 380f, 520f)) {
            canvas.drawCircle(centerX, centerY, r, gridPaint)
        }

        // 3. Draw main roads of Ayacucho (Libertadores, 28 de Julio, Av. Cusco, Vía Evitamiento)
        val pathRoads = Path().apply {
            // Av. Cusco towards San Juan Bautista
            moveTo(centerX, centerY)
            quadTo(centerX - 80f, centerY + 200f, centerX - 140f, h * 0.75f)

            // Av. Libertadores towards Carmen Alto
            moveTo(centerX, centerY)
            quadTo(centerX + 60f, centerY + 180f, centerX + 160f, h * 0.78f)

            // Towards Jesús Nazareno & Andrés Avelino
            moveTo(centerX, centerY)
            quadTo(centerX + 80f, centerY - 150f, centerX + 200f, h * 0.15f)

            // Towards Acuchimay
            moveTo(centerX, centerY)
            quadTo(centerX - 120f, centerY - 120f, centerX - 240f, h * 0.22f)
        }
        canvas.drawPath(pathRoads, roadPaint)

        // 4. District name labels
        textPaint.color = Color.parseColor("#64748B")
        textPaint.textSize = 28f

        // Center / Plaza Mayor
        canvas.drawText("🏛️ Plaza Mayor (Centro)", centerX, centerY - 25f, textPaint)

        // Carmen Alto
        canvas.drawText("Carmen Alto", centerX + 180f, centerY + 240f, textPaint)

        // San Juan Bautista
        canvas.drawText("San Juan Bautista", centerX - 180f, centerY + 250f, textPaint)

        // Jesús Nazareno
        canvas.drawText("Jesús Nazareno", centerX + 160f, centerY - 220f, textPaint)

        // Andrés Avelino Cáceres
        canvas.drawText("A. Avelino Cáceres", centerX + 200f, centerY - 320f, textPaint)

        // 5. Draw Job Pins
        pinRects.clear()

        jobs.forEachIndexed { index, job ->
            val pos = getJobMapCoordinates(job, centerX, centerY, index)
            val isSelected = job.id == selectedJobId

            // Pulse animation for featured or selected jobs
            if (job.isFeatured || isSelected) {
                pulsePaint.alpha = pulseAlpha
                pulsePaint.color = if (isSelected) Color.parseColor("#3F51B5") else Color.parseColor("#FF5722")
                canvas.drawCircle(pos.x, pos.y, 35f + pulseRadius, pulsePaint)
            }

            // Pin background circle
            val paint = when {
                isSelected -> pinSelectedPaint
                job.isFeatured -> pinFeaturedPaint
                else -> pinPaint
            }

            val pinRadius = if (isSelected) 36f else 28f
            canvas.drawCircle(pos.x, pos.y, pinRadius, paint)

            // White border
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }
            canvas.drawCircle(pos.x, pos.y, pinRadius, borderPaint)

            // Price / icon text
            val shortText = "S/${job.payment.toInt()}"
            pinTextPaint.textSize = if (isSelected) 18f else 16f
            canvas.drawText(shortText, pos.x, pos.y + 6f, pinTextPaint)

            // Save click area
            val touchRect = RectF(
                pos.x - 45f,
                pos.y - 45f,
                pos.x + 45f,
                pos.y + 45f
            )
            pinRects.add(Triple(touchRect, job, pos))
        }

        // 6. User current location dot (Plaza Mayor)
        val userDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0288D1")
            style = Paint.Style.FILL
        }
        val userBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawCircle(centerX, centerY, 16f, userDotPaint)
        canvas.drawCircle(centerX, centerY, 16f, userBorderPaint)
    }

    private fun getJobMapCoordinates(job: JobOffer, centerX: Float, centerY: Float, index: Int): PointF {
        return when (job.district) {
            "Carmen Alto" -> PointF(centerX + 140f + (index % 2) * 40f, centerY + 180f + (index * 25f))
            "San Juan Bautista" -> PointF(centerX - 160f - (index % 2) * 35f, centerY + 170f + (index * 20f))
            "Jesús Nazareno" -> PointF(centerX + 130f + (index * 20f), centerY - 180f - (index * 20f))
            "Andrés Avelino Cáceres" -> PointF(centerX + 180f, centerY - 280f + (index * 15f))
            else -> { // Ayacucho Centro
                val angle = (index * 1.3).toFloat()
                PointF(centerX + cos(angle) * 85f, centerY + sin(angle) * 85f)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val touchX = event.x
            val touchY = event.y

            for (triple in pinRects) {
                if (triple.first.contains(touchX, touchY)) {
                    selectedJobId = triple.second.id
                    invalidate()
                    onJobPinClickListener?.invoke(triple.second)
                    return true
                }
            }
        }
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
