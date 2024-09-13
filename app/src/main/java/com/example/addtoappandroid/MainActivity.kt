package com.example.addtoappandroid

import android.os.Bundle
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


class MainActivity : Api.HostAppApi, ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Api.HostAppApi.setUp(FlutterEngineCache.getInstance().get(MyApplication.ENGINE_ID)!!.dartExecutor.binaryMessenger, this);
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

    override fun cancel() {
//        moveTaskToBack(true)
//        finish()
    }

    override fun authSuccess(token: Api.Token) {
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
                val hostInfo  = Api.HostInfo()
                hostInfo.locale = "ru"
                hostInfo.paymentSystemId = "put your payment system id here"
                Api.ManiAuthApi(FlutterEngineCache.getInstance().get("book_engine")!!.dartExecutor).send(hostInfo, object : Api.VoidResult
                     {
                    override fun success() {
                    }

                    override fun error(error: Throwable) {
                    }

                })
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
