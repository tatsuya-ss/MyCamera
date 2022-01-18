package com.example.mycamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mycamera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupRadioGroup()
        setupButton()
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId) {
                R.id.preview -> binding.cameraButton.text = binding.preview.text
                R.id.takePicture -> binding.cameraButton.text = binding.takePicture.text
            }
        }
    }

    private fun setupButton() {
        binding.cameraButton.setOnClickListener {
            when(binding.radioGroup.checkedRadioButtonId) {
                R.id.preview -> preview()
                R.id.takePicture -> takePicture()
            }
        }
    }

    private fun preview() { }
    private fun takePicture() { }
}