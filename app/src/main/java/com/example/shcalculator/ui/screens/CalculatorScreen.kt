package com.example.shcalculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shcalculator.R
import com.example.shcalculator.data.EyeThrow
import com.example.shcalculator.data.StrongholdPrediction
import com.example.shcalculator.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val defaultPadding = 16.dp
private val defaultElevation = 4.dp
private val defaultBorderWidth = (1.5).dp
private val defaultCornerShape = 14.dp

//fun getIntersection(
//    e1: EyeThrow,
//    e2: EyeThrow
//): StrongholdPrediction {
//    if (e1.angle == e2.angle) {
//        return StrongholdPrediction(Double.NaN, Double.NaN)
//    }
//
//    val tanA1 = tan(e1.angle * PI / 180)
//    val tanA2 = tan(e2.angle * PI / 180)
//    val cotA1 = 1/tanA1
//    val cotA2 = 1/tanA2
//
//    val xFinal = (-e2.z+e1.z+cotA1*e1.x-cotA2*e2.x)/(cotA1-cotA2)
//    val zFinal = (-e2.x+e1.x+tanA1*e1.z-tanA2*e2.z)/(tanA1-tanA2)
//
//    return StrongholdPrediction(
//        xFinal,
//        zFinal
//    )
//}

fun getIntersection(
    e1: EyeThrow,
    e2: EyeThrow
): StrongholdPrediction {
    val rad1 = (e1.angle + 270) * PI / 180
    val rad2 = (e2.angle + 270) * PI / 180

    val d1x = -cos(rad1)
    val d1z = -sin(rad1)
    val d2x = -cos(rad2)
    val d2z = -sin(rad2)

    val denom = d1x * d2z - d1z * d2x
    if (abs(denom) < 1e-9) {
        return StrongholdPrediction(Double.NaN, Double.NaN)
    }

    val dx = e2.x - e1.x
    val dz = e2.z - e1.z

    val t1 = (dx * d2z - dz * d2x) / denom
    val t2 = (dx * d1z - dz * d1x) / denom

    if (t1 <= 0 || t2 <= 0) {
        return StrongholdPrediction(Double.NaN, Double.NaN)
    }

    val xFinal = e1.x + t1 * d1x
    val zFinal = e1.z + t1 * d1z

    return StrongholdPrediction(xFinal, zFinal)
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

fun DrawScope.centeredImage(
    image: ImageBitmap,
    center: Offset,
    size: IntSize
) {
    if (center.x.isNaN() || center.y.isNaN()) {
        return
    }

    val topLeft = center - Offset(
        size.width / 2f,
        size.height / 2f
    )

    drawImage(
        image = image,
        dstOffset = topLeft.round(),
        dstSize = size
    )
}

@Composable
fun CalculatorScreen(
    viewModel: SearchViewModel = viewModel()
) {
    var showBottomSheet by remember{mutableStateOf(false)}
    val scrollState = rememberScrollState()

    val eyeThrows = viewModel.eyeThrows
    val prediction = viewModel.prediction

    if (showBottomSheet) {
        EyeModal(
            onDismiss = { showBottomSheet = false },
            onClick = { viewModel.addEyeThrow(it) }
        )
    }

    if (eyeThrows.size >= 2) {
        viewModel.updatePrediction(getIntersection(eyeThrows[0], eyeThrows[1]))
    }else{
        viewModel.updatePrediction(StrongholdPrediction(Double.NaN, Double.NaN))
    }

    Column(
        Modifier
            .padding(defaultPadding)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard {
            PredictionBody(prediction, eyeThrows.size)
        }
        HorizontalSpacer()
        HorizontalCard {
            StrongholdMap(eyeThrows, prediction)
        }
        HorizontalSpacer()
        HorizontalCard {
            CoordinateBody(
                eyeThrows = eyeThrows,
                onDelete = { eyeThrow ->
                    viewModel.deleteEyeThrow(eyeThrow)
                }
            )
        }
        HorizontalSpacer()
        HorizontalButton(
            onClick = {
                showBottomSheet = true
            }
        ) {
            Text("Add eye throw")
        }
    }
}

@Composable
fun ElevatedCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(defaultCornerShape),
        elevation = CardDefaults.cardElevation(defaultElevation),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun HorizontalCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(defaultCornerShape),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = defaultBorderWidth,
                color = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(defaultCornerShape)
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun HorizontalButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(defaultCornerShape),
        elevation = ButtonDefaults.buttonElevation(defaultElevation),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun HorizontalSpacer() {
    Spacer(modifier = Modifier.height(defaultPadding))
}
@Composable
fun StrongholdMap(
    throws: List<EyeThrow>,
    prediction: StrongholdPrediction,
) {
    var scale by remember{ mutableFloatStateOf(1f) }
    var offset by remember{mutableStateOf(Offset.Zero)}

    val gridColor = MaterialTheme.colorScheme.onSecondary
    val eyeImage = ImageBitmap.imageResource(R.drawable.ender_eye)
    val portalImage = ImageBitmap.imageResource(R.drawable.end_portal)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val oldScale = scale
                    val newScale = (scale * zoom)
                        .coerceIn(0.2f, 5f)

                    offset = centroid + (offset - centroid) * (newScale / oldScale) + pan
                    scale = newScale
                }
            }
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
                color = gridColor,
                start = Offset(screenX, 0f),
                end = Offset(screenX, size.height),
                strokeWidth = 1.5f
            )
        }

        for(i in startY..endY) {
            val worldY = i * gridSpacing
            val screenY = center.y + (worldY * pixelsPerBlock)

            drawLine(
                color = gridColor,
                start = Offset(0f, screenY),
                end = Offset(size.width, screenY),
                strokeWidth = 1.5f
            )
        }

        drawLine(
            color = gridColor,
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 3f
        )

        drawLine(
            color = gridColor,
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 3f
        )

        for (eyeThrow in throws) {
            val origin = worldToCanvas(eyeThrow.x.toFloat(), eyeThrow.z.toFloat(), center, pixelsPerBlock)
            val rad = ((eyeThrow.angle + 270) * PI / 180).toFloat()
            val endWorld = Offset(eyeThrow.x.toFloat(), eyeThrow.z.toFloat()) + Offset(-cos(rad), -sin(rad)) * 10000f
            val end = worldToCanvas(endWorld.x, endWorld.y, center, pixelsPerBlock)

            drawLine(
                color = Color.Blue,
                start = origin,
                end = end,
                strokeWidth = 4f
            )

            centeredImage(eyeImage, origin, IntSize(60, 60))
        }

        val predictionOrigin = worldToCanvas(prediction.x.toFloat(), prediction.z.toFloat(), center, pixelsPerBlock)

        centeredImage(portalImage, predictionOrigin, IntSize(60, 60))
    }
}

