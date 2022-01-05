package com.darkbeast0106.qrcode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.util.Log

import android.widget.Toast

import com.google.zxing.integration.android.IntentIntegrator

import com.google.zxing.integration.android.IntentResult
import java.lang.Exception
import com.google.zxing.WriterException

import android.graphics.Bitmap
import android.view.View

import com.journeyapps.barcodescanner.BarcodeEncoder

import com.google.zxing.BarcodeFormat

import com.google.zxing.common.BitMatrix

import com.google.zxing.MultiFormatWriter





class MainActivity : AppCompatActivity() {
    private lateinit var textResult: TextView
    private lateinit var btnScan: Button
    private lateinit var btnGenerate: Button
    private lateinit var editTextQR: EditText
    private lateinit var imageResult: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        btnGenerate.setOnClickListener {
            val text = editTextQR.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Üres a beviteli mező", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val multiFormatWriter = MultiFormatWriter()
            try {
                //A szövegből QR_CODE bitmátrix készítése
                val bitMatrix = multiFormatWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE, 500, 500
                )
                val barcodeEncoder = BarcodeEncoder()
                //A bitmátrixból kép készítése amit meg tudunk jeleníteni.
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                imageResult.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }
        btnScan.setOnClickListener {
            // Kamera megnyitása QR_CODE lekéréséhez. A beolvasás után visszatérve az alkalmazáshoz.
            val intentIntegrator = IntentIntegrator(this@MainActivity)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentIntegrator.setPrompt("QR CODE SCAN")
            intentIntegrator.setCameraId(0)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setBarcodeImageEnabled(true)
            intentIntegrator.initiateScan()
        }
    }

    private fun init(){
        textResult = findViewById(R.id.text_Scan_Result)
        btnScan = findViewById(R.id.btn_scan_qr)
        btnGenerate = findViewById(R.id.btn_generate_qr)
        editTextQR = findViewById(R.id.edit_text_qr)
        imageResult = findViewById(R.id.image_result)
    }


    // Akkor hívódik meg ha az Activityből megnyitott másik activity bezáródáskor adatot küld vissza
    // ennek az Activitynek
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Megvizsgáljuk, hogy IntentIntegrator által készített hívás volt-e.
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // Ha Intentintegratoros hívás volt, megnézzük, hogy küldött-e vissza adatot.
            if (result.contents == null) {
                Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show()
            } else {
                textResult.text = result.contents

                //Ha a beolvasott QR_CODE egy url-t tartalmaz akkor megpróbál rálépni az url-re.
                try {
                    val url = Uri.parse(result.contents)
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                } catch (exception: Exception) {
                    Log.d("URI ERROR", exception.toString())
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}