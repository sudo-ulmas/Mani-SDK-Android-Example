package sdk.pully.uz

import CancelReason
import DesignVariant
import HostAppApi
import HostInfo
import ManiAuthApi
import ManiEnvironment
import ManiResidentType
import Token
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import sdk.pully.uz.ui.theme.AddToAppAndroidTheme

class MainActivity : ComponentActivity() {

    private var currentEngineId: String? = null

    var onFlutterActivityResult: ((ActivityResult) -> Unit)? = null

    private val flutterActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MainActivity", "Flutter activity returned with result code: ${result.resultCode}")
        onFlutterActivityResult?.invoke(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddToAppAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SdkLauncherScreen(activity = this)
                }
            }
        }
    }

    fun createFreshFlutterEngine(): String {
        currentEngineId?.let { engineId ->
            FlutterEngineCache.getInstance().get(engineId)?.destroy()
            FlutterEngineCache.getInstance().remove(engineId)
            Log.d("MainActivity", "Previous engine destroyed: $engineId")
        }

        val engineId = "book_engine_${System.currentTimeMillis()}"
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
        intent.setClass(this, FlutterAddActivity::class.java)
        flutterActivityLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentEngineId?.let { engineId ->
            FlutterEngineCache.getInstance().get(engineId)?.destroy()
            FlutterEngineCache.getInstance().remove(engineId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdkLauncherScreen(activity: MainActivity) {
    val context = LocalContext.current

    // Form state
    var paymentSystemId by remember { mutableStateOf("") }
    var locale by remember { mutableStateOf("uz") }
    var pinfl by remember { mutableStateOf("31308977420022") }
    var phoneNumber by remember { mutableStateOf("917940244") }

    // Dropdown states
    var selectedResidentType by remember { mutableStateOf(ManiResidentType.RESIDENT) }
    var selectedDesignVariant by remember { mutableStateOf(DesignVariant.SMARTBANK) }

    var residentTypeExpanded by remember { mutableStateOf(false) }
    var designVariantExpanded by remember { mutableStateOf(false) }

    // Date picker state
    var lastLogin by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Token result state
    var receivedToken by remember { mutableStateOf<Token?>(null) }
    var operationCancelled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "SDK Launcher",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Configure the parameters below to launch the Mani Auth SDK.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider()

        // --- Text Fields ---
        SdkTextField(
            label = "Payment System ID",
            value = paymentSystemId,
            onValueChange = { paymentSystemId = it }
        )

        SdkTextField(
            label = "PINFL",
            value = pinfl,
            onValueChange = { pinfl = it },
            keyboardType = KeyboardType.Number
        )

        SdkTextField(
            label = "Phone Number",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            keyboardType = KeyboardType.Phone
        )

        SdkTextField(
            label = "Locale",
            value = locale,
            onValueChange = { locale = it }
        )

        // --- Dropdowns ---

        SdkDropdown(
            label = "Resident Type",
            options = ManiResidentType.values().map { it.name },
            selectedOption = selectedResidentType.name,
            expanded = residentTypeExpanded,
            onExpandedChange = { residentTypeExpanded = it },
            onOptionSelected = { name ->
                selectedResidentType = ManiResidentType.valueOf(name)
                residentTypeExpanded = false
            }
        )

        SdkDropdown(
            label = "Design Variant",
            options = DesignVariant.values().map { it.name },
            selectedOption = selectedDesignVariant.name,
            expanded = designVariantExpanded,
            onExpandedChange = { designVariantExpanded = it },
            onOptionSelected = { name ->
                selectedDesignVariant = DesignVariant.valueOf(name)
                designVariantExpanded = false
            }
        )

        SdkDatePickerField(
            label = "Last Login",
            value = lastLogin,
            onDateSelected = { lastLogin = it },
            showDialog = showDatePicker,
            onShowDialog = { showDatePicker = true },
            onDismiss = { showDatePicker = false }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // --- Launch Button ---
        Button(
            onClick = {
                // Reset previous results
                receivedToken = null
                operationCancelled = false

                val freshEngineId = activity.createFreshFlutterEngine()
                val hostInfo = HostInfo(
                    paymentSystemId = paymentSystemId.trim(),
                    locale = locale.trim(),
                    environment = ManiEnvironment.PROD,
                    pinfl = pinfl.trim(),
                    phoneNumber = phoneNumber.trim(),
                    residentType = selectedResidentType,
                    designVariant = selectedDesignVariant,
                )

                val engine = FlutterEngineCache.getInstance().get(freshEngineId)!!

                HostAppApi.setUp(
                    engine.dartExecutor.binaryMessenger,
                    object : HostAppApi {
                        override fun cancel(reason: CancelReason) {
                            Log.d("cancel", "Operation cancelled")
                            operationCancelled = true
                            FlutterAddActivity.currentInstance?.finishWithResult(Activity.RESULT_CANCELED)
                        }

                        override fun authSuccess(token: Token) {
                            Log.d("success", "Operation succeeded")
                            receivedToken = token
                            FlutterAddActivity.currentInstance?.finishWithResult(Activity.RESULT_OK)
                        }
                    }
                )

                activity.onFlutterActivityResult = { result ->
                    when (result.resultCode) {
                        Activity.RESULT_OK -> Log.d("MainActivity", "Auth success")
                        Activity.RESULT_CANCELED -> {
                            Log.d("MainActivity", "Operation cancelled")
                            if (!operationCancelled && receivedToken == null) {
                                operationCancelled = true
                            }
                        }
                    }
                }

                ManiAuthApi(engine.dartExecutor).send(hostInfo) { result ->
                    result.onSuccess {
                        Log.d("success", "Operation succeeded")
                    }.onFailure { error ->
                        Log.d("error", "Operation failed: ${error.message}")
                    }
                }

                activity.launchFlutterActivity(freshEngineId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Launch SDK", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        // --- Result Section ---
        if (operationCancelled && receivedToken == null) {
            ResultCard(
                title = "Cancelled",
                content = "The SDK operation was cancelled by the user.",
                isError = true,
                context = context
            )
        }

        receivedToken?.let { token ->
            TokenResultCard(token = token, context = context)
        }
    }
}

@Composable
fun SdkTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdkDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun TokenResultCard(token: Token, context: Context) {
    // Adjust these fields based on your actual Token data class structure
    val tokenString = token.toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "✅ Auth Successful — Token Received",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // Token box with copy button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = tokenString,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SDK Token", tokenString)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Token copied!", Toast.LENGTH_SHORT).show()
                        }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Copy token",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Copy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdkDatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    showDialog: Boolean,
    onShowDialog: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        onDateSelected(sdf.format(Date(millis)))
                    }
                    onDismiss()
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("Not set") },
        trailingIcon = {
            IconButton(onClick = onShowDialog) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDialog() },
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun ResultCard(title: String, content: String, isError: Boolean, context: Context) {
    val bgColor = if (isError) Color(0xFFFFEDED) else Color(0xFFEDFFED)
    val textColor = if (isError) Color(0xFFB00020) else Color(0xFF1B5E20)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, color = textColor, fontSize = 15.sp)
        Text(text = content, color = textColor, fontSize = 13.sp)
    }
}