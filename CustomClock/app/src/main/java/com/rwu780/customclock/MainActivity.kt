package com.rwu780.customclock

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rwu780.customclock.ui.theme.CustomClockTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.text.Typography.degree

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomClockTheme {

                var currentTime by remember {
                    mutableStateOf(LocalDateTime.now())
                }

                LaunchedEffect(key1 = currentTime) {
                    currentTime = LocalDateTime.now()
                    delay(500)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    CustomClock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        hour = currentTime.hour,
                        minute = currentTime.minute,
                        seconds = currentTime.second,
                    )
                }
            }
        }
    }
}

data class ClockStyle(
    val radius: Dp = 100.dp,
    val startingAngle: Int = 0,
    val endingAngle: Int = 360,
    val lineStroke: Dp = 5.dp,
    val minlineColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.LightGray,
    val minlineLength: Dp = 10.dp,
    val quarterLineColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Red,
    val quarterLength: Dp = 20.dp
)

@Composable
fun CustomClock(
    modifier: Modifier = Modifier,
    clockStyle: ClockStyle = ClockStyle(),
    hour: Int = 10,
    minute: Int = 43,
    seconds: Int = 23
) {

    val radius = clockStyle.radius
    val startingAngle = clockStyle.startingAngle
    val endingAngle = clockStyle.endingAngle

    Canvas(
        modifier = modifier,
    ) {

        val circleCenter = center

        // Draw Clock Face
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    strokeWidth = 2.dp.toPx()
                    color = Color.WHITE
                    this.style = Paint.Style.FILL_AND_STROKE
                    setShadowLayer(
                        100f,
                        0f,
                        0f,
                        Color.argb(80, 0, 0, 0)
                    )
                }
            )
        }

        // Draw Clock center
        drawCircle(
            androidx.compose.ui.graphics.Color.Black,
            center = circleCenter,
            radius = 5.dp.toPx(),

            )

        // Draw Tick
        for (i in startingAngle..endingAngle step 30) {

            val lineStyle = when {
                i % 90 == 0 -> LineStyle.QuarterLineStyle
                else -> LineStyle.MinLineStyle
            }

            val (lineColor, lineLength) = when (lineStyle) {
                LineStyle.QuarterLineStyle -> Pair(
                    clockStyle.quarterLineColor,
                    clockStyle.quarterLength
                )
                LineStyle.MinLineStyle -> Pair(clockStyle.minlineColor, clockStyle.minlineLength)
            }

            val angleInRad = degreeToRadian(i)

            val lineStart = Offset(
                x = (radius - lineLength).toPx() * cos(angleInRad) + circleCenter.x,
                y = (radius - lineLength).toPx() * sin(angleInRad) + circleCenter.y
            )

            val lineEnd = Offset(
                x = (radius).toPx() * cos(angleInRad) + circleCenter.x,
                y = (radius).toPx() * sin(angleInRad) + circleCenter.y
            )

            drawLine(
                color = lineColor,
                lineStart,
                lineEnd,
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Hour Hand
        var hourDegree = hour * (360 / 12) + (360 / 12) * (minute / 60) - 90
        var hourStart = Offset(
            circleCenter.x + 15.dp.toPx() * cos(degreeToRadian(hourDegree)),
            circleCenter.y + 15.dp.toPx() * sin(degreeToRadian(hourDegree))
        )

        var hourEnd = Offset(
            circleCenter.x + (radius - 40.dp).toPx() * cos(degreeToRadian(hourDegree)),
            circleCenter.y + (radius - 40.dp).toPx() * sin(degreeToRadian(hourDegree))
        )

        drawLine(
            androidx.compose.ui.graphics.Color.Black,
            hourStart,
            hourEnd,
            strokeWidth = 6.dp.toPx()
        )

        // Draw Minute Hand
        var minDegree = minute * (360 / 60) - 90 + (360 / 60) * (seconds / 60)
        val minStart = Offset(
            x = circleCenter.x + 15.dp.toPx() * cos(degreeToRadian(minDegree)),
            y = circleCenter.y + 15.dp.toPx() * sin(degreeToRadian(minDegree))
        )

        val minEnd = Offset(
            x = circleCenter.x + (radius.toPx() - 30.dp.toPx()) * cos(degreeToRadian(minDegree)),
            y = circleCenter.y + (radius.toPx() - 30.dp.toPx()) * sin(degreeToRadian(minDegree))
        )

        drawLine(
            androidx.compose.ui.graphics.Color.Black,
            minStart,
            minEnd,
            strokeWidth = 6.dp.toPx()
        )


        // Draw sec hand
        var secDegree = seconds * (360 / 60) - 90

        val secStart = Offset(
            x = circleCenter.x + 15.dp.toPx() * cos(degreeToRadian(secDegree)),
            y = circleCenter.y + 15.dp.toPx() * sin(degreeToRadian(secDegree))
        )

        val secEnd = Offset(
            x = circleCenter.x + (radius.toPx() - 25.dp.toPx()) * cos(degreeToRadian(secDegree)),
            y = circleCenter.y + (radius.toPx() - 25.dp.toPx()) * sin(degreeToRadian(secDegree))
        )

        drawLine(
            androidx.compose.ui.graphics.Color.Red,
            secStart,
            secEnd,
            strokeWidth = 3.dp.toPx()
        )
    }
}

private fun degreeToRadian(degree: Int): Float {
    return (degree * PI / 180f).toFloat()
}

private fun radianToDegree(radian: Float): Int {
    return (radian * 180f / PI).toInt()
}

sealed class LineStyle {
    object MinLineStyle : LineStyle()
    object QuarterLineStyle : LineStyle()
}

