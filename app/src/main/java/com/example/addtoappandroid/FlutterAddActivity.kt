package sdk.pully.uz

import android.app.Activity
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity

class FlutterAddActivity: FlutterActivity() {

    companion object {
        var currentInstance: FlutterAddActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentInstance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        currentInstance = null
    }

    fun finishWithResult(resultCode: Int) {
        setResult(resultCode)
        finish()
    }
}
