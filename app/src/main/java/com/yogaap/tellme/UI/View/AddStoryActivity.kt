package com.yogaap.tellme.UI.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yogaap.tellme.Data.di.ViewModelFactory
import com.yogaap.tellme.Data.di.createCustomTempFile
import com.yogaap.tellme.Data.di.reduceFileImage
import com.yogaap.tellme.Data.di.rotateBitmap
import com.yogaap.tellme.Data.di.uriToFile
import com.yogaap.tellme.R
import com.yogaap.tellme.databinding.ActivityAddStoryBinding
import com.yogaap.tellme.UI.viewModel.AddStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null
    private val addStoryViewModel: AddStoryViewModel by viewModels { factory }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupViewModel()
        setupPermission()
        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            btnTakePicture.setOnClickListener { takePicture() }
            btnOpenFile.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadStory() }
        }
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    private fun setupView() {
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            btnTakePicture.setOnClickListener { takePicture() }
            btnOpenFile.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadStory() }
        }

        binding.checkBox.setOnClickListener{
            if(binding.checkBox.isChecked) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            uploadStory()
                        }
                    }
            } else {
                uploadStory()
            }
        }

        supportActionBar?.apply {
            title = getString(R.string.title_add_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                true
            )
            binding.ivAddStory.setImageBitmap(result)
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.yogaap.tellme",
                it
            )
            currentPhotoPath = it.absolutePath

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile
            binding.ivAddStory.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        showLoading()
        addStoryViewModel.getSession().observe(this@AddStoryActivity) {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                // Check if location permission is granted
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Request location once
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                Log.d("Check Location", "uploadStory: ${location.latitude}, ${location.longitude}")
                                uploadResponse(
                                    it.token,
                                    imageMultipart,
                                    binding.edtDescStory.text.toString().toRequestBody("text/plain".toMediaType()),
                                    location.latitude,
                                    location.longitude
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            // Handle location request failure
                            e.printStackTrace()
                            uploadResponse(
                                it.token,
                                imageMultipart,
                                binding.edtDescStory.text.toString().toRequestBody("text/plain".toMediaType())
                            )
                        }
                } else {
                    // Location permission not granted, upload without location
                    uploadResponse(
                        it.token,
                        imageMultipart,
                        binding.edtDescStory.text.toString().toRequestBody("text/plain".toMediaType())
                    )
                }
            } else {
                Toast.makeText(
                    this@AddStoryActivity,
                    getString(R.string.input_image),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadResponse(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double? = 0.0,
        lon: Double? = 0.0
    ) {
        addStoryViewModel.uploadStory(token, file, description, lat, lon)
        addStoryViewModel.uploadResponse.observe(this@AddStoryActivity) {
            if (!it.error) {
                moveActivity()
            }
        }
        showToast()
    }

    private fun showLoading() {
        addStoryViewModel.isLoading.observe(this@AddStoryActivity) {
            binding.pbAdd.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast() {
        addStoryViewModel.toastText.observe(this@AddStoryActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this@AddStoryActivity, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun moveActivity() {
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}