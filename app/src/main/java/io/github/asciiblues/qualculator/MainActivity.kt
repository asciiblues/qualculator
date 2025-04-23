package io.github.asciiblues.qualculator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsSwitch
import io.github.asciiblues.emicalculator.R
import io.github.asciiblues.qualculator.ui.theme.QualculatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Scanner
import kotlin.math.PI
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    var isClearAll by mutableStateOf(false)
    val vibrtFileName = "vibrt.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QualculatorTheme {
                val scope = rememberCoroutineScope()
                val currentScreen = rememberSaveable { mutableIntStateOf(0) } // 0 = Home, 1 = Converter, 2 = Settings, other for claer
                val snackbarHostState = remember { SnackbarHostState() }
                setStatusBarColor()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentScreen.value == 0,
                                icon = {
                                    Icon(
                                        if (currentScreen.value == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = { Text("Home") },
                                onClick = { currentScreen.value = 0 }
                            )

                            NavigationBarItem(
                                selected = currentScreen.value == 1,
                                icon = {
                                    Icon(
                                        painterResource(
                                            if (currentScreen.value == 1)
                                                R.drawable.baseline_drive_file_move_24
                                            else
                                                R.drawable.outline_drive_file_move_24
                                        ),
                                        contentDescription = "Converter"
                                    )
                                },
                                label = { Text("Convert") },
                                onClick = { currentScreen.value = 1 }
                            )

                            NavigationBarItem(
                                selected = currentScreen.value == 2,
                                icon = {
                                    Icon(
                                        if (currentScreen.value == 2)
                                            Icons.Filled.Settings
                                        else
                                            Icons.Outlined.Settings,
                                        contentDescription = "Settings"
                                    )
                                },
                                label = { Text("Settings") },
                                onClick = { currentScreen.value = 2 }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentScreen.value in 0..1) {
                            FloatingActionButton(
                                onClick = {
                                    isClearAll = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_clear_all_24),
                                    contentDescription = "Clear All"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        mapp(
                            modifier = Modifier.padding(innerPadding),
                            snackbarHostState = snackbarHostState,
                            scope = scope,
                            currentScreen = currentScreen
                        )
                    }
                }
            }
        }
    }

    fun writeSettingFile(name: String, value: String, context: Context) {
        val file = File(context.filesDir, name)
        try {
            val writer = FileWriter(file)
            writer.write(value)
            writer.close()
        } catch (ex: Exception) {
            Log.d("DEV LOG", "Error :- ${ex.message}")
            Toast.makeText(context, "Error :- ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun readSettingFile(name: String, context: Context): String? {
        var value: String? = null
        val file = File(context.filesDir, name)
        try {
            val reader = Scanner(file)
            value = reader.nextLine()
            reader.close()
        } catch (ex: Exception) {
            Log.d("DEV LOG", "Error :- ${ex.message}")
            Toast.makeText(context, "Error :- ${ex.message}", Toast.LENGTH_SHORT).show()
        }
        return value
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun mapp(
        modifier: Modifier = Modifier,
        snackbarHostState: SnackbarHostState = SnackbarHostState(),
        scope: CoroutineScope = CoroutineScope(
            Dispatchers.Main
        ) /* Default for Preview */
        ,
        currentScreen: MutableState<Int> = mutableStateOf(0)
    ) {

        val scrollState = rememberScrollState()
        var isBottomScroll by remember { mutableStateOf(false) }
        val context = LocalContext.current
        var isVibrt by remember { mutableStateOf(true) }

        if (isClearAll) {
            LaunchedEffect(Unit) {
                scope.launch {
                    when (currentScreen.value) {
                        0 -> {
                            currentScreen.value = 4 // it is for clear
                            delay(60)
                            isClearAll = false
                            currentScreen.value = 0
                        }
                        1 -> {
                            currentScreen.value = 5 // it is for clear
                            delay(60)
                            isClearAll = false
                            currentScreen.value = 1
                        }
                    }
                }
            }
        }

        if (isBottomScroll) {
            LaunchedEffect(Unit) {
                scope.launch {
                    scrollState.animateScrollTo(scrollState.maxValue)
                    delay(100)
                    isBottomScroll = false
                }
            }
        }

        @Composable
        fun readVibrt() {
            LaunchedEffect(Unit) {
                val f = File(context.filesDir, vibrtFileName)
                if (!f.exists()) {
                    writeSettingFile(vibrtFileName, "1", context)
                    Log.d(
                        "DEV LOG",
                        "File Created with value of 1 (true), File Name :- $vibrtFileName, For :- $isVibrt (vibrate on calculate button click [0] )"
                    )
                }
                isVibrt = readSettingFile(vibrtFileName, context) == "1"
                Log.d("DEV LOG", "isVibrt Initial value :- $isVibrt")
            }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            when (currentScreen.value) {
                0 -> {
                        readVibrt()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(5.dp)
                                .imePadding()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            val calcType = listOf(
                                " < < Select Calculator Type > >",
                                "Simple Interest Calculator",
                                "Principle Amount Calculator",
                                "Rate of Interest Calculator",
                                "Time of Interest Calculator",
                                "Average Calculator",
                                "Square Area Calculator",
                                "Rectangle Area Calculator",
                                "Circle Area Calculator",
                                "Cube Volume Calculator",
                                "Cuboid Volume Calculator",
                                "Cylinder Volume Calculator"
                            )

                            var isCalcTypeExpanded by remember { mutableStateOf(false) }
                            var selectedCalcType by remember { mutableStateOf(calcType[0]) }

                            Spacer(modifier = Modifier.height(15.dp))

                            ExposedDropdownMenuBox(
                                expanded = isCalcTypeExpanded,
                                onExpandedChange = { isCalcTypeExpanded = !isCalcTypeExpanded }
                            ) {
                                TextField(
                                    value = selectedCalcType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Calculator Type") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isCalcTypeExpanded
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .padding(horizontal = 6.dp)
                                        .fillMaxWidth(),
                                )

                                ExposedDropdownMenu(
                                    expanded = isCalcTypeExpanded,
                                    onDismissRequest = { isCalcTypeExpanded = false }
                                ) {
                                    calcType.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                selectedCalcType = selectionOption
                                                isCalcTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                            when (selectedCalcType) {
                                "Simple Interest Calculator" -> {
                                    Card {
                                        val options = listOf("In Year", "In Month")
                                        var selectedOptionText by remember { mutableStateOf(options[0]) }
                                        val rOptions = listOf("Per Year", "Per Month")
                                        var rSelectedOptionText by remember { mutableStateOf(rOptions[0]) }
                                        var pText by remember { mutableStateOf("") }
                                        var p = pText.toDoubleOrNull() ?: 0.0
                                        var rText by remember { mutableStateOf("") }
                                        var r = rText.toDoubleOrNull() ?: 0.0
                                        var si by remember { mutableDoubleStateOf(0.00) }
                                        var a by remember { mutableDoubleStateOf(0.00) }
                                        var tText by remember { mutableStateOf("") }
                                        var t = tText.toDoubleOrNull() ?: 0.0
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Simple Interest Calculator",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = pText,
                                                onValueChange = { pText = it },
                                                label = { Text("Principal Amount") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = rText,
                                                    onValueChange = { rText = it },
                                                    label = { Text("Rate of Interest") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = rSelectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select Rate type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        rOptions.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    rSelectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = tText,
                                                    onValueChange = { tText = it },
                                                    label = { Text("Time") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = selectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select time type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        options.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    selectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        si = when {
                                                            rSelectedOptionText == "Per Year" && selectedOptionText == "In Month" -> {
                                                                (p * r * (t / 12.0)) / 100
                                                            }

                                                            rSelectedOptionText == "Per Month" && selectedOptionText == "In Year" -> {
                                                                (p * r * (t * 12.0)) / 100
                                                            }

                                                            rSelectedOptionText == "Per Month" && selectedOptionText == "In Month" -> {
                                                                (p * r * t) / 100
                                                            }

                                                            rSelectedOptionText == "Per Year" && selectedOptionText == "In Year" -> {
                                                                (p * r * t) / 100
                                                            }

                                                            else -> 0.0 // fallback in case of invalid input
                                                        }
                                                        si =
                                                            BigDecimal(si).setScale(4, RoundingMode.HALF_UP)
                                                                .toDouble()
                                                        a = p + si
                                                        // vibart the phone
                                                        if (isVibrt) {
                                                            if (Build.VERSION.SDK_INT <= 26) {
                                                                vibrator.vibrate(100)
                                                            } else {
                                                                vibrator.vibrate(
                                                                    VibrationEffect.createOneShot(
                                                                        100,
                                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    } catch (ex: Exception) {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                "Error :- ${ex.message}",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text("Calculate")
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Column(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "Simple Interest = $si",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Spacer(modifier = Modifier.height(5.dp))
                                                Text(
                                                    "Total Amount = $a",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Principle Amount Calculator" -> {
                                    Card {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            var iText by remember { mutableStateOf("") }
                                            var i = iText.toDoubleOrNull() ?: 0.0
                                            var rText by remember { mutableStateOf("") }
                                            var r = rText.toDoubleOrNull() ?: 0.0
                                            var tText by remember { mutableStateOf("") }
                                            var t = tText.toDoubleOrNull() ?: 0.0
                                            var p by remember { mutableDoubleStateOf(0.00) }
                                            val options = listOf("In Year", "In Month")
                                            var selectedOptionText by remember { mutableStateOf(options[0]) }
                                            val rOptions = listOf("Per Year", "Per Month")
                                            var rSelectedOptionText by remember { mutableStateOf(rOptions[0]) }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Principle Amount",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = iText,
                                                onValueChange = { iText = it },
                                                label = { Text("Interest") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = rText,
                                                    onValueChange = { rText = it },
                                                    label = { Text("Rate of Interest") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = rSelectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select Rate type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        rOptions.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    rSelectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = tText,
                                                    onValueChange = { tText = it },
                                                    label = { Text("Time") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = selectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select time type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        options.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    selectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        p = when {
                                                            rSelectedOptionText == "Per Year" && selectedOptionText == "In Month" -> {
                                                                ((i * 100.0) / (r * (t / 12.0)))
                                                            }

                                                            rSelectedOptionText == "Per Month" && selectedOptionText == "In Year" -> {
                                                                ((i * 100.0) / (r * (t * 12.0)))
                                                            }

                                                            rSelectedOptionText == "Per Month" && selectedOptionText == "In Month" -> {
                                                                ((i * 100.0) / (r * t))
                                                            }

                                                            rSelectedOptionText == "Per Year" && selectedOptionText == "In Year" -> {
                                                                ((i * 100.0) / (r * t))
                                                            }

                                                            else -> 0.0 // fallback in case of invalid input
                                                        }
                                                        p = BigDecimal(p).setScale(4, RoundingMode.HALF_UP)
                                                            .toDouble()
                                                        if (isVibrt) {
                                                            if (Build.VERSION.SDK_INT <= 26) {
                                                                vibrator.vibrate(100)
                                                            } else {
                                                                vibrator.vibrate(
                                                                    VibrationEffect.createOneShot(
                                                                        100,
                                                                        VibrationEffect.DEFAULT_AMPLITUDE
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    } catch (ex: Exception) {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                "Error :- ${ex.message}",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                            if (isVibrt) {
                                                                if (Build.VERSION.SDK_INT <= 26) {
                                                                    vibrator.vibrate(100)
                                                                } else {
                                                                    vibrator.vibrate(
                                                                        VibrationEffect.createOneShot(
                                                                            100,
                                                                            VibrationEffect.DEFAULT_AMPLITUDE
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Principle Amount = $p",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                }

                                "Rate of Interest Calculator" -> {
                                    Card {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            var iText by remember { mutableStateOf("") }
                                            var i = iText.toDoubleOrNull() ?: 0.0
                                            var pText by remember { mutableStateOf("") }
                                            var p = pText.toDoubleOrNull() ?: 0.0
                                            var r by remember { mutableDoubleStateOf(0.0) }
                                            var tText by remember { mutableStateOf("") }
                                            var t = tText.toDoubleOrNull() ?: 0.0
                                            val options = listOf("In Year", "In Month")
                                            var selectedOptionText by remember { mutableStateOf(options[0]) }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Rate of Interest", fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = iText,
                                                onValueChange = { iText = it },
                                                label = { Text("Interest") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            OutlinedTextField(
                                                value = pText,
                                                onValueChange = { pText = it },
                                                label = { Text("Principle Amount") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = tText,
                                                    onValueChange = { tText = it },
                                                    label = { Text("Time") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = selectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select time type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        options.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    selectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        r = when (selectedOptionText) {
                                                            "In Month" -> {
                                                                i * 100.0 / (p * (t / 12.0))
                                                            }

                                                            "In Year" -> {
                                                                i * 100.0 / (p * t)
                                                            }

                                                            else -> 0.0
                                                        }
                                                    } catch (ex: Exception) {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                "Error :- ${ex.message}",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Rate = $r%",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                }

                                "Time of Interest Calculator" -> {
                                    Card {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            var iText by remember { mutableStateOf("") }
                                            var i = iText.toDoubleOrNull() ?: 0.0
                                            var pText by remember { mutableStateOf("") }
                                            var p = pText.toDoubleOrNull() ?: 0.0
                                            val rOptions = listOf("Per Year", "Per Month")
                                            var rSelectedOptionText by remember { mutableStateOf(rOptions[0]) }
                                            var rText by remember { mutableStateOf("") }
                                            var r = rText.toDoubleOrNull() ?: 0.0
                                            var t by remember { mutableDoubleStateOf(0.0) }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                text = "Get Time of Interest",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = iText,
                                                singleLine = true,
                                                onValueChange = { iText = it },
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                label = { Text("Interest") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            OutlinedTextField(
                                                value = pText,
                                                singleLine = true,
                                                onValueChange = { pText = it },
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                label = { Text("Principle Amount") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(2.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = rText,
                                                    onValueChange = { rText = it },
                                                    label = { Text("Rate of Interest") },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .width(180.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                var expanded by remember { mutableStateOf(false) }

                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = rSelectedOptionText,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Select Rate type") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        rOptions.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    rSelectedOptionText = selectionOption
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        t = when (rSelectedOptionText) {
                                                            "Per Year" -> {
                                                                i * 100 / (p * r)
                                                            }

                                                            "Per Month" -> {
                                                                i * 100 / (p * r * 12.0)
                                                            }

                                                            else -> 0.0
                                                        }
                                                    } catch (ex: Exception) {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                "Error :- ${ex.message}",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(
                                                        MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                            ) {
                                                Text(
                                                    "Time = $t",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }

                                "Average Calculator" -> {
                                    Card {
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer
                                                ), horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            var numItem by remember { mutableIntStateOf(0) }
                                            var chcknum by remember { mutableStateOf(false) }
                                            var isOnItems by remember { mutableStateOf(false) }
                                            var average by remember { mutableDoubleStateOf(0.00) }
                                            var items by remember { mutableStateOf(mutableListOf<Double>()) }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Average",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = if (numItem == 0) "" else numItem.toString(),
                                                onValueChange = { numItem = it.toIntOrNull() ?: 0 },
                                                label = { Text("How many Number of Items") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    items = List(numItem) { 0.00 }.toMutableList()
                                                    // animated scroll to bottom
                                                    isBottomScroll = true
                                                    //on the chcknum
                                                    if (numItem < 1) {
                                                        chcknum = true
                                                    } else {
                                                        chcknum = false
                                                        isOnItems = true
                                                        numItem = 0
                                                    }

                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Add ${if (numItem < 2) "Item" else "Items"}") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            if (!chcknum) {
                                                if (isOnItems) {
                                                    LazyColumn(
                                                        modifier = Modifier
                                                            .padding(vertical = 3.dp, horizontal = 5.dp)
                                                            .height(125.dp)
                                                            .fillMaxWidth()
                                                    ) {
                                                        itemsIndexed(items) { index, item ->
                                                            val imeAction =
                                                                if (index < items.lastIndex) ImeAction.Next else ImeAction.Done

                                                            OutlinedTextField(
                                                                value = if (item == 0.00) "" else item.toString(),
                                                                onValueChange = { newValue ->
                                                                    val parsedValue =
                                                                        newValue.toDoubleOrNull()
                                                                    if (parsedValue != null) {
                                                                        items = items.toMutableList()
                                                                            .apply {
                                                                                this[index] = parsedValue
                                                                            }
                                                                    }
                                                                },
                                                                modifier = Modifier
                                                                    .padding(horizontal = 5.dp)
                                                                    .fillMaxWidth(),
                                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                                    keyboardType = KeyboardType.Decimal,
                                                                    imeAction = imeAction
                                                                ),
                                                                label = { Text("Item ${index + 1}") }
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        "^ ^ ^ This is scrollable ^ ^ ^",
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    Button(
                                                        onClick = {
                                                            //todo : main average function
                                                            //sun of all digits in the list
                                                            average = items.sum()
                                                            //divide the sum by the number of items
                                                            average /= items.size
                                                            //todo : done !!!

                                                            //todo vibrate
                                                            if (isVibrt) {
                                                                if (Build.VERSION.SDK_INT <= 26) {
                                                                    vibrator.vibrate(100)
                                                                } else {
                                                                    vibrator.vibrate(
                                                                        VibrationEffect.createOneShot(
                                                                            100,
                                                                            VibrationEffect.DEFAULT_AMPLITUDE
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .padding(8.dp)
                                                            .fillMaxWidth()
                                                    ) { Text("Calculate") }
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 8.dp)
                                                            .shadow(
                                                                elevation = 1.dp,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                                            .fillMaxWidth()
                                                            .height(50.dp)
                                                    ) {
                                                        Text(
                                                            "Average = $average",
                                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier.align(Alignment.Center)
                                                        )
                                                    }
                                                }
                                            } else {
                                                LaunchedEffect(Unit) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            "You can't add item (s) without enter number of item !",
                                                            withDismissAction = true
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                "Square Area Calculator" -> {
                                    Card {
                                        var sideText by remember { mutableStateOf("") }
                                        var side = sideText.toDoubleOrNull() ?: 0.0
                                        var area by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "Get Area Of Square",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = sideText,
                                                onValueChange = { sideText = it },
                                                label = { Text("Side") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "Note :- All Side Must Be Same",
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    area = side.pow(2)
                                                    //not use area = side * side , simply use pow() it mean side^2 (area = side power of 2)
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                }, modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Area Of Square = $area²",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Rectangle Area Calculator" -> {
                                    Card {
                                        var bText by remember { mutableStateOf("") }
                                        var b = bText.toDoubleOrNull() ?: 0.0
                                        var lText by remember { mutableStateOf("") }
                                        var l = lText.toDoubleOrNull() ?: 0.0
                                        var area by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer
                                                )
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Area Of Rectangle",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = lText,
                                                onValueChange = { lText = it },
                                                label = { Text("Length") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = bText,
                                                onValueChange = { bText = it },
                                                label = { Text("Breadth") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    area = l * b
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Area Of Rectangle = $area²",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                }

                                "Circle Area Calculator" -> {
                                    Card {
                                        var rText by remember { mutableStateOf("") }
                                        var r = rText.toDoubleOrNull() ?: 0.0
                                        var area by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer
                                                )
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Area Of Circle",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = rText,
                                                onValueChange = { rText = it },
                                                label = { Text("Radius") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    area = PI * r.pow(2)
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)

                                            ) {
                                                Text(
                                                    "Area Of Circle = $area²",
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Cube Volume Calculator" -> {
                                    Card {
                                        var sideText by remember { mutableStateOf("") }
                                        var side = sideText.toDoubleOrNull() ?: 0.0
                                        var volume by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer
                                                )
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Volume Of Cube",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = sideText,
                                                onValueChange = { sideText = it },
                                                label = { Text("Side") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    "Note :- All Side Must Be Same",
                                                    modifier = Modifier.align(Alignment.CenterStart),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    //todo : main area function
                                                    volume = side.pow(3)
                                                    //not use area = side * side * side , simply use pow() it mean side^3 (area = side power of 3)
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Volume Of Cube = $volume³",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Cuboid Volume Calculator" -> {
                                    Card {
                                        var lText by remember { mutableStateOf("") }
                                        var l = lText.toDoubleOrNull() ?: 0.0
                                        var bText by remember { mutableStateOf("") }
                                        var b = bText.toDoubleOrNull() ?: 0.0
                                        var hText by remember { mutableStateOf("") }
                                        var h = hText.toDoubleOrNull() ?: 0.0
                                        var volume by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer
                                                )
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Volume Of Cuboid",
                                                fontSize = 25.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = lText,
                                                onValueChange = { lText = it },
                                                label = { Text("Length") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = bText,
                                                onValueChange = { bText = it },
                                                label = { Text("Breadth") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = hText,
                                                onValueChange = { hText = it },
                                                label = { Text("Height") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    //todo : main area function
                                                    volume = l * b * h
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Volume Of Cuboid = $volume³",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        }
                                    }
                                }

                                "Cylinder Volume Calculator" -> {
                                    Card {
                                        var hegText by remember { mutableStateOf("") }
                                        var rdsText by remember { mutableStateOf("") }
                                        var volume by remember { mutableDoubleStateOf(0.0) }

                                        fun calcVol() {
                                            try {
                                                var height = BigDecimal(hegText.toDoubleOrNull() ?: 0.0)
                                                var radius = BigDecimal(rdsText.toDoubleOrNull() ?: 0.0)
                                                val pi = BigDecimal(PI, MathContext.DECIMAL128)
                                                val res = pi.multiply(radius.pow(2)).multiply(height)
                                                volume = res.setScale(2, RoundingMode.HALF_UP).toDouble()
                                            } catch (ex: Exception) {
                                                volume = 000.000
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        "Invalid Input !, Error Code :- ${ex.message}",
                                                        withDismissAction = true,
                                                        duration = SnackbarDuration.Long
                                                    )
                                                }
                                                Log.e(
                                                    "DEV LOG",
                                                    "Invalid Input !, Error Code :- ${ex.message} , Full Error Is In Down \n|\n|\n|"
                                                )
                                                Log.e("DEV LOG", "Full Error :- $ex")
                                            }

                                        }

                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Get Volume Of Cylinder",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = hegText,
                                                onValueChange = { hegText = it },
                                                label = { Text("Height") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = rdsText,
                                                onValueChange = { rdsText = it },
                                                label = { Text("Radius") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Button(
                                                onClick = {
                                                    //todo : main area function
                                                    calcVol()
                                                    //todo vibrate
                                                    if (isVibrt) {
                                                        if (Build.VERSION.SDK_INT <= 26) {
                                                            vibrator.vibrate(100)
                                                        } else {
                                                            vibrator.vibrate(
                                                                VibrationEffect.createOneShot(
                                                                    100,
                                                                    VibrationEffect.DEFAULT_AMPLITUDE
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                            ) { Text("Calculate") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .shadow(
                                                        elevation = 1.dp,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Volume Of Cylinder = $volume³",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                else -> {
                                    Text(
                                        " ^ Please Select Calculator Type ^ ",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                }
                1 -> {
                    Surface {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                                .background(MaterialTheme.colorScheme.background),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(15.dp))
                            val convrtType = listOf(
                                " < < Select Converter Type > >",
                                "Measuring Unit Converter ex . [ cm -> inch | m -> km ]",
                                "Temperature Converter ex . [ C -> F | F -> C ]",
                                "Data Converter ex . [ KB -> MB | MB -> KB ]",
                                "Time Converter ex . [ Hour -> Minute | Minute -> Hour ]"
                            )

                            var isExpndConvType by remember { mutableStateOf(false) }
                            var selectedConvOptn by remember { mutableStateOf(convrtType[0]) }


                            Spacer(modifier = Modifier.height(10.dp))

                            ExposedDropdownMenuBox(
                                expanded = isExpndConvType,
                                onExpandedChange = { isExpndConvType = !isExpndConvType }
                            ) {
                                TextField(
                                    value = selectedConvOptn,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Converter Type") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = isExpndConvType
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .padding(horizontal = 6.dp)
                                        .fillMaxWidth(),
                                )

                                ExposedDropdownMenu(
                                    expanded = isExpndConvType,
                                    onDismissRequest = { isExpndConvType = false }
                                ) {
                                    convrtType.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                selectedConvOptn = selectionOption
                                                isExpndConvType = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            when (selectedConvOptn) {
                                "Measuring Unit Converter ex . [ cm -> inch | m -> km ]" -> {
                                    Card {
                                        var fromUnitText by remember { mutableStateOf("") }
                                        var fromUnit = fromUnitText.toDoubleOrNull() ?: 0.0
                                        var toUnitText by remember { mutableStateOf("") }
                                        var toUnit = toUnitText.toDoubleOrNull() ?: 0.0

                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Measuring Unit Converter",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = fromUnitText,
                                                onValueChange = { fromUnitText = it },
                                                label = { Text("Measuring Unit") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )

                                            val fromList = listOf("mm", "cm", "in", "ft","m", "km")
                                            val toList = listOf("mm", "cm", "in","ft","m", "km")
                                            var isFrom by remember { mutableStateOf(false) }
                                            var isTo by remember { mutableStateOf(false) }
                                            var fromListOptn by remember { mutableStateOf(fromList[0]) }
                                            var toListOptn by remember { mutableStateOf(toList[1]) }

                                            Spacer(modifier = Modifier.height(5.dp))
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                ExposedDropdownMenuBox(
                                                    expanded = isFrom,
                                                    onExpandedChange = { isFrom = !isFrom },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = fromListOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("From") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isFrom
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isFrom,
                                                        onDismissRequest = { isFrom = false }
                                                    ) {
                                                        fromList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    fromListOptn = selectionOption
                                                                    isFrom = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "-> To ->",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.CenterVertically)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                ExposedDropdownMenuBox(
                                                    expanded = isTo,
                                                    onExpandedChange = { isTo = !isTo },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = toListOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("To") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isTo
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isTo,
                                                        onDismissRequest = { isTo = false }
                                                    ) {
                                                        toList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    toListOptn = selectionOption
                                                                    isTo = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            fun getFullForm(isTo: Boolean?): String {
                                                if (isTo == true) {
                                                    return when (toListOptn) {
                                                        "mm" -> {
                                                            "Millimetre"
                                                        }

                                                        "cm" -> {
                                                            "Centimetre"
                                                        }

                                                        "in" -> {
                                                            "Inch"
                                                        }

                                                        "ft" -> {
                                                            "Feet"
                                                        }

                                                        "m" -> {
                                                            "Meter"
                                                        }

                                                        "km" -> {
                                                            "Kilometer"
                                                        }

                                                        else -> {
                                                            "????"
                                                        }
                                                    }
                                                } else {
                                                    return when (fromListOptn) {
                                                        "mm" -> {
                                                            "Millimetre"
                                                        }

                                                        "cm" -> {
                                                            "Centimetre"
                                                        }

                                                        "m" -> {
                                                            "Meter"
                                                        }

                                                        "km" -> {
                                                            "Kilometer"
                                                        }

                                                        else -> {
                                                            "????"
                                                        }
                                                    }
                                                }
                                            }

                                            Text(
                                                "${getFullForm(isTo = false)} -> To -> ${
                                                    getFullForm(
                                                        isTo = true
                                                    )
                                                }",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal
                                            )

                                            //here is main function for converting Units
                                            when (fromListOptn) {
                                                "mm" -> {
                                                    toUnit = when (toListOptn) {
                                                        "cm" -> fromUnit / 10
                                                        "in" -> fromUnit / 25.4
                                                        "ft" -> fromUnit / 304.8
                                                        "m" -> fromUnit / 1000
                                                        "km" -> fromUnit / 1_000_000
                                                        else -> fromUnit
                                                    }
                                                }

                                                "cm" -> {
                                                    toUnit = when (toListOptn) {
                                                        "mm" -> fromUnit * 10
                                                        "in" -> fromUnit / 2.54
                                                        "ft" -> fromUnit / 30.48
                                                        "m" -> fromUnit / 100
                                                        "km" -> fromUnit / 100_000
                                                        else -> fromUnit
                                                    }
                                                }

                                                "m" -> {
                                                    toUnit = when (toListOptn) {
                                                        "mm" -> fromUnit * 1000
                                                        "cm" -> fromUnit * 100
                                                        "in" -> fromUnit * 39.3701
                                                        "ft" -> fromUnit * 3.28084
                                                        "km" -> fromUnit / 1000
                                                        else -> fromUnit
                                                    }
                                                }

                                                "km" -> {
                                                    toUnit = when (toListOptn) {
                                                        "mm" -> fromUnit * 1_000_000
                                                        "cm" -> fromUnit * 100_000
                                                        "in" -> fromUnit * 39_370.1
                                                        "ft" -> fromUnit * 3_280.84
                                                        "m" -> fromUnit * 1000
                                                        else -> fromUnit
                                                    }
                                                }

                                                "in" -> {
                                                    toUnit = when (toListOptn) {
                                                        "mm" -> fromUnit * 25.4
                                                        "cm" -> fromUnit * 2.54
                                                        "ft" -> fromUnit / 12
                                                        "m" -> fromUnit / 39.3701
                                                        "km" -> fromUnit / 39_370.1
                                                        else -> fromUnit
                                                    }
                                                }

                                                "ft" -> {
                                                    toUnit = when (toListOptn) {
                                                        "mm" -> fromUnit * 304.8
                                                        "cm" -> fromUnit * 30.48
                                                        "in" -> fromUnit * 12
                                                        "m" -> fromUnit / 3.28084
                                                        "km" -> fromUnit / 3_280.84
                                                        else -> fromUnit
                                                    }
                                                }
                                            }


                                            Spacer(modifier = Modifier.height(5.dp))

                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .shadow(1.dp, RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                            ) {
                                                Text(
                                                    "Convert Results = $toUnit",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Temperature Converter ex . [ C -> F | F -> C ]" -> {
                                    Card {
                                        var tempText by remember { mutableStateOf("") }
                                        var temp = tempText.toDoubleOrNull() ?: 0.0
                                        var result by remember { mutableDoubleStateOf(0.0) }
                                        var oprt by remember { mutableStateOf("") }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Temperature Converter",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = tempText,
                                                onValueChange = { tempText = it },
                                                label = { Text("Temperature") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            var tempList = listOf("°C", "°F", "°R", "°N", "K")
                                            var isFrom by remember { mutableStateOf(false) }
                                            var isTo by remember { mutableStateOf(false) }
                                            var fromTempSelectedOptn by remember {
                                                mutableStateOf(
                                                    tempList[0]
                                                )
                                            }
                                            var toTempSelectedOptn by remember { mutableStateOf(tempList[1]) }

                                            fun getFullForm(isTo: Boolean): String {
                                                return if (isTo) {
                                                    when (toTempSelectedOptn) {
                                                        "°C" -> "Celsius"
                                                        "°F" -> "Fahrenheit"
                                                        "°R" -> "Rankine"
                                                        "°N" -> "Newton"
                                                        "°D" -> "Delisle"
                                                        "K" -> "Kelvin"
                                                        else -> "???"
                                                    }
                                                } else {
                                                    when (fromTempSelectedOptn) {
                                                        "°C" -> "Celsius"
                                                        "°F" -> "Fahrenheit"
                                                        "°R" -> "Rankine"
                                                        "°N" -> "Newton"
                                                        "°D" -> "Delisle"
                                                        "K" -> "Kelvin"
                                                        else -> "???"
                                                    }
                                                }
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                ExposedDropdownMenuBox(
                                                    expanded = isFrom,
                                                    onExpandedChange = { isFrom = !isFrom },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = fromTempSelectedOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("From") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isFrom
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isFrom,
                                                        onDismissRequest = { isFrom = false }
                                                    ) {
                                                        tempList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    fromTempSelectedOptn =
                                                                        selectionOption
                                                                    isFrom = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "-> To ->",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.CenterVertically)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                ExposedDropdownMenuBox(
                                                    expanded = isTo,
                                                    onExpandedChange = { isTo = !isTo },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = toTempSelectedOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("To") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isTo
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isTo,
                                                        onDismissRequest = { isTo = false }
                                                    ) {
                                                        tempList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    toTempSelectedOptn = selectionOption
                                                                    isTo = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                "${getFullForm(isTo = false)} -> To -> ${
                                                    getFullForm(
                                                        isTo = true
                                                    )
                                                }",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            //here's main function for converting temperature
                                            when (fromTempSelectedOptn) {
                                                "°C" -> {
                                                    when (toTempSelectedOptn) {
                                                        "°F" -> {
                                                            result = (temp * 9 / 5) + 32
                                                            oprt = "°F"
                                                        }

                                                        "K" -> {
                                                            result = temp + 273.15
                                                            oprt = "K"
                                                        }

                                                        "°R" -> {
                                                            result = (temp * 9 / 5) + 491.97
                                                            oprt = "°R"
                                                        }

                                                        "°N" -> {
                                                            result = temp + 0.33000
                                                            oprt = "°N"
                                                        }

                                                        else -> {
                                                            result = temp
                                                            oprt = "°C"
                                                        }
                                                    }
                                                }

                                                "°F" -> {
                                                    when (toTempSelectedOptn) {
                                                        "°C" -> {
                                                            result = (temp - 32) * 5 / 9
                                                            oprt = "°C"
                                                        }

                                                        "K" -> {
                                                            result = (temp - 32) * 5 / 9 + 273.15
                                                            oprt = "K"
                                                        }

                                                        "°R" -> {
                                                            result = temp + 459.67
                                                            oprt = "°R"
                                                        }

                                                        "°N" -> {
                                                            result = (temp - 32) * 11 / 60
                                                            oprt = "°N"
                                                        }

                                                        else -> {
                                                            result = temp
                                                            oprt = "°F"
                                                        }
                                                    }
                                                }

                                                "K" -> {
                                                    when (toTempSelectedOptn) {
                                                        "°C" -> {
                                                            result = temp - 273.15
                                                            oprt = "°C"
                                                        }

                                                        "°F" -> {
                                                            result = (temp - 273.15) * 9 / 5 + 32
                                                            oprt = "°F"
                                                        }

                                                        "°R" -> {
                                                            result = temp * 9 / 5
                                                            oprt = "°R"
                                                        }

                                                        "°N" -> {
                                                            result = (temp - 273.15) * 33 / 100
                                                            oprt = "°N"
                                                        }

                                                        else -> {
                                                            result = temp
                                                            oprt = "K"
                                                        }
                                                    }
                                                }

                                                "°N" -> {
                                                    when (toTempSelectedOptn) {
                                                        "°C" -> {
                                                            result = temp * 100 / 33
                                                            oprt = "°C"
                                                        }

                                                        "°F" -> {
                                                            result = temp * 60 / 11 + 32
                                                            oprt = "°F"
                                                        }

                                                        "K" -> {
                                                            result = temp * 100 / 33 + 273.15
                                                            oprt = "K"
                                                        }

                                                        "°R" -> {
                                                            result = temp * 66 / 11 + 491.67
                                                            oprt = "°R"
                                                        }

                                                        else -> {
                                                            result = temp
                                                            oprt = "°N"
                                                        }
                                                    }
                                                }

                                                "°R" -> {
                                                    when (toTempSelectedOptn) {
                                                        "K" -> {
                                                            result = temp * 5 / 9
                                                            oprt = "K"
                                                        }

                                                        "°C" -> {
                                                            result = (temp - 491.67) * 5 / 9
                                                            oprt = "°C"
                                                        }

                                                        "°F" -> {
                                                            result = temp - 459.67
                                                            oprt = "°F"
                                                        }

                                                        "°N" -> {
                                                            result = (temp - 491.67) * 11 / 60
                                                            oprt = "°N"
                                                        }

                                                        else -> {
                                                            result = temp
                                                            oprt = "°R"
                                                        }
                                                    }
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .shadow(1.dp, RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                            ) {
                                                Text(
                                                    "${getFullForm(true)} = $result $oprt",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                        }
                                    }
                                }

                                "Data Converter ex . [ KB -> MB | MB -> KB ]" -> {
                                    Card {
                                        var dataText by remember { mutableStateOf("") }
                                        var data = dataText.toDoubleOrNull() ?: 0.0
                                        var result by remember { mutableDoubleStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Data Converter",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = dataText,
                                                onValueChange = { dataText = it },
                                                label = { Text("Data") },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            var dataList = listOf(
                                                "Bytes",
                                                "KB",
                                                "MB",
                                                "GB",
                                                "TB",
                                                "PB",
                                                "EB",
                                                "ZB",
                                                "YB"
                                            )
                                            var isFrom by remember { mutableStateOf(false) }
                                            var isTo by remember { mutableStateOf(false) }
                                            var fromDataSelectedOptn by remember {
                                                mutableStateOf(
                                                    dataList[2]
                                                )
                                            }
                                            var isBinarySystem by remember { mutableStateOf(true) }
                                            var isDecimalSystem by remember { mutableStateOf(false) }
                                            var toDataSelectedOptn by remember { mutableStateOf(dataList[3]) }
                                            fun getFullForm(isTo: Boolean): String {
                                                return if (isTo) {
                                                    when (toDataSelectedOptn) {
                                                        "Bytes" -> "Bytes"
                                                        "KB" -> "Kilobyte"
                                                        "MB" -> "Megabytes"
                                                        "GB" -> "Gigabytes"
                                                        "TB" -> "Terabytes"
                                                        "PB" -> "Petabytes"
                                                        "EB" -> "Exabytes"
                                                        "ZB" -> "Zettabytes"
                                                        "YB" -> "Yottabytes"
                                                        else -> {
                                                            "???"
                                                        }
                                                    }
                                                } else {
                                                    when (fromDataSelectedOptn) {
                                                        "Bit" -> "Bit"
                                                        "Bytes" -> "Bytes"
                                                        "KB" -> "Kilobyte"
                                                        "MB" -> "Megabytes"
                                                        "GB" -> "Gigabytes"
                                                        "TB" -> "Terabytes"
                                                        "PB" -> "Petabytes"
                                                        "EB" -> "Exabytes"
                                                        "ZB" -> "Zettabytes"
                                                        "YB" -> "Yottabytes"
                                                        else -> {
                                                            "???"
                                                        }
                                                    }
                                                }
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(
                                                        horizontal = 4.dp,
                                                        vertical = 2.dp
                                                    )
                                                    .fillMaxWidth()
                                            ) {
                                                ExposedDropdownMenuBox(
                                                    expanded = isFrom,
                                                    onExpandedChange = { isFrom = !isFrom },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = fromDataSelectedOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("From") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isFrom
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isFrom,
                                                        onDismissRequest = { isFrom = false }
                                                    ) {
                                                        dataList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    fromDataSelectedOptn =
                                                                        selectionOption
                                                                    isFrom = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "-> To ->",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.CenterVertically)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                ExposedDropdownMenuBox(
                                                    expanded = isTo,
                                                    onExpandedChange = { isTo = !isTo },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = toDataSelectedOptn,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("To") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isTo
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isTo,
                                                        onDismissRequest = { isTo = false }
                                                    ) {
                                                        dataList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    toDataSelectedOptn = selectionOption
                                                                    isTo = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                "${getFullForm(false)} -> To -> ${getFullForm(true)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            var isSlctSys by remember { mutableStateOf(false) }
                                            var isbynr by remember { mutableStateOf(false) }
                                            var isdsml by remember { mutableStateOf(true) }
                                            fun getDataSystem(): String {
                                                return if (isBinarySystem) {
                                                    "Binary"
                                                } else if (isDecimalSystem) {
                                                    "Decimal"
                                                } else {
                                                    "???"
                                                }
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .shadow(1.dp, RoundedCornerShape(3.dp))
                                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    "Select Data System [ ${getDataSystem()} ]",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                if (isSlctSys) {
                                                    Dialog(onDismissRequest = { isSlctSys = false }) {
                                                        Card {
                                                            var isInfo by remember {
                                                                mutableStateOf(
                                                                    false
                                                                )
                                                            }
                                                            Column(
                                                                modifier = Modifier
                                                                    .padding(5.dp)
                                                                    .wrapContentSize()
                                                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Spacer(modifier = Modifier.height(5.dp))
                                                                Text(
                                                                    "Select System",
                                                                    fontSize = 20.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                                Spacer(modifier = Modifier.height(5.dp))
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth() // Optional, for full width click area
                                                                        .clickable(onClick = {
                                                                            isdsml = true
                                                                            isbynr = false
                                                                        })
                                                                        .padding(16.dp) // Optional for spacing
                                                                ) {
                                                                    RadioButton(
                                                                        selected = isdsml,
                                                                        onClick = null // Prevents double-calling onClick
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text(text = "Decimal")
                                                                }
                                                                Spacer(modifier = Modifier.height(5.dp))
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth() // Optional, for full width click area
                                                                        .clickable(onClick = {
                                                                            isbynr = true
                                                                            isdsml = false
                                                                        })
                                                                        .padding(16.dp) // Optional for spacing
                                                                ) {
                                                                    RadioButton(
                                                                        selected = isbynr,
                                                                        onClick = null // Prevents double-calling onClick
                                                                    )
                                                                    Spacer(modifier = Modifier.width(8.dp))
                                                                    Text(text = "Binary")
                                                                }
                                                                Spacer(modifier = Modifier.height(5.dp))
                                                                Row(
                                                                    modifier = Modifier
                                                                        .padding(
                                                                            horizontal = 8.dp
                                                                        )
                                                                        .fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.End
                                                                ) {
                                                                    Spacer(modifier = Modifier.width(2.dp))
                                                                    TextButton(
                                                                        onClick = {
                                                                            isSlctSys = false
                                                                        }
                                                                    ) { Text("Cancel") }
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    IconButton(
                                                                        onClick = { isInfo = !isInfo }
                                                                    ) {
                                                                        Icon(
                                                                            painter = painterResource(id = R.drawable.baseline_help_outline_24),
                                                                            contentDescription = "Help / What ?"
                                                                        )
                                                                    }
                                                                    Spacer(
                                                                        modifier = Modifier
                                                                            .width(5.dp)
                                                                    )
                                                                    Button(
                                                                        onClick = {
                                                                            if (isbynr) {
                                                                                isBinarySystem = true
                                                                                isDecimalSystem = false
                                                                            } else if (isdsml) {
                                                                                isBinarySystem = false
                                                                                isDecimalSystem = true
                                                                            } else {
                                                                                //todo : nothing
                                                                            }
                                                                            isSlctSys = false
                                                                        }
                                                                    ) { Text("Done") }
                                                                    DropdownMenu(
                                                                        expanded = isInfo,
                                                                        onDismissRequest = {
                                                                            isInfo = false
                                                                        }
                                                                    ) {
                                                                        Card {
                                                                            Column(
                                                                                modifier = Modifier
                                                                                    .padding(
                                                                                        5.dp
                                                                                    )
                                                                                    .wrapContentSize(),
                                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                                            ) {
                                                                                Text("In The Decimal System 1MB = 1000 KB , GB = 1000MB, TB = 1000GB , PB = 1000TB ...")
                                                                                Spacer(
                                                                                    modifier = Modifier.height(
                                                                                        5.dp
                                                                                    )
                                                                                )
                                                                                Text("In The Binary System 1MB = 1024 KB , GB = 1024MB, TB = 1024GB , PB = 1024TB ...")
                                                                                Spacer(
                                                                                    modifier = Modifier.height(
                                                                                        5.dp
                                                                                    )
                                                                                )
                                                                                Text("The Decimal System Used By Disk manufacturers \nThe Binary System Used By Computer / Phone / OS ( Operating System )")
                                                                                Spacer(
                                                                                    modifier = Modifier.height(
                                                                                        5.dp
                                                                                    )
                                                                                )
                                                                                val intent = remember {
                                                                                    simple_browser.createIntent(context, "https://github.com/asciiblues/Data-Decimal-System-vs-Binary-/blob/main/README.md")
                                                                                }
                                                                                ElevatedButton(
                                                                                    onClick = {
                                                                                        context.startActivity(
                                                                                            intent
                                                                                        )
                                                                                    },
                                                                                ) { Text("Learn More") }
                                                                                Spacer(
                                                                                    modifier = Modifier.height(
                                                                                        5.dp
                                                                                    )
                                                                                )
                                                                                DropdownMenuItem(
                                                                                    text = { Text("Close") },
                                                                                    onClick = {
                                                                                        isInfo = false
                                                                                    }
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            //main function for conversion
                                            when (fromDataSelectedOptn) {
                                                "Bytes" -> {
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "KB" -> {
                                                                result = data / 1024
                                                            }

                                                            "MB" -> {
                                                                result = data / 1024 / 1024
                                                            }

                                                            "GB" -> {
                                                                result = data / 1024 / 1024 / 1024
                                                            }

                                                            "TB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "PB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "KB" -> {
                                                                result = data / 1000
                                                            }

                                                            "MB" -> {
                                                                result = data / 1000 / 1000
                                                            }

                                                            "GB" -> {
                                                                result = data / 1000 / 1000 / 1000
                                                            }

                                                            "TB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "PB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                }

                                                "KB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1000
                                                            }

                                                            "MB" -> {
                                                                result = data / 1000
                                                            }

                                                            "GB" -> {
                                                                result = data / 1000 / 1000
                                                            }

                                                            "TB" -> {
                                                                result = data / 1000 / 1000 / 1000
                                                            }

                                                            "PB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1024
                                                            }

                                                            "MB" -> {
                                                                result = data / 1024
                                                            }

                                                            "GB" -> {
                                                                result = data / 1024 / 1024
                                                            }

                                                            "TB" -> {
                                                                result = data / 1024 / 1024 / 1024
                                                            }

                                                            "PB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                }

                                                "MB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result = data * 1000
                                                            }

                                                            "GB" -> {
                                                                result = data / 1000
                                                            }

                                                            "TB" -> {
                                                                result = data / 1000 / 1000
                                                            }

                                                            "PB" -> {
                                                                result = data / 1000 / 1000 / 1000
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result = data * 1024
                                                            }

                                                            "GB" -> {
                                                                result = data / 1024
                                                            }

                                                            "TB" -> {
                                                                result = data / 1024 / 1024
                                                            }

                                                            "PB" -> {
                                                                result = data / 1024 / 1024 / 1024
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                }

                                                "GB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result = data * 1000
                                                            }

                                                            "TB" -> {
                                                                result = data / 1000
                                                            }

                                                            "PB" -> {
                                                                result = data / 1000 / 1000
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result = data * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result = data * 1024
                                                            }

                                                            "TB" -> {
                                                                result = data / 1024
                                                            }

                                                            "PB" -> {
                                                                result = data / 1024 / 1024
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                }

                                                "TB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "GB" -> {
                                                                result = data * 1000
                                                            }

                                                            "PB" -> {
                                                                result = data / 1000
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1000 / 1000
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1000 / 1000 / 1000 / 1000
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "GB" -> {
                                                                result = data * 1024
                                                            }

                                                            "PB" -> {
                                                                result = data / 1024
                                                            }

                                                            "EB" -> {
                                                                result =
                                                                    data / 1024 / 1024
                                                            }

                                                            "ZB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024
                                                            }

                                                            "YB" -> {
                                                                result =
                                                                    data / 1024 / 1024 / 1024 / 1024
                                                            }

                                                            else -> {
                                                                result = data
                                                            }
                                                        }
                                                    }
                                                }

                                                "PB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result = data * 1000 * 1000 * 1000
                                                            }

                                                            "GB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "TB" -> {
                                                                result = data * 1000
                                                            }

                                                            "EB" -> {
                                                                result = data / 1000
                                                            }

                                                            "ZB" -> {
                                                                result = data / 1000 / 1000
                                                            }

                                                            "YB" -> {
                                                                result = data / 1000 / 1000 / 1000
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result = data * 1024 * 1024 * 1024
                                                            }

                                                            "GB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "TB" -> {
                                                                result = data * 1024
                                                            }

                                                            "EB" -> {
                                                                result = data / 1024
                                                            }

                                                            "ZB" -> {
                                                                result = data / 1024 / 1024
                                                            }

                                                            "YB" -> {
                                                                result = data / 1024 / 1024 / 1024
                                                            }
                                                        }
                                                    }
                                                }

                                                "EB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "GB" -> {
                                                                result = data * 1000 * 1000 * 1000
                                                            }

                                                            "TB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "PB" -> {
                                                                result = data * 1000
                                                            }

                                                            "ZB" -> {
                                                                result = data / 1000
                                                            }

                                                            "YB" -> {
                                                                result = data / 1000 / 1000
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "GB" -> {
                                                                result = data * 1024 * 1024 * 1024
                                                            }

                                                            "TB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "PB" -> {
                                                                result = data * 1024
                                                            }

                                                            "ZB" -> {
                                                                result = data / 1024
                                                            }

                                                            "YB" -> {
                                                                result = data / 1024 / 1024
                                                            }
                                                        }
                                                    }
                                                }

                                                "ZB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "GB" -> {
                                                                result = data * 1000 * 1000 * 1000
                                                            }

                                                            "TB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "PB" -> {
                                                                result = data * 1000
                                                            }

                                                            "YB" -> {
                                                                result = data / 1000
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "GB" -> {
                                                                result = data * 1024 * 1024 * 1024
                                                            }

                                                            "TB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "PB" -> {
                                                                result = data * 1024
                                                            }

                                                            "YB" -> {
                                                                result = data / 1024
                                                            }
                                                        }
                                                    }
                                                }

                                                "YB" -> {
                                                    if (isDecimalSystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "GB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "TB" -> {
                                                                result =
                                                                    data * 1000 * 1000 * 1000 * 1000
                                                            }

                                                            "PB" -> {
                                                                result = data * 1000 * 1000 * 1000
                                                            }

                                                            "EB" -> {
                                                                result = data * 1000 * 1000
                                                            }

                                                            "ZB" -> {
                                                                result = data * 1000
                                                            }
                                                        }
                                                    }
                                                    if (isBinarySystem) {
                                                        when (toDataSelectedOptn) {
                                                            "Bytes" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "KB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "MB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "GB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "TB" -> {
                                                                result =
                                                                    data * 1024 * 1024 * 1024 * 1024
                                                            }

                                                            "PB" -> {
                                                                result = data * 1024 * 1024 * 1024
                                                            }

                                                            "EB" -> {
                                                                result = data * 1024 * 1024
                                                            }

                                                            "ZB" -> {
                                                                result = data * 1024
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    isSlctSys = true
                                                    if (isBinarySystem) {
                                                        isbynr = true
                                                        isdsml = false
                                                    }
                                                    if (isDecimalSystem) {
                                                        isbynr = false
                                                        isdsml = true
                                                    }
                                                }
                                            ) { Text("Select Data System ...") }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .shadow(1.dp, RoundedCornerShape(10.dp))
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                                            ) {
                                                Text(
                                                    "Results = $result",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            }
                                        }

                                    }
                                }

                                "Time Converter ex . [ Hour -> Minute | Minute -> Hour ]" -> {
                                    Card {
                                        var timetext by remember { mutableStateOf("") }
                                        var time = timetext.toDoubleOrNull() ?: 0.0
                                        var result by remember { mutableStateOf(0.0) }
                                        Column(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                "Time Converter",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            HorizontalDivider()
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = timetext,
                                                onValueChange = { timetext = it },
                                                label = { Text("Enter Time") },
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .fillMaxWidth(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            var timeList = listOf(
                                                "Millisecond",
                                                "Second",
                                                "Minute",
                                                "Hour",
                                                "Day",
                                                "Week",
                                                "Month",
                                                "Year"
                                            )

                                            val SECOND = 1000.0
                                            val MINUTE = SECOND * 60
                                            val HOUR = MINUTE * 60
                                            val DAY = HOUR * 24
                                            val WEEK = DAY * 7
                                            val MONTH = DAY * 30     // Approximate
                                            val YEAR = DAY * 365     // Approximate

                                            var fromTimeSelected by remember { mutableStateOf(timeList[1]) }
                                            var toTimeSelected by remember { mutableStateOf(timeList[2]) }
                                            var isTo by remember { mutableStateOf(false) }
                                            var isFrom by remember { mutableStateOf(false) }

                                            Row(
                                                modifier = Modifier
                                                    .padding(
                                                        horizontal = 4.dp,
                                                        vertical = 2.dp
                                                    )
                                                    .fillMaxWidth()
                                            ) {
                                                ExposedDropdownMenuBox(
                                                    expanded = isFrom,
                                                    onExpandedChange = { isFrom = !isFrom },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = fromTimeSelected,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("From") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isFrom
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isFrom,
                                                        onDismissRequest = { isFrom = false }
                                                    ) {
                                                        timeList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    fromTimeSelected =
                                                                        selectionOption
                                                                    isFrom = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "-> To ->",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.CenterVertically)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                ExposedDropdownMenuBox(
                                                    expanded = isTo,
                                                    onExpandedChange = { isTo = !isTo },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(5.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = toTimeSelected,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("To") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = isTo
                                                            )
                                                        },
                                                        modifier = Modifier.menuAnchor()
                                                    )

                                                    ExposedDropdownMenu(
                                                        expanded = isTo,
                                                        onDismissRequest = { isTo = false }
                                                    ) {
                                                        timeList.forEach { selectionOption ->
                                                            DropdownMenuItem(
                                                                text = { Text(selectionOption) },
                                                                onClick = {
                                                                    toTimeSelected = selectionOption
                                                                    isTo = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text("$fromTimeSelected -> to ->  $toTimeSelected",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            //here is main function for calculation of time
                                            when (fromTimeSelected) {
                                                "Millisecond" -> {
                                                    result = when (toTimeSelected) {
                                                        "Second" -> time / SECOND
                                                        "Minute" -> time / MINUTE
                                                        "Hour" -> time / HOUR
                                                        "Day" -> time / DAY
                                                        "Week" -> time / WEEK
                                                        "Month" -> time / MONTH
                                                        "Year" -> time / YEAR
                                                        else -> time
                                                    }
                                                }
                                                "Second" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * SECOND
                                                        "Minute" -> time / 60
                                                        "Hour" -> time / 3600
                                                        "Day" -> time / 86400
                                                        "Week" -> time / 604800
                                                        "Month" -> time / (30 * 86400)
                                                        "Year" -> time / (365 * 86400)
                                                        else -> time
                                                    }
                                                }
                                                "Minute" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * MINUTE
                                                        "Second" -> time * 60
                                                        "Hour" -> time / 60
                                                        "Day" -> time / 1440
                                                        "Week" -> time / 10080
                                                        "Month" -> time / (30 * 1440 / 60)
                                                        "Year" -> time / (365 * 1440 / 60)
                                                        else -> time
                                                    }
                                                }
                                                "Hour" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * HOUR
                                                        "Second" -> time * 3600
                                                        "Minute" -> time * 60
                                                        "Day" -> time / 24
                                                        "Week" -> time / 168
                                                        "Month" -> time / (30 * 24)
                                                        "Year" -> time / (365 * 24)
                                                        else -> time
                                                    }
                                                }
                                                "Day" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * DAY
                                                        "Second" -> time * 86400
                                                        "Minute" -> time * 1440
                                                        "Hour" -> time * 24
                                                        "Week" -> time / 7
                                                        "Month" -> time / 30
                                                        "Year" -> time / 365
                                                        else -> time
                                                    }
                                                }
                                                "Week" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * WEEK
                                                        "Second" -> time * 604800
                                                        "Minute" -> time * 10080
                                                        "Hour" -> time * 168
                                                        "Day" -> time * 7
                                                        "Month" -> time / (30.0 / 7.0)
                                                        "Year" -> time / 52.1429
                                                        else -> time
                                                    }
                                                }
                                                "Month" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * MONTH
                                                        "Second" -> time * 2_592_000
                                                        "Minute" -> time * 43_200
                                                        "Hour" -> time * 720
                                                        "Day" -> time * 30
                                                        "Week" -> time * (30.0 / 7.0)
                                                        "Year" -> time / 12
                                                        else -> time
                                                    }
                                                }
                                                "Year" -> {
                                                    result = when (toTimeSelected) {
                                                        "Millisecond" -> time * YEAR
                                                        "Second" -> time * 31_536_000
                                                        "Minute" -> time * 525_600
                                                        "Hour" -> time * 8760
                                                        "Day" -> time * 365
                                                        "Week" -> time * 52.1429
                                                        "Month" -> time * 12
                                                        else -> time
                                                    }
                                                }
                                            }
                                            Box (modifier = Modifier.padding(horizontal = 8.dp).shadow(1.dp, RoundedCornerShape(10.dp)).fillMaxWidth().height(50.dp).background(MaterialTheme.colorScheme.onSecondaryContainer)) {
                                                Text(
                                                    "Results = $result",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = MaterialTheme.colorScheme.secondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
                2 -> {
                    var isLocalVibrt by remember { mutableStateOf(true) }
                    LaunchedEffect(Unit) {
                        val storedValue = readSettingFile(vibrtFileName, context)
                        isLocalVibrt = storedValue == "1"
                        Log.d("DEV LOG", "Initial value read [0] : $storedValue")
                    }
                    Surface {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                                .background(MaterialTheme.colorScheme.background),
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            Text(
                                "Settings",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            SettingsGroup(title = { Text("Vibrate") }) {
                                SettingsSwitch(
                                    state = isLocalVibrt,
                                    title = { Text("Vibrate") },
                                    subtitle = { Text("Vibrate on Calculate Button Click") },
                                    onCheckedChange = { newState: Boolean ->
                                        isLocalVibrt = newState
                                        scope.launch {
                                            writeSettingFile(
                                                context = context,
                                                name = vibrtFileName,
                                                value = if (newState) "1" else "0"
                                            )
                                            Log.d("DEV LOG", "Vibrate Value : $newState")
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .fillMaxWidth()
                                )
                            }
                            var isthirdlibs by remember { mutableStateOf(false) }
                            SettingsGroup (title = { Text("About and Info") }) {
                                Column (modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().wrapContentHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Qualculator",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        "Version = 1.0",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))

                                    fun getShowOrHide(): String {
                                        return if (isthirdlibs) "Hide Third Party Libraries" else "Show Third Party Libraries"
                                    }
                                    Button(onClick = { isthirdlibs = !isthirdlibs }) {
                                        Text(getShowOrHide())
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                                val libs = listOf(
                                    "accompanist-systemuicontroller", "Compose-Settings"
                                )
                                val libsLink = listOf (
                                    "https://github.com/google/accompanist",
                                    "https://github.com/alorma/Compose-Settings"
                                )
                                AnimatedVisibility(visible = isthirdlibs) {
                                        LazyColumn {
                                            itemsIndexed(libs) { index, libs ->
                                                Box (modifier = Modifier.padding(2.dp).shadow(1.dp, RoundedCornerShape(5.dp)).background(
                                                    MaterialTheme.colorScheme.primaryContainer).fillMaxWidth().wrapContentHeight()) {
                                                    Text(libs, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(
                                                        Alignment.CenterStart))
                                                    Button(
                                                        onClick = {
                                                            val url = libsLink[index].toString()
                                                            val i = simple_browser.createIntent(context, url)
                                                            context.startActivity(i)
                                                        },
                                                        modifier = Modifier.align(Alignment.CenterEnd)
                                                    ) { Text("Open Web") }
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun previewMapp(modifier: Modifier = Modifier) {
        mapp() //default values
    }
}