@Composable
fun PredictionBody(prediction: StrongholdPrediction, numThrows: Int) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "PREDICTED STRONGHOLD",
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        fontWeight = FontWeight.Medium
    )
    if(numThrows >= 2) {
        if (prediction.x.isNaN() || prediction.z.isNaN()) {
            Text(
                text = "Invalid intersect",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            Text(
                text = "${prediction.x.toInt()}, ${prediction.z.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }else{
        Text(
            text = "Need 2+ throws",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun CoordinateBody(eyeThrows: List<EyeThrow>, onDelete: (EyeThrow) -> Unit) {
    Spacer(modifier = Modifier.height(12.dp))
    if(eyeThrows.isEmpty()){
        Text(
            text = "No throws yet",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Tap the add button to log an eye throw",
            fontSize = 13.sp,
        )
    }else{
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ) {
            items(eyeThrows) { currentEye ->
                CoordinateItem(
                    eyeThrow = currentEye,
                    onDelete = {
                        onDelete(currentEye)
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EyeModal(onDismiss: () -> Unit, onClick: (EyeThrow) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var x by remember { mutableStateOf("0") }
    var z by remember { mutableStateOf("0") }
    var angle by remember { mutableStateOf("0") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Add Eye Throw",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                EyeField(
                    value = x,
                    onValueChange = { x = it },
                    modifier = Modifier.weight(1f),
                    label = "X"
                )
                Spacer(Modifier.width(10.dp))
                EyeField(
                    value = z,
                    onValueChange = { z = it },
                    modifier = Modifier.weight(1f),
                    label = "Z"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            EyeField(
                value = angle,
                onValueChange = { angle = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Angle"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val result = EyeThrow(
                        x.toDoubleOrNull() ?: 0.0,
                        z.toDoubleOrNull() ?: 0.0,
                        angle.toDoubleOrNull() ?: 0.0
                    )
                    onClick(result)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Throw", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun EyeField(
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
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !hasBeenFocused) {
                    onValueChange("")
                    hasBeenFocused = true
                }

                if (!focusState.isFocused && value == "") {
                    onValueChange("0")
                    hasBeenFocused = false
                }
            }
    )
}

@Composable
fun CoordinateItem(eyeThrow: EyeThrow, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "${eyeThrow.x.toInt()}, ${eyeThrow.z.toInt()}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "angle ${eyeThrow.angle}°",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete throw",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}