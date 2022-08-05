package com.example.activityresultapi

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import coil.load
import com.example.activityresultapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //во фрагменте все точно также
    private val requestPermissionLauncher =
    //контракт - задает, что мы делаем, когда запрашиваем результат, он создает интент на получение результата и
        //рассказывает как распарсить результат
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Log.d(MainActivity::class.java.simpleName, "Permission granted: $it")
        }

    private val editMessageLauncher = registerForActivityResult(EditTextActivity.Contract()) {
        it?.message?.let { message ->
            if (message.isNotEmpty()) {
                binding.tvText.text = message
            }
        }
    }

    private val imagePickerLauncher = ImagePicker(activityResultRegistry, this) { imageUri ->
        if (imageUri != null) {
            binding.ivImage.load(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bEditMessage.setOnClickListener {
            editMessage()
        }
        binding.bRequestPermission.setOnClickListener {
            requestPermission()
        }
        binding.bPickImage.setOnClickListener {
            pickImage()
        }
    }

    private fun requestPermission() {
        //обязательно передаем какое-то значение (зависит от типа контракта)
        requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
    }

    private fun editMessage() {
        editMessageLauncher.launch(binding.tvText.text.toString())
    }

    private fun pickImage() {
        imagePickerLauncher.pickImage()
    }
}

//вынесение логики в отдельный класс
//activityResultRegistry управляет получением всех результатов, запрашивает их и тд
class ImagePicker(
    private val activityResultRegistry: ActivityResultRegistry,
    private val lifecycleOwner: LifecycleOwner,
    private val callback: (imageUri: Uri?) -> Unit
) {

    //есть перегрузка и без lifecycleOwner
    private val getContentLauncher: ActivityResultLauncher<String> =
        activityResultRegistry.register(
            IMAGE_PICKER_REGISTER_KEY,
            lifecycleOwner,
            ActivityResultContracts.GetContent(),
        ) { imageUri ->
            callback(imageUri)
        }

    fun pickImage() {
        getContentLauncher.launch("image/*")
    }

    companion object {
        private const val IMAGE_PICKER_REGISTER_KEY = "image_picker_key"
    }
}