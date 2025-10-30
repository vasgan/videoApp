package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun ShimmerImage(url: String, modifier: Modifier = Modifier,scale: ContentScale, rounded: Int) {
    var isLoading by remember { mutableStateOf(true) }

    AsyncImage(
        contentScale = scale,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .listener(
                onSuccess = { _, _ -> isLoading = false },
                onError = { _, _ -> isLoading = false }
            )
            .build(),
        contentDescription = null,
        //todo выпилить либу и запилить кастомный шиммер
        modifier = modifier
            .clip(RoundedCornerShape(rounded.dp))
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                color = Color.LightGray
            )
    )
}

@Composable
fun AnnotatedClickableText(
    text: AnnotatedString,
    modifier: Modifier,
    textStyle: TextStyle,
    onAnnotationClick: (String) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = text,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { pos ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    text.getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()?.let { ann ->
                            onAnnotationClick(ann.item)
                        }
                }
            }
        },
        onTextLayout = { layoutResult.value = it },
        style = textStyle
    )
}

@Composable
fun CustomSlider(
    value: Float,                               // внешнее значение (например, позиция плеера)
    onPreviewChange: (Float) -> Unit = {},      // обновляем превью/таймкод во время drag
    onCommitChange: (Float) -> Unit = {},       // применяем (seek) в конце
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    bufferedFraction: Float = 0f,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    trackHeight: Dp = 6.dp,
    thumbRadius: Dp = 8.dp,
    activeColor: Color = Color.Red,
    bufferedColor: Color = Color.LightGray.copy(alpha = 0.6f),
    inactiveColor: Color = Color.Gray.copy(alpha = 0.35f),
    thumbColor: Color = Color.White,
    thumbScaleIdle: Float = 1f,
    thumbScaleDrag: Float = 1.95f,
    thumbScaleSpec: FiniteAnimationSpec<Float> = spring(
        stiffness = Spring.StiffnessMediumLow,  // плавно
        dampingRatio = Spring.DampingRatioNoBouncy
    ),
) {
    //Локальный перевод ДП в пиксели
    val density = LocalDensity.current
    val thumbR = with(density) { thumbRadius.toPx() }

    // локальный override на время drag
    var draggingValue by remember { mutableStateOf<Float?>(null) }
    val isDragging = draggingValue != null

    //текущее положение ползунка
    val external = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val drawValue = draggingValue ?: external

    //высчитываем полжение ползунка откуда он проехал от начала и до конца
    val fraction = if (valueRange.endInclusive > valueRange.start)
        (drawValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
    else 0f

    // сбрасываем override, когда внешнее значение догнало коммит
    LaunchedEffect(external) {
        draggingValue?.let { local ->
            val eps = (valueRange.endInclusive - valueRange.start) * 0.001f
            if (kotlin.math.abs(external - local) <= eps) draggingValue = null
        }
    }

    var widthPx by remember { mutableFloatStateOf(1f) }
    fun pxToValue(x: Float): Float {
        val f = (x / widthPx).coerceIn(0f, 1f)
        return valueRange.start + f * (valueRange.endInclusive - valueRange.start)
    }

    //высчитываем drag и переключение
    val gestures = if (enabled) Modifier.pointerInput(valueRange) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            draggingValue = pxToValue(down.position.x)
            onPreviewChange(draggingValue!!)

            horizontalDrag(down.id) { change ->
                change.consume()
                val v = pxToValue(change.position.x)
                draggingValue = v
                onPreviewChange(v)
            }

            // коммитим, но оставляем draggingValue до прихода внешнего value
            draggingValue?.let { onCommitChange(it) }
        }
    } else Modifier

    // анимация масштаба кружка при drag
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) thumbScaleDrag else thumbScaleIdle,
        animationSpec = thumbScaleSpec,
        label = "thumbScale"
    )

    Box(
        modifier
            .height(max(trackHeight + 2 * thumbRadius, 36.dp))
            .alpha(if (enabled) 1f else 0.6f)
            .then(gestures),
        contentAlignment = Alignment.CenterStart
    ) {
        // трек
        Canvas(
            Modifier
                .fillMaxWidth()
                .onGloballyPositioned { widthPx = it.size.width.toFloat() }
                .height(trackHeight)
                .clip(CircleShape)
        ) {
            val w = size.width
            val h = size.height

            drawRect(inactiveColor, size = Size(w, h))
            if (bufferedFraction > 0f) {
                drawRect(bufferedColor, size = Size(w * bufferedFraction.coerceIn(0f, 1f), h))
            }
            drawRect(activeColor, size = Size(w * fraction.coerceIn(0f, 1f), h))
        }

        // бегунок (без позиционной анимации во время drag)
        val thumbX = (widthPx * fraction).coerceIn(0f, widthPx)
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(max(trackHeight, thumbRadius * 2))
        ) {
            // лёгкая тень
            drawCircle(
                color = Color.Black.copy(alpha = 0.25f),
                radius = thumbR * thumbScale * 1.15f,
                center = Offset(thumbX, size.height / 2f + 1.5f)
            )
            // сам кружок
            drawCircle(
                color = thumbColor,
                radius = thumbR * thumbScale,
                center = Offset(thumbX, size.height / 2f)
            )
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Activity.setFullscreen(enable: Boolean) {
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    if (enable) {
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}