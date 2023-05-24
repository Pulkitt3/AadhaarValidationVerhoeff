package com.example.xbiztask2.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.xbiztask2.databinding.FragmentDashboardBinding
import me.echodev.resizer.Resizer
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private val PERMISSION_REQUEST_CODE = 1
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val REQUEST_IMAGE_SELECTION = 2
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String

    private var singleImage = false
    private var capturedFirstImage = false
    private var getFirstImage = false


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.cvCamera.setOnClickListener { showImageSelectionDialog(true) }
        binding.cvGallery.setOnClickListener { showImageSelectionDialog(false) }
        checkPermissions()
        return root
    }

    lateinit var builder: AlertDialog.Builder
    lateinit var alert: AlertDialog
    private fun showImageSelectionDialog(captureSelection: Boolean) {
        val options = arrayOf("Capture 1 Image", "Capture 2 Images")
        builder = AlertDialog.Builder(requireContext())
        alert = builder.create()
        builder.setTitle("Select Number of Images")
            .setItems(options) { _, which ->
                if (!captureSelection) {
                    when (which) {
                        0 -> selectImageFromGallery("single")
                        1 -> selectImageFromGallery("double")
                    }
                } else {
                    // Capture the first image
                    when (which) {
                        0 -> captureImage("single")
                        1 -> captureImage("double")
                    }
                }
            }
            .setCancelable(true)
        builder.show()
    }


    private fun captureImage(item: String) {
        singleImage = item != "single"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Handle error while creating the file
            null
        }
        photoFile?.let {
            val photoURI: Uri =
                FileProvider.getUriForFile(requireContext(), "com.example.xbiztask2.provider", it)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun selectImageFromGallery(item: String) {
        singleImage = item != "single"
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_SELECTION)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (!singleImage) {
                var camURI = Uri.parse(currentPhotoPath)
                if (camURI != null) {
                    Log.e("TAG", "onActivityResult: $camURI")
                    val imageFile = File(currentPhotoPath)
                    if (imageFile.exists()) {
                        val imageSizeInBytes = imageFile.length()
                        val imageSizeInKB = imageSizeInBytes / 1024
                        if (imageSizeInKB > 500) {
                            val fileName = "XBIZ" + System.currentTimeMillis()
                            val resizedImage = Resizer(requireContext())
                                .setTargetLength(2000)
                                .setQuality(80)
                                .setOutputFormat("JPEG")
                                .setOutputFilename(fileName)
                                .setSourceImage(imageFile)
                                .resizedFile
                            val imageSizeInBytes = resizedImage.length()
                            val imageSizeInKB = imageSizeInBytes / 1024
                            Log.e("TAG", "onActivityResult: 1 " + imageSizeInKB)
                            try {
                                val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                binding.capturedImageLL1.visibility = View.VISIBLE
                                binding.capturedImageLL2.visibility = View.GONE
                                binding.capturedImage1.setImageBitmap(bitmap)
                                binding.capturedImageTV1.setText(resizedImage.toString())
                                val fileName = "XBIZ" + System.currentTimeMillis() + ".jpg"
                                binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 2 $e")
                            }
                        } else {
                            try {
                                val imageSizeInBytess = imageFile.length()
                                val imageSizeInKBs = imageSizeInBytess / 1024
                                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                                binding.capturedImageLL1.visibility = View.VISIBLE
                                binding.capturedImageLL2.visibility = View.GONE
                                binding.capturedImage1.setImageBitmap(bitmap)
                                binding.capturedImageTV1.setText(imageFile.toString())
                                binding.capturedImageTV11.setText(imageSizeInKBs.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 3 $e")
                            }
                        }

                    } else {
                        Log.e("TAG", "onActivityResult: 1" + "sdsd")
                        // File does not exist, handle the error or log a message
                    }
                }
            } else {
                var camURI = Uri.parse(currentPhotoPath)
                if (camURI != null) {
                    Log.e("TAG", "onActivityResult: " + camURI)
                    val imageFile = File(currentPhotoPath)
                    if (imageFile.exists()) {
                        val imageSizeInBytes = imageFile.length()
                        val imageSizeInKB = imageSizeInBytes / 1024
                        if (imageSizeInKB > 500) {
                            if (capturedFirstImage) {
                                val fileName = "XBIZ" + System.currentTimeMillis()
                                val resizedImage = Resizer(requireContext())
                                    .setTargetLength(2000)
                                    .setQuality(80)
                                    .setOutputFormat("JPEG")
                                    .setOutputFilename(fileName)
                                    .setSourceImage(imageFile)
                                    .resizedFile
                                val imageSizeInBytes = resizedImage.length()
                                val imageSizeInKB = imageSizeInBytes / 1024
                                Log.e("TAG", "onActivityResult: 1 " + imageSizeInKB)
                                try {
                                    val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                    binding.capturedImageLL2.visibility = View.VISIBLE
                                    binding.capturedImage2.setImageBitmap(bitmap)
                                    binding.capturedImageTV2.setText(resizedImage.toString())
                                    binding.capturedImageTV22.setText(imageSizeInKB.toString() + " KB")
                                } catch (e: java.lang.Exception) {
                                    Log.e("TAG", "onActivityResult: 2 " + e)
                                }
                            } else {
                                capturedFirstImage = true
                                captureImage("double")
                                val fileName = "XBIZ" + System.currentTimeMillis()
                                val resizedImage = Resizer(requireContext())
                                    .setTargetLength(2000)
                                    .setQuality(80)
                                    .setOutputFormat("JPEG")
                                    .setOutputFilename(fileName)
                                    .setSourceImage(imageFile)
                                    .resizedFile
                                val imageSizeInBytes = resizedImage.length()
                                val imageSizeInKB = imageSizeInBytes / 1024
                                Log.e("TAG", "onActivityResult: 1 " + imageSizeInKB)
                                try {
                                    val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                    binding.capturedImageLL1.visibility = View.VISIBLE
                                    binding.capturedImage1.setImageBitmap(bitmap)
                                    binding.capturedImageTV1.setText(resizedImage.toString())
                                    binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                                } catch (e: java.lang.Exception) {
                                    Log.e("TAG", "onActivityResult: 2 " + e)
                                }
                            }
                        } else {
                            if (capturedFirstImage) {
                                val fileName = "XBIZ" + System.currentTimeMillis()
                                val resizedImage = Resizer(requireContext())
                                    .setTargetLength(2000)
                                    .setQuality(80)
                                    .setOutputFormat("JPEG")
                                    .setOutputFilename(fileName)
                                    .setSourceImage(imageFile)
                                    .resizedFile
                                val imageSizeInBytes = resizedImage.length()
                                val imageSizeInKB = imageSizeInBytes / 1024
                                Log.e("TAG", "onActivityResult: 1 " + imageSizeInKB)
                                try {
                                    val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                    binding.capturedImageLL2.visibility = View.VISIBLE
                                    binding.capturedImage2.setImageBitmap(bitmap)
                                    binding.capturedImageTV2.setText(resizedImage.toString())
                                    binding.capturedImageTV22.setText(imageSizeInKB.toString() + " KB")
                                } catch (e: java.lang.Exception) {
                                    Log.e("TAG", "onActivityResult: 2 " + e)
                                }
                            } else {
                                capturedFirstImage = true
                                captureImage("double")
                                val fileName = "XBIZ" + System.currentTimeMillis()
                                val resizedImage = Resizer(requireContext())
                                    .setTargetLength(2000)
                                    .setQuality(80)
                                    .setOutputFormat("JPEG")
                                    .setOutputFilename(fileName)
                                    .setSourceImage(imageFile)
                                    .resizedFile
                                val imageSizeInBytes = resizedImage.length()
                                val imageSizeInKB = imageSizeInBytes / 1024
                                Log.e("TAG", "onActivityResult: 1 " + imageSizeInKB)
                                try {
                                    val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                    binding.capturedImageLL1.visibility = View.VISIBLE
                                    binding.capturedImage1.setImageBitmap(bitmap)
                                    binding.capturedImageTV1.setText(resizedImage.toString())
                                    binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                                } catch (e: java.lang.Exception) {
                                    Log.e("TAG", "onActivityResult: 2 " + e)
                                }
                            }
                        }

                    } else {
                        Log.e("TAG", "onActivityResult: 1" + "sdsd")
                        // File does not exist, handle the error or log a message
                    }
                }
            }

        } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            if (!singleImage) {
                val file = data?.data?.let { getFilePathFromUri(requireContext(), it) }
                if (file != null) {
                    val imageFile = File(file)
                    Log.e("TAG", "onActivityResult: $imageFile")
                    val imageSizeInBytes = imageFile.length()
                    val imageSizeInKB = imageSizeInBytes / 1024
                    if (imageSizeInKB > 500) {
                        val fileName = "XBIZ" + System.currentTimeMillis()
                        val resizedImage = Resizer(requireContext())
                            .setTargetLength(2000)
                            .setQuality(80)
                            .setOutputFormat("JPEG")
                            .setOutputFilename(fileName)
                            .setSourceImage(imageFile)
                            .resizedFile
                        val imageSizeInBytes = resizedImage.length()
                        val imageSizeInKB = imageSizeInBytes / 1024
                        Log.e("TAG", "onActivityResult: 1 $imageSizeInKB")
                        try {
                            val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                            binding.capturedImageLL1.visibility = View.VISIBLE
                            binding.capturedImageLL2.visibility = View.GONE
                            binding.capturedImage1.setImageBitmap(bitmap)
                            binding.capturedImageTV1.setText(resizedImage.toString())
                            Log.e("TAG", "onActivityResult: 12 $bitmap")
                            binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                        } catch (e: java.lang.Exception) {
                            Log.e("TAG", "onActivityResult: 2 $e")
                        }
                    } else {
                        try {
                            val imageSizeInBytess = imageFile.length()
                            val imageSizeInKBs = imageSizeInBytess / 1024
                            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                            binding.capturedImageLL1.visibility = View.VISIBLE
                            binding.capturedImageLL2.visibility = View.GONE
                            binding.capturedImage1.setImageBitmap(bitmap)
                            binding.capturedImageTV1.setText(imageFile.toString())
                            Log.e("TAG", "onActivityResult: 11 $bitmap")
                            binding.capturedImageTV11.setText(imageSizeInKBs.toString() + " KB")
                        } catch (e: java.lang.Exception) {
                            Log.e("TAG", "onActivityResult: 3 $e")
                        }
                    }
                }
            } else {
                val file = data?.data?.let { getFilePathFromUri(requireContext(), it) }
                val imageFile = File(file)
                if (imageFile != null) {
                    Log.e("TAG", "onActivityResult: $file")
                    val imageSizeInBytes = imageFile.length()
                    val imageSizeInKB = imageSizeInBytes / 1024
                    if (imageSizeInKB > 500) {
                        if (getFirstImage) {
                            val fileName = "XBIZ" + System.currentTimeMillis()
                            val resizedImage = Resizer(requireContext())
                                .setTargetLength(2000)
                                .setQuality(80)
                                .setOutputFormat("JPEG")
                                .setOutputFilename(fileName)
                                .setSourceImage(imageFile)
                                .resizedFile
                            val imageSizeInBytes = resizedImage.length()
                            val imageSizeInKB = imageSizeInBytes / 1024
                            Log.e("TAG", "onActivityResult: 1 $imageSizeInKB")
                            try {
                                val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                binding.capturedImageLL2.visibility = View.VISIBLE
                                binding.capturedImage2.setImageBitmap(bitmap)
                                binding.capturedImageTV2.setText(resizedImage.toString())
                                binding.capturedImageTV22.setText(imageSizeInKB.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 2 $e")
                            }
                        } else {
                            getFirstImage = true
                            selectImageFromGallery("double")
                            val fileName = "XBIZ" + System.currentTimeMillis()
                            val resizedImage = Resizer(requireContext())
                                .setTargetLength(2000)
                                .setQuality(80)
                                .setOutputFormat("JPEG")
                                .setOutputFilename(fileName)
                                .setSourceImage(imageFile)
                                .resizedFile
                            val imageSizeInBytes = resizedImage.length()
                            val imageSizeInKB = imageSizeInBytes / 1024
                            Log.e("TAG", "onActivityResult: 1 $imageSizeInKB")
                            try {
                                val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                binding.capturedImageLL1.visibility = View.VISIBLE
                                binding.capturedImage1.setImageBitmap(bitmap)
                                binding.capturedImageTV1.setText(resizedImage.toString())
                                binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 2 $e")
                            }
                        }
                    } else {
                        if (getFirstImage) {
                            val fileName = "XBIZ" + System.currentTimeMillis()
                            val resizedImage = Resizer(requireContext())
                                .setTargetLength(2000)
                                .setQuality(80)
                                .setOutputFormat("JPEG")
                                .setOutputFilename(fileName)
                                .setSourceImage(imageFile)
                                .resizedFile
                            val imageSizeInBytes = resizedImage.length()
                            val imageSizeInKB = imageSizeInBytes / 1024
                            Log.e("TAG", "onActivityResult: 1 $imageSizeInKB")
                            try {
                                val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                binding.capturedImageLL2.visibility = View.VISIBLE
                                binding.capturedImage2.setImageBitmap(bitmap)
                                binding.capturedImageTV2.setText(resizedImage.toString())
                                binding.capturedImageTV22.setText(imageSizeInKB.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 2 $e")
                            }
                        } else {
                            getFirstImage = true
                            selectImageFromGallery("double")
                            val fileName = "XBIZ" + System.currentTimeMillis()
                            val resizedImage = Resizer(requireContext())
                                .setTargetLength(2000)
                                .setQuality(80)
                                .setOutputFormat("JPEG")
                                .setOutputFilename(fileName)
                                .setSourceImage(imageFile)
                                .resizedFile
                            val imageSizeInBytes = resizedImage.length()
                            val imageSizeInKB = imageSizeInBytes / 1024
                            Log.e("TAG", "onActivityResult: 1 $imageSizeInKB")
                            try {
                                val bitmap = BitmapFactory.decodeFile(resizedImage.absolutePath)
                                binding.capturedImageLL1.visibility = View.VISIBLE
                                binding.capturedImage1.setImageBitmap(bitmap)
                                binding.capturedImageTV1.setText(resizedImage.toString())
                                binding.capturedImageTV11.setText(imageSizeInKB.toString() + " KB")
                            } catch (e: java.lang.Exception) {
                                Log.e("TAG", "onActivityResult: 2 $e")
                            }
                        }
                    }
                }
            }
        }

    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf("_data")
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow("_data")
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            return filePath
        }
        return uri.path
    }

    fun calculateSampleSize(options: BitmapFactory.Options, maxSizeInBytes: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var sampleSize = 1
        var totalPixels = width * height

        while (totalPixels / (sampleSize * sampleSize) > maxSizeInBytes) {
            sampleSize *= 2
        }

        return sampleSize
    }

    private fun checkPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(requireContext(),
                it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(),
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE)
        } else {
            // Permissions already granted, proceed with your logic
            // ...
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val grantedPermissions = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (grantedPermissions) {
                // All permissions granted, proceed with your logic
                // ...
            } else {
                // Permissions not granted, handle accordingly
                // ...
            }
        }
    }
}