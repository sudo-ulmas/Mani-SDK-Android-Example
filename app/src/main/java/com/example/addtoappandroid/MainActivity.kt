package com.example.addtoappandroid

import HostAppApi
import HostInfo
import ManiAuthApi
import Token
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import io.flutter.embedding.engine.FlutterEngineCache
import uz.myid.android.sdk.capture.MyIdClient
import uz.myid.android.sdk.capture.MyIdConfig
import uz.myid.android.sdk.capture.MyIdException
import uz.myid.android.sdk.capture.MyIdResult
import uz.myid.android.sdk.capture.MyIdResultListener
import uz.myid.android.sdk.capture.model.MyIdBuildMode


class MainActivity : HostAppApi, ComponentActivity(), MyIdResultListener {

    private val client = MyIdClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMyId()
        HostAppApi.setUp(FlutterEngineCache.getInstance().get(MyApplication.ENGINE_ID)!!.dartExecutor.binaryMessenger, this);
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        client.handleActivityResult(resultCode, this)
    }

    private fun startMyId() {
        val config = MyIdConfig.builder(clientId = "")
            .withClientHash("", "")
            .withPassportData("")
            .withBirthDate("")
            .withBuildMode(MyIdBuildMode.PRODUCTION)
            .build()

        /*
           Start the flow. 1 should be your request code (customize as needed).
           Must be an Activity or Fragment (support library).
           This request code will be important for you on onActivityResult() to identify the MyIdResultListener.
        */
    }

    override fun cancel() {
//        moveTaskToBack(true)
//        finish()
        Log.d("hi", "bye")
    }

    override fun authSuccess(token: Token) {
        Log.d("token",  token.accessToken.toString())
    }

    override fun onError(exception: MyIdException) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(result: MyIdResult) {
        TODO("Not yet implemented")
    }

    override fun onUserExited() {
        TODO("Not yet implemented")
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
                val hostInfo  = HostInfo(
                    paymentSystemId = "your payment system id",
                    locale = "uz",
                    environment = ManiEnvironment.PROD,
                    pinfl = "pinfl",
                    residentType = ManiResidentType.RESIDENT
                )
                ManiAuthApi(FlutterEngineCache.getInstance().get("book_engine")!!.dartExecutor).send(hostInfo) { result ->
                    result.onSuccess {
                        Log.d("success", "Operation succeeded")
                    }.onFailure { error ->
                        Log.d("error", "Operation failed: ${error.message}")
                    }
                }
                activity.startActivity(
                    FlutterActivity
                        .withCachedEngine(MyApplication.ENGINE_ID)
                        .build(activity)
                )

                      },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Click Me", fontSize = 20.sp)
        }
    }
}
