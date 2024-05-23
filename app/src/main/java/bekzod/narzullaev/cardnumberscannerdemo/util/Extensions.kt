package bekzod.narzullaev.cardnumberscannerdemo.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import bekzod.narzullaev.cardnumberscannerdemo.data.CardInfo
import com.google.mlkit.vision.text.Text

fun analyzedText(results: Text): CardInfo? {
    var cardNumber = ""
    var expiryDate = ""
    for (block in results.textBlocks) {
        val blockText = block.text.replace(" ", "")
        if (blockText.length == 16 && blockText.matches(Regex("^\\d{16}"))) {
            cardNumber = blockText
        } else if (blockText.contains(Regex("(0[1-9]|1[0-2])/?([0-9]{4}|[0-9]{2})"))) {
            val date = blockText.replace(Regex("[a-zA-Z]"), "").replace(" ", "")
            val slashIndex = date.indexOf("/")
            if (slashIndex >= 2 && (slashIndex + 2) <= date.lastIndex) {
                expiryDate = date.substring(slashIndex - 2, slashIndex + 3)
            }
        }
    }
    return if (cardNumber.isNotEmpty()) {
        CardInfo(cardNumber, expiryDate)
    } else null
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun checkPermissionIsGranted(context: Context, permission: String): Boolean {
    val res: Int = context.checkCallingOrSelfPermission(permission)
    return res == PackageManager.PERMISSION_GRANTED
}

fun copyToClipboard(context: Context, text: String) {
    if (text.isBlank()) {
        return
    }

    val clipboard: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}