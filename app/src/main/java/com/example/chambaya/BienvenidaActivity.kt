package com.example.chambaya

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.databinding.ActividadBienvenidaBinding

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActividadBienvenidaBinding
    private lateinit var indicators: List<View>

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
            indicator.setOnClickListener { showSlide(index) }
        }

        binding.btnAccessAccount.setOnClickListener { openMain() }
        binding.tvSignup.setOnClickListener { openMain() }
        showSlide(0)
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSlide(index: Int) {
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
                width = if (isSelected) dp(34) else dp(8)
                height = dp(8)
            }
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class WelcomeSlide(
        val imageRes: Int,
        val title: String,
        val subtitle: String
    )
}
