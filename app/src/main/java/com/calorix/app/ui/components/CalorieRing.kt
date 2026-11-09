package com.calorix.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calorix.app.ui.theme.AmberAccent
import com.calorix.app.ui.theme.CoralRed
import com.calorix.app.ui.theme.EmeraldPrimary

@Composable
fun CalorieRing(
    consumed: Int,
    goal: Int,
    progress: Float,
    isOverGoal: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 16.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "CalorieRingProgress"
    )

    val ringColor = when {
        isOverGoal -> CoralRed
        progress > 0.85f -> AmberAccent
        else -> EmeraldPrimary
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = size.toPx() - strokePx
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background track ring (360 degrees)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Foreground progress ring
            val sweep = animatedProgress * 360f
            if (sweep > 0f) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Center typography and stats
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$consumed",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "of $goal kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            val remaining = goal - consumed
            if (isOverGoal) {
                Text(
                    text = "+${-remaining} kcal over",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CoralRed
                )
            } else {
                Text(
                    text = "$remaining kcal left",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = EmeraldPrimary
                )
            }
        }
    }
}
