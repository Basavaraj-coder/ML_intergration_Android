package com.example.myml_app_integration

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myml_app_integration.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class MainActivity : AppCompatActivity() {

    lateinit var bind : ActivityMainBinding
    lateinit var i: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.OpenCamerabtn.setOnClickListener {
            //camera opening task

            try{
                i  = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            }catch (e:Exception){
                e.printStackTrace()
            }


            if(i.resolveActivity(this.packageManager)!=null ){
                //checking if the intent wth help of resolveActivity & packagemnger
                // is returning null or not
                startActivityForResult(i,100)
            }else{
                //here what is intent gives us null value
                Toast.makeText(this@MainActivity,"Oops camera is not opening ..",
                    Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //getting data from camera
        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            val extras = data?.extras //safe call operator
            val btmp = extras?.get("data")as Bitmap

            // Configure the face detector
            //val objDetecFace = DetectFace(btmp,context)
            //calling of method which configures face detector

            configureFaceDetactor(btmp)

        }
    }

    private fun configureFaceDetactor(btmp: Bitmap) {
        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()

        //2) the image which we got from camera
        val image = InputImage.fromBitmap(btmp, 0)

        //3) face detector instance to detect img it take options as args
        //args are imp from highAccuracyOpts/args only images can be detected
        val detector = FaceDetection.getClient(highAccuracyOpts)
// Or, to use the default option

        //process the image
        val res = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully
                // ...

                // Task completed successfully
                // ...
                try {
                    var result:String?=""
                    var i = 1
                    for (face in faces) {
                        result += """
            Smile in % : ${face.smilingProbability!! * 100}\n
            face id ${i}\n
            eye prob in %:${face.leftEyeOpenProbability!! * 100 + face.rightEyeOpenProbability!! * 100}
            """.trimIndent()+"\n"
                        i++
//                        val bounds = face.boundingBox
//                        val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        if (leftEar != null) {
                            val leftEarPos = leftEar.position
                        }

                        // If classification was enabled:
                        if (face.smilingProbability != null) {
                            val smileProb = face.smilingProbability!!
                        }
                        if (face.rightEyeOpenProbability != null) {
                            val rightEyeOpenProb = face.rightEyeOpenProbability!!
                        }

                        // If face tracking was enabled:
                        if (face.trackingId != null) {
                            val id = face.trackingId!!
                        }
                    }
                    if (faces.isEmpty()) {
                        print("no faces detected")
                        Toast.makeText(
                            this@MainActivity,
                            "result : no faces detected",
                            Toast.LENGTH_LONG
                        ).show()
                        //Log.d("error face ", "no faces detection empty")
                    } else {
                        bind.textView.text=result
                        println("result : $result")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...

                // Task failed with an exception
                // ...
                e.printStackTrace()
            }
    }
}