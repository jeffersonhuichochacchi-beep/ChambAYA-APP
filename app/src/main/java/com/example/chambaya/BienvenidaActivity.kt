package com.example.chambaya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chambaya.databinding.ActividadBienvenidaBinding

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActividadBienvenidaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAccessAccount.setOnClickListener { openMain() }
        binding.tvSignup.setOnClickListener { openMain() }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
