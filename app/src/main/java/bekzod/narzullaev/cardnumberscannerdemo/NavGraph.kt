package bekzod.narzullaev.cardnumberscannerdemo

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bekzod.narzullaev.cardnumberscannerdemo.data.CardInfo
import bekzod.narzullaev.cardnumberscannerdemo.screens.ResultScreen
import bekzod.narzullaev.cardnumberscannerdemo.screens.ScanScreen
import kotlinx.serialization.Serializable

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Result
    ) {
        composable<Result> { backStackEntry ->
            val result by backStackEntry
                .savedStateHandle
                .getStateFlow("result", CardInfo("", ""))
                .collectAsState()

            LaunchedEffect(key1 = Unit) {
                Log.d("TAG", "NavGraph: $result")
            }

            ResultScreen(
                result = result,
                onScan = {
                    backStackEntry.savedStateHandle.remove<CardInfo>("result")

                    navController.navigate(Scan)
                }
            )
        }

        composable<Scan> {
            ScanScreen(
                onSuccess = { result ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("result", result)

                    if (navController.currentDestination?.route == Scan::class.qualifiedName) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}


@Serializable
object Result

@Serializable
object Scan

