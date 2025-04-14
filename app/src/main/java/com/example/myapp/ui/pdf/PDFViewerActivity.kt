package com.example.myapp.ui.pdf

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityPdfViewerBinding
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import okhttp3.*
import java.io.File
import java.io.IOException

class PDFViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewerBinding
    private lateinit var pdfView: PDFView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfView = binding.pdfView
        binding.progressBar.visibility = View.VISIBLE

        // Get PDF URL from intent
        val pdfUrl = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"

        // Download and display PDF
        downloadAndDisplayPdf(pdfUrl)
    }

    private fun downloadAndDisplayPdf(pdfUrl: String) {
        val request = Request.Builder()
            .url(pdfUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PDFViewerActivity,
                        "Failed to download PDF: ${e.message}",
                        Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@PDFViewerActivity,
                            "Server error: ${response.code}",
                            Toast.LENGTH_LONG).show()
                        finish()
                    }
                    return
                }

                try {
                    // Save PDF to cache
                    val pdfFile = File(cacheDir, "temp.pdf")
                    response.body?.byteStream()?.use { input ->
                        pdfFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    runOnUiThread {
                        displayPdf(pdfFile)
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@PDFViewerActivity,
                            "Error processing PDF",
                            Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        })
    }

    private fun displayPdf(pdfFile: File) {
        try {
            pdfView.fromFile(pdfFile)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .onLoad {
                    binding.progressBar.visibility = View.GONE
                }
                .onError { error ->
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this,
                        "Error displaying PDF: ${error.message}",
                        Toast.LENGTH_LONG).show()
                    finish()
                }
                .scrollHandle(DefaultScrollHandle(this))
                .load()
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this,
                "Failed to open PDF",
                Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onDestroy() {
        // Clean up temporary file
        File(cacheDir, "temp.pdf").delete()
        super.onDestroy()
    }
}