package com.dicoding.mystoryapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.mystoryapp.factory.ViewModelFactory
import com.dicoding.mystoryapp.ui.main.MainActivity
import com.dicoding.mystoryapp.util.getImageUri
import com.dicoding.mystoryapp.util.reduceFileImage
import com.dicoding.mystoryapp.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var addStoryViewModel: AddStoryViewModel

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this, REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
            else
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT)

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarLogin.visibility = View.GONE

        addStoryViewModel = obtainViewModel(this)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }

    }

    //Upload Image
    private fun uploadImage() {
        addStoryViewModel.getSession().observe(this@AddStoryActivity) {
            binding.progressBarLogin.visibility = View.VISIBLE
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                val description = binding.textStorydescription.text.toString()

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                if (it.isLoggedIn) {
                    addStoryViewModel.addNewStory(
                        it.token,
                        multipartBody,
                        requestBody
                    )
                    addStoryViewModel.addStoryResponse.observe(this@AddStoryActivity) { response ->
                        if (response.error == false) {
                            Toast.makeText(this, "Successfully add story", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error when add story", Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.progressBarLogin.visibility = View.GONE
                } else {
                    binding.progressBarLogin.visibility = View.GONE
                    Toast.makeText(this, "Error user is not logged in", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                binding.progressBarLogin.visibility = View.GONE
            }
        }


    }


    // Camera
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }


    // Photopicker
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // ViewModel setup
    private fun obtainViewModel(activity: AppCompatActivity): AddStoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(AddStoryViewModel::class.java)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}