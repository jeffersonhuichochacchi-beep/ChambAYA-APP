package com.example.chambaya

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.databinding.ActividadBienvenidaBinding

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActividadBienvenidaBinding
    private lateinit var indicators: List<View>
    private var currentSlideIndex = 0
    private var indicatorAnimator: ValueAnimator? = null

    private val slides = listOf(
        WelcomeSlide(
            R.drawable.welcome_illustration,
            "Welcome to ChambAYA!",
            "The ultimate app for making your daily work as convenient\nand smooth as possible"
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_jobs,
            "Find local jobs fast",
            "Discover trusted chambas near you and apply\nwith just a few taps"
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_map,
            "Work around your city",
            "Explore opportunities on the map and choose\nthe best route for your day"
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_chat,
            "Chat and get hired",
            "Talk directly with employers, confirm details\nand start with confidence"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        indicators = listOf(
            binding.indicatorOne,
            binding.indicatorTwo,
            binding.indicatorThree,
            binding.indicatorFour
        )

        indicators.forEachIndexed { index, indicator ->
            indicator.setOnClickListener { showSlide(index, restartProgress = true) }
        }

        binding.btnAccessAccount.setOnClickListener { openMain() }
        binding.tvSignup.setOnClickListener { openMain() }
        showSlide(0, restartProgress = true)
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            startIndicatorProgress()
        }
    }

    override fun onPause() {
        indicatorAnimator?.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        indicatorAnimator?.cancel()
        indicatorAnimator = null
        super.onDestroy()
    }

    private fun openMain() {
        indicatorAnimator?.cancel()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSlide(index: Int, restartProgress: Boolean) {
        currentSlideIndex = index
        val slide = slides[index]
        binding.imgWelcomeIllustration.setImageResource(slide.imageRes)
        binding.tvWelcomeTitle.text = slide.title
        binding.tvWelcomeSubtitle.text = slide.subtitle

        indicators.forEachIndexed { indicatorIndex, indicator ->
            val isSelected = indicatorIndex == index
            indicator.setBackgroundResource(
                if (isSelected) R.drawable.bg_indicator_active else R.drawable.bg_indicator_inactive
            )
            indicator.layoutParams = indicator.layoutParams.apply {
                width = dp(8)
                height = dp(8)
            }
        }

        if (restartProgress) {
            startIndicatorProgress()
        }
    }

    private fun startIndicatorProgress() {
        indicatorAnimator?.cancel()
        val activeIndicator = indicators[currentSlideIndex]
        val collapsedWidth = dp(8)
        val expandedWidth = dp(34)

        activeIndicator.layoutParams = activeIndicator.layoutParams.apply {
            width = collapsedWidth
            height = dp(8)
        }
        activeIndicator.requestLayout()

        indicatorAnimator = ValueAnimator.ofInt(collapsedWidth, expandedWidth).apply {
            duration = SLIDE_DURATION_MS
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                activeIndicator.layoutParams = activeIndicator.layoutParams.apply {
                    width = animator.animatedValue as Int
                }
                activeIndicator.requestLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                private var wasCancelled = false

                override fun onAnimationCancel(animation: Animator) {
                    wasCancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!wasCancelled) {
                    val nextIndex = (currentSlideIndex + 1) % slides.size
                    showSlide(nextIndex, restartProgress = true)
                }
                }
            })
            start()
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class WelcomeSlide(
        val imageRes: Int,
        val title: String,
        val subtitle: String
    )

    private companion object {
        const val SLIDE_DURATION_MS = 3500L
    }
}
