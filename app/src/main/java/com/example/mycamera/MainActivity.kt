package com.example.mycamera

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mycamera.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    // アプリ内で決めたコード
    // どのようなインテントを呼び出したのかを判別
    val REQUEST_PREVIEW = 1
    val REQUEST_PICTURE = 2
    val REQUEST_EXTERNAL_STORAGE = 3

    lateinit var currentPhotoUri: Uri

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupRadioGroup()
        setupButton()
        checkStoragePermission()
    }

    // アクティビティが閉じられると起動する
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // requestCodeには呼び出した時のコードが入ってくる
        if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            // "data"というキーで画像が渡される
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
        } else if (requestCode == REQUEST_PICTURE) {
            when(resultCode) {
                RESULT_OK -> {
                    Intent(Intent.ACTION_SEND).also { share ->
                        share.type = "image/*"
                        share.putExtra(Intent.EXTRA_STREAM, currentPhotoUri)
                        // createChooser(share, "Share to")はアプリの選択画面の新たなインテント
                        startActivity(Intent.createChooser(share, "Share to"))
                    }
                }
                else -> { contentResolver.delete(currentPhotoUri, null, null) }
            }
        }
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

    private fun newPreview() {

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
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                val time: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val values = ContentValues().apply {
                    // MediaStore.Images.Mediaは画像格納の外部ストレージ
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${time}_.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                val collection = MediaStore.Images.Media.getContentUri("external")
                val photoUri = contentResolver.insert(collection, values)
                photoUri?.let {
                    currentPhotoUri = it
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_PICTURE)
            }
        }
    }

    private fun checkStoragePermission() {
        // 権限確認ウィンドウ表示
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            storagePermission()
        }
    }

    private fun storagePermission() {
        var permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                binding.cameraButton.isEnabled = grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

}