package bekzod.narzullaev.cardnumberscannerdemo.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.text.isDigitsOnly
import bekzod.narzullaev.cardnumberscannerdemo.data.CardInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextAnalyzer(val onSuccess: (CardInfo) -> Unit) : ImageAnalysis.Analyzer {

    private val options = TextRecognizerOptions.DEFAULT_OPTIONS

    private val scanner = TextRecognition.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image
            ?.let { image ->
                scanner.process(
                    InputImage.fromMediaImage(
                        image, imageProxy.imageInfo.rotationDegrees
                    )
                ).addOnSuccessListener { results ->
                    results.textBlocks
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(",") { it.text }
                        ?.let {
                            Log.d("TAG", "analyzedText: $it")
                        }

                    val cardInfo = analyzedText(results)
                    cardInfo?.let(onSuccess)
                }.addOnCompleteListener {
                    imageProxy.close()
                }
            }
    }
}