package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DramaEntity
import com.example.ui.components.DramaPosterCard
import com.example.ui.components.DramaPosterImage
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.DramaAmber
import com.example.ui.theme.DramaBlack
import com.example.ui.theme.DramaCard
import com.example.ui.theme.DramaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    featuredDrama: DramaEntity?,
    allDramas: List<DramaEntity>,
    favoriteIds: List<String>,
    isAdminLoggedIn: Boolean,
    onDramaClick: (DramaEntity) -> Unit,
    onPlayClick: (DramaEntity) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featured = featuredDrama ?: allDramas.firstOrNull()
    val continueWatching = allDramas.take(3)
    val trendingDramas = allDramas.filter { it.category == "TRENDING" || it.rating >= 4.8f }
    val newReleases = allDramas.filter { it.category == "NEW" || it.totalEpisodes > 50 }
    val theEditList = allDramas.takeLast(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DramaBlack),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Sticky/Top Header Bar
        item {
            TopHeaderBar(
                onSearchClick = onSearchClick,
                onAdminClick = onAdminClick,
                isAdminLoggedIn = isAdminLoggedIn
            )
        }

        // Hero Cover Story (matching video 00:12 - 00:16)
        item {
            if (featured != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DramaCard)
                        .testTag("hero_cover_story")
                ) {
                    // Full Hero Backdrop Art
                    DramaPosterImage(
                        drawableResName = featured.coverDrawableResName,
                        title = featured.title,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic multi-step gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f),
                                        DramaBlack
                                    )
                                )
                            )
                    )

                    // Top Right Episode Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${featured.totalEpisodes} EP",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bottom Content: Title & Action Buttons
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "COVER STORY",
                            color = DramaAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = featured.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons Row (Play + More Info)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onPlayClick(featured) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .height(42.dp)
                                    .testTag("hero_play_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Play",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { onDramaClick(featured) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .height(42.dp)
                                    .testTag("hero_more_info_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "More Info",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pager dots indicator (video 00:13)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White)
                            )
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.35f))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: RESUME - Continue Watching
        item {
            SectionHeader(sub = "RESUME", main = "Continue Watching")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(continueWatching) { drama ->
                    DramaPosterCard(
                        drama = drama,
                        isFavorite = favoriteIds.contains(drama.id),
                        onDramaClick = onDramaClick,
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.width(128.dp)
                    )
                }
            }
        }

        // Section: THIS WEEK - Trending Now
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(sub = "THIS WEEK", main = "Trending Now")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trendingDramas) { drama ->
                    DramaPosterCard(
                        drama = drama,
                        isFavorite = favoriteIds.contains(drama.id),
                        onDramaClick = onDramaClick,
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.width(124.dp)
                    )
                }
            }
        }

        // Section: JUST DROPPED - New Releases
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(sub = "JUST DROPPED", main = "New Releases")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(newReleases) { drama ->
                    DramaPosterCard(
                        drama = drama,
                        isFavorite = favoriteIds.contains(drama.id),
                        onDramaClick = onDramaClick,
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.width(124.dp)
                    )
                }
            }
        }

        // Section: THE EDIT
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(sub = "CURATED", main = "The Edit")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(theEditList) { drama ->
                    DramaPosterCard(
                        drama = drama,
                        isFavorite = favoriteIds.contains(drama.id),
                        onDramaClick = onDramaClick,
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.width(124.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(sub: String, main: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = sub,
            color = DramaAmber,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = main,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
