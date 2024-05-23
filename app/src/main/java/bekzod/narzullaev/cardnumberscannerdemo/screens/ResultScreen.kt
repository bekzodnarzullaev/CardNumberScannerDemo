package bekzod.narzullaev.cardnumberscannerdemo.screens

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import bekzod.narzullaev.cardnumberscannerdemo.data.CardInfo
import bekzod.narzullaev.cardnumberscannerdemo.util.checkPermissionIsGranted
import bekzod.narzullaev.cardnumberscannerdemo.util.copyToClipboard
import bekzod.narzullaev.cardnumberscannerdemo.util.openAppSettings

@Composable
fun ResultScreen(
    result: CardInfo,
    onScan: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    var isCameraGranted by remember { mutableStateOf(false) }
    var isScanBtnClicked by remember { mutableStateOf(false) }

    val cameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            isCameraGranted = isGranted
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (checkPermissionIsGranted(context, Manifest.permission.CAMERA)) {
            isCameraGranted = true
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(key1 = isScanBtnClicked, key2 = isCameraGranted) {
        val isPermanentlyDenied =
            !shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        if (!isCameraGranted && isScanBtnClicked && isPermanentlyDenied) {
            activity.openAppSettings()
        }
        isScanBtnClicked = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (result.cardNumber.isNotBlank()) {
            Text(
                text = result.cardNumber + ", " + result.expireDate,
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable {
                        copyToClipboard(context, result.cardNumber)
                    }
                    .padding(vertical = 12.dp)
            )
        }
        Button(
            onClick = {
                isScanBtnClicked = true
                if (!isCameraGranted) {
                    cameraPermission.launch(Manifest.permission.CAMERA)
                    return@Button
                }
                onScan()
            }
        ) {
            Text(text = "Scan")
        }
    }
}