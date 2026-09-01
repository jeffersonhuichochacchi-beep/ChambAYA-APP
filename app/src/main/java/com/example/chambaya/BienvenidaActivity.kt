package com.example.chambaya

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.databinding.ActividadBienvenidaBinding

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActividadBienvenidaBinding
    private lateinit var indicators: List<View>
    private var currentSlideIndex = 0
    private var indicatorAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        IdiomaManager.applySavedLanguage(this)
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
        binding.tvSignup.setOnClickListener { openRegister() }
        binding.btnLanguage.setOnClickListener { changeWelcomeLanguage() }
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
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_GUEST_MODE, true)
        }
        startActivity(intent)
        finish()
    }

    private fun openLogin() {
        indicatorAnimator?.cancel()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun openRegister() {
        indicatorAnimator?.cancel()
        startActivity(Intent(this, RegistroActivity::class.java))
    }

    private fun showSlide(index: Int, restartProgress: Boolean) {
        currentSlideIndex = index
        val slide = getSlides()[index]
        binding.imgWelcomeIllustration.setImageResource(slide.imageRes)
        updateCurrentSlideText()

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
                        val nextIndex = (currentSlideIndex + 1) % getSlides().size
                        showSlide(nextIndex, restartProgress = true)
                    }
                }
            })
            start()
        }
    }

    private fun changeWelcomeLanguage() {
        IdiomaManager.toggleSavedLanguage(this)
        updateLocalizedTexts()
    }

    private fun updateLocalizedTexts() {
        binding.btnLanguage.contentDescription = localizedString(R.string.action_change_language)
        binding.btnAccessAccount.text = localizedString(R.string.welcome_get_started)
        binding.tvNoAccount.text = localizedString(R.string.auth_no_account)
        binding.tvSignup.text = localizedString(R.string.auth_signup)
        updateCurrentSlideText()
    }

    private fun updateCurrentSlideText() {
        val slide = getSlides()[currentSlideIndex]
        binding.tvWelcomeTitle.text = localizedString(slide.titleRes)
        binding.tvWelcomeSubtitle.text = localizedString(slide.subtitleRes)
    }

    private fun localizedString(@StringRes resId: Int): String {
        return IdiomaManager.createLocalizedContext(this).getString(resId)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class WelcomeSlide(
        val imageRes: Int,
        val titleRes: Int,
        val subtitleRes: Int
    )

    private fun getSlides() = listOf(
        WelcomeSlide(
            R.drawable.welcome_illustration,
            R.string.welcome_slide_1_title,
            R.string.welcome_slide_1_subtitle
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_jobs,
            R.string.welcome_slide_2_title,
            R.string.welcome_slide_2_subtitle
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_map,
            R.string.welcome_slide_3_title,
            R.string.welcome_slide_3_subtitle
        ),
        WelcomeSlide(
            R.drawable.welcome_illustration_chat,
            R.string.welcome_slide_4_title,
            R.string.welcome_slide_4_subtitle
        )
    )

    private companion object {
        const val SLIDE_DURATION_MS = 3500L
    }
}
