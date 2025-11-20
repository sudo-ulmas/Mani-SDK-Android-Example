package com.example.addtoappandroid

import DesignVariant.SMARTBANK
import HostAppApi
import HostInfo
import ManiAuthApi
import ManiEnvironment.PROD
import ManiResidentType.*
import Token
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.addtoappandroid.ui.theme.AddToAppAndroidTheme
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor


class MainActivity :  ComponentActivity() {

    private var currentEngineId: String? = null
    private var preWarmEngine: FlutterEngine? = null

    private val flutterActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MainActivity", "Flutter activity returned with result code: ${result.resultCode}")
        // Don't destroy engine immediately - let it be cleaned up in onDestroy or when creating new one
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddToAppAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android", this)
                }
            }
        }
    }

     fun createFreshFlutterEngine(): String {
        // Clean up previous engine if it exists
        currentEngineId?.let { engineId ->
            FlutterEngineCache.getInstance().get(engineId)?.destroy()
            FlutterEngineCache.getInstance().remove(engineId)
            Log.d("MainActivity", "Previous engine destroyed: $engineId")
        }

        val engineId = "book_engine_${System.currentTimeMillis()}"

        // Create fresh engine
        val flutterEngine = FlutterEngine(this).apply {
            dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        }

        FlutterEngineCache.getInstance().put(engineId, flutterEngine)
        currentEngineId = engineId
        Log.d("MainActivity", "Fresh engine created: $engineId")
        return engineId
    }

    fun launchFlutterActivity(engineId: String) {
        val intent = FlutterActivity
            .withCachedEngine(engineId)
            .build(this)
        flutterActivityLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up engine when activity is destroyed
        currentEngineId?.let { engineId ->
            FlutterEngineCache.getInstance().get(engineId)?.destroy()
            FlutterEngineCache.getInstance().remove(engineId)
        }
    }



}



@Composable
fun Greeting(name: String, activity: ComponentActivity) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Button(
            onClick = {
                val freshEngineId = (activity as MainActivity).createFreshFlutterEngine()
                val hostInfo  = HostInfo(
                    paymentSystemId = "",
                    locale = "uz",
                    environment = PROD,
                    pinfl = "31308977420022",
                    phoneNumber = "917940244",
                    residentType = RESIDENT,
                    designVariant = DesignVariant.MANI,
                )
                HostAppApi.setUp(FlutterEngineCache.getInstance().get(freshEngineId)!!.dartExecutor.binaryMessenger, object: HostAppApi {
                    override fun cancel() {
                        Log.d("cancel","Operation cancelled")
                        // Don't finish activity here, let Flutter handle it
                    }

                    override fun authSuccess(token: Token) {
                        Log.d("success","Operation succeded")
                        // Don't finish activity here, let Flutter handle it
                    }
                });
                ManiAuthApi(FlutterEngineCache.getInstance().get(freshEngineId)!!.dartExecutor).send(hostInfo) { result ->
                    result.onSuccess {
                        Log.d("success", "Operation succeeded")
                    }.onFailure { error ->
                        Log.d("error", "Operation failed: ${error.message}")
                    }
                }
activity.launchFlutterActivity(freshEngineId);
                      },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Click Me", fontSize = 20.sp)
        }
    }
}
