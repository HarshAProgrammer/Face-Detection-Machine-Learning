package com.example.machinelearning

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.EasyPermissions.hasPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        const val PERMISSION_CAMERA_REQUEST_CODE = 1
    }
    lateinit var details:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button = findViewById<Button>(R.id.btnCamera)
        details = findViewById<TextView>(R.id.textDescription)


        button.setOnClickListener {
            requestPermissions()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 0)

            } else {
                Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
            }


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            if (bitmap != null) {
                detectFace(bitmap)

            }
        }
    }

    private fun detectFace(bitmap: Bitmap) {

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = " "
                var i = 1
                for (face in faces) {
                    resultText = "No of Faces : $i " +
                            "\nSmile Strength : ${face.smilingProbability?.times(100)}% " +
                            "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}% " +
                            "\nRight Eye Open : ${face.rightEyeOpenProbability?.times(100)}% "

                    i++
                }
                if (faces == null) {
                    details.text = "No face Detected"

                } else {
                    details.text = resultText

                }
            }.addOnFailureListener { e ->
                details.text = "Something Went Wrong"
            }


    }

    private fun hasPermissions() =
        EasyPermissions.hasPermissions(
            this,
            CAMERA
        )

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.request_msg),
            PERMISSION_CAMERA_REQUEST_CODE,
            CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }


}
