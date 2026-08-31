package com.example.shcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shcalculator.ui.theme.SHCalculatorTheme
import kotlinx.coroutines.launch
import java.security.KeyStore

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHCalculatorTheme {
                var showBottomSheet by remember{mutableStateOf(false)}
                var coordinates by remember{mutableStateOf<List<List<Float>>>(emptyList())}
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Calculate Stronghold")
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {

                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                ) { innerPadding ->

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                    ) {
                        Column(modifier = Modifier
                            .padding(16.dp)
                        ) {
                            var upperText = "Need at least 2 eye measures to calculate"

                            if (coordinates.size >= 2) {
                                upperText = "idkWhere bruh"
                            }

                            if (!coordinates.isEmpty()) {
                                LazyColumn {
                                    items(coordinates) { currentCoord ->
                                        coordinateItem(currentCoord)
                                    }
                                }

                                HorizontalDivider()
                            }

                            Text (text = upperText)
                        }
                    }

                    if (showBottomSheet) {
                        addEyeModal(
                            onDismiss = {
                                showBottomSheet = false
                            },

                            onClick = {
                                coordinates = coordinates + listOf(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addEyeModal(onDismiss: () -> Unit, onClick: (List<Float>) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var x by remember {mutableStateOf("0")}
    var z by remember {mutableStateOf("0")}
    var angle by remember {mutableStateOf("0")}

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text="Add Eye Measurement",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                addEyeField(
                    value = x,
                    onValueChange = { x = it },
                    modifier = Modifier.weight(1f),
                    label = "X"
                )
                Spacer(Modifier.width(16.dp))
                addEyeField(
                    value = z,
                    onValueChange = { z = it },
                    modifier = Modifier.weight(1f),
                    label = "Z"
                )
                Spacer(Modifier.width(16.dp))
                addEyeField(
                    value = angle,
                    onValueChange = { angle = it },
                    modifier = Modifier.weight(1f),
                    label = "Angle"
                )
                Spacer(Modifier.width(16.dp))
                Button(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = {
                        val result = listOf(x.toFloat(), z.toFloat(), angle.toFloat())
                        onClick(result)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun addEyeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String = "Number"
) {
    var hasBeenFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(newValue)
            }
        },
        label = {
            Text(label)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        singleLine = true,
        modifier = modifier
            .fillMaxHeight()
            .onFocusChanged {focusState ->
                if (focusState.isFocused && !hasBeenFocused) {
                    onValueChange("")
                    hasBeenFocused = true
                }
            }
    )
}

@Composable
fun coordinateItem(coordinate: List<Float>) {
    Text(
        text = coordinate.joinToString(" ")
    )
}