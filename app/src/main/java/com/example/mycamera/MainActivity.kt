package com.example.mycamera

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mycamera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // アプリ内で決めたコード
    // どのようなインテントを呼び出したのかを判別
    val REQUEST_PREVIEW = 1

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

    private fun preview() {
        // カメラを起動し撮影データ取得するインテントはMediaStore.ACTION_IMAGE_CAPTUREを使う
        // スコープ関数also（forEachに似てるかも）
          // 対象オブジェクトに対して処理を行ったり、参照が必要な時に使う
          // 対象オブジェクトはitで参照でき、戻り値は対象オブジェクト
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            // アクティビティが確実にインテントを受け取るためにresolveActivityを使う
            // インテントを処理するアプリがあるかチェックする
            // packageManagerはインストールされてるアプリの情報を保持してるクラス
            intent.resolveActivity(packageManager)?.also {
                // アクティビティ起動後、そのアクティビティから値を受け取る場合startActivityForResultを使う（現在非推奨）
                startActivityForResult(intent, REQUEST_PREVIEW)
            }
        }
    }

//    private fun newPreview() {
//
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
//            intent.resolveActivity(packageManager)?.also {
//                val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
//                    if (result?.resultCode == Activity.RESULT_OK) {
//                        val intent: Intent? = result.data
//                        val imageBitmap = intent?.extras?.get("data") as Bitmap
//                        binding.imageView.setImageBitmap(imageBitmap)
//                    }
//                }
//                resultLauncher.launch(intent)
//            }
//        }
//    }
    private fun takePicture() { }

    // アクティビティが閉じられると起動する
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // requestCodeには呼び出した時のコードが入ってくる
        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            // "data"というキーで画像が渡される
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
        }
    }
}