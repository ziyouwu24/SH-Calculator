package com.example.shcalculator

import android.nfc.cardemulation.OffHostApduService
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shcalculator.ui.theme.SHCalculatorTheme
import kotlinx.coroutines.launch
import java.security.KeyStore
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class EyeThrow(
    val x: Double,
    val z: Double,
    val angle: Double,
)

data class StrongholdPrediction(
    val x: Double,
    val z: Double,
)

fun getIntersection(
    e1: EyeThrow,
    e2: EyeThrow
): StrongholdPrediction {
    if (e1.angle == e2.angle) {
        return StrongholdPrediction(0.0, 0.0) //return nil?
    }

    val tanA1 = tan(e1.angle * PI / 180)
    val tanA2 = tan(e2.angle * PI / 180)
    val cotA1 = 1/tanA1
    val cotA2 = 1/tanA2

    val x_f = (-e2.z+e1.z+cotA1*e1.x-cotA2*e2.x)/(cotA1-cotA2)
    val z_f = (-e2.x+e1.x+tanA1*e1.z-tanA2*e2.z)/(tanA1-tanA2)

    return StrongholdPrediction(
        x_f,
        z_f
    )
}

fun worldToCanvas(
    x: Float,
    z: Float,
    center: Offset,
    pixelsPerBlock: Float,
): Offset {
    return Offset(
        x = center.x + (x * pixelsPerBlock),
        y = center.y + (z * pixelsPerBlock)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHCalculatorTheme {
                var showBottomSheet by remember{mutableStateOf(false)}
                var eyeThrows by remember{mutableStateOf(emptyList<EyeThrow>())}
                var prediction by remember{mutableStateOf(StrongholdPrediction(0.0,0.0))}
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

                            if (eyeThrows.size >= 2) {
                                prediction = getIntersection(eyeThrows[0], eyeThrows[1])
                                upperText = "${prediction.x.toInt()}, ${prediction.z.toInt()}"
                            }

                            if (!eyeThrows.isEmpty()) {
                                LazyColumn {
                                    items(eyeThrows) { currentEye ->
                                        CoordinateItem(currentEye)
                                    }
                                }

                                HorizontalDivider()
                            }

                            Text (text = upperText)

                            Spacer(modifier = Modifier.height(16.dp))

                            //StrongholdMap(eyeThrows, prediction)
                        }
                    }

                    if (showBottomSheet) {
                        AddEyeModal(
                            onDismiss = {
                                showBottomSheet = false
                            },

                            onClick = {
                                eyeThrows = eyeThrows + listOf(it)
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
fun AddEyeModal(onDismiss: () -> Unit, onClick: (EyeThrow) -> Unit) {
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
                AddEyeField(
                    value = x,
                    onValueChange = { x = it },
                    modifier = Modifier.weight(1f),
                    label = "X"
                )
                Spacer(Modifier.width(16.dp))
                AddEyeField(
                    value = z,
                    onValueChange = { z = it },
                    modifier = Modifier.weight(1f),
                    label = "Z"
                )
                Spacer(Modifier.width(16.dp))
                AddEyeField(
                    value = angle,
                    onValueChange = { angle = it },
                    modifier = Modifier.weight(1f),
                    label = "Angle"
                )
                Spacer(Modifier.width(16.dp))
                Button(
                    modifier = Modifier.fillMaxHeight(),
                    onClick = {
                        val result = EyeThrow(x.toDouble(), z.toDouble(), angle.toDouble())
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
fun StrongholdMap(
    throws: List<EyeThrow>,
    prediction: StrongholdPrediction,
    modifier: Modifier = Modifier
) {
    var scale by remember{ mutableFloatStateOf(1f) }
    var offset by remember{mutableStateOf(Offset.Zero)}

    Canvas(
        modifier = modifier
            .size(300.dp, 300.dp)
            .pointerInput(Unit) {
                detectTransformGestures{ centroid, pan, zoom, _ ->
                    val oldScale = scale
                    val newScale = (scale * zoom)
                        .coerceIn(0.2f, 5f)

                    offset = centroid + (offset - centroid) * (newScale / oldScale) + pan
                    scale = newScale
                }
            }
            .clipToBounds()
    ) {
        val gridSpacing = when {
            scale > 2f -> 8f
            scale > .5f -> 16f
            else -> 32f
        }

        val pixelsPerBlock = 2f * scale
        val gridPixelSpacing = gridSpacing * pixelsPerBlock

        val center = Offset(
            size.width / 2f + offset.x,
            size.height / 2f + offset.y
        )

        val startX = ((-center.x) / gridPixelSpacing).toInt()
        val endX = ((size.width - center.x) / gridPixelSpacing).toInt()

        val startY = ((-center.y) / gridPixelSpacing).toInt()
        val endY = ((size.height - center.y) / gridPixelSpacing).toInt()

        for(i in startX..endX) {
            val worldX = i * gridSpacing
            val screenX = center.x + (worldX * pixelsPerBlock)

            drawLine(
                color = Color(0xFF333333),
                start = Offset(screenX, 0f),
                end = Offset(screenX, size.height),
                strokeWidth = 2f
            )
        }

        for(i in startY..endY) {
            val worldY = i * gridSpacing
            val screenY = center.y + (worldY * pixelsPerBlock)

            drawLine(
                color = Color(0xFF333333),
                start = Offset(0f, screenY),
                end = Offset(size.width, screenY),
                strokeWidth = 2f
            )
        }

        drawLine(
            color = Color(0xFF333333),
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 4f
        )

        drawLine(
            color =Color(0xFF333333),
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 4f
        )

        for (eyeThrow in throws) {
            val origin = worldToCanvas(eyeThrow.x.toFloat(), eyeThrow.z.toFloat(), center, pixelsPerBlock)
            val rad = ((eyeThrow.angle + 270) * PI / 180).toFloat()
            val endWorld = Offset(eyeThrow.x.toFloat(), eyeThrow.z.toFloat()) + Offset(-cos(rad), -sin(rad)) * 1000f
            val end = worldToCanvas(endWorld.x, endWorld.y, center, pixelsPerBlock)

            drawCircle(
                color = Color.Blue,
                radius = 8f,
                center = origin
            )

            drawLine(
                color = Color.Blue,
                start = origin,
                end = end,
                strokeWidth = 4f
            )
        }

        val predictionOrigin = worldToCanvas(prediction.x.toFloat(), prediction.z.toFloat(), center, pixelsPerBlock)

        drawCircle(
            color = Color.Green,
            radius = 12f,
            center = predictionOrigin
        )
    }
}

@Composable
fun AddEyeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    label: String = "Number"
) {
    var hasBeenFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
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
fun CoordinateItem(eyeThrow: EyeThrow) {
    Text(
        text = "${eyeThrow.x} ${eyeThrow.z} ${eyeThrow.angle}"
    )
}