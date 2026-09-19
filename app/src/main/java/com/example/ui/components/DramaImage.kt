package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DramaCard
import com.example.ui.theme.DramaCardElevated

@Composable
fun DramaPosterImage(
    drawableResName: String,
    title: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        drawableResName,
        "drawable",
        context.packageName
    )

    if (resourceId != 0) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = title,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        // Aesthetic cinematic gradient fallback
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(
                    colors = listOf(
                        DramaCardElevated,
                        DramaCard,
                        Color(0xFF0F0E13)
                    )
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxSize(0.4f)
            )
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
