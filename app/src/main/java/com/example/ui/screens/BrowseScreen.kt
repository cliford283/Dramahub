package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DramaEntity
import com.example.ui.components.DramaPosterCard
import com.example.ui.theme.DramaBlack
import com.example.ui.theme.DramaCard
import com.example.ui.theme.DramaRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BrowseScreen(
    selectedCategory: String,
    allDramas: List<DramaEntity>,
    favoriteIds: List<String>,
    onCategorySelect: (String) -> Unit,
    onDramaClick: (DramaEntity) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "COLLECTION", "NEW", "HOTLIST", "FANTASY", "ASIA", "FEMALE", "MALE", "REVENGE", "BILLIONAIRE"
    )

    val filteredDramas = when (selectedCategory) {
        "COLLECTION" -> allDramas
        "NEW" -> allDramas.filter { it.category == "NEW" || it.totalEpisodes > 50 }
        "HOTLIST" -> allDramas.filter { it.category == "HOTLIST" || it.rating >= 4.8f }
        "FANTASY" -> allDramas.filter { it.category == "FANTASY" || it.tags.contains("FANTASY") }
        "REVENGE" -> allDramas.filter { it.tags.contains("REVENGE") }
        "BILLIONAIRE" -> allDramas.filter { it.tags.contains("CEO") || it.tags.contains("BILLIONAIRE") }
        else -> allDramas
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DramaBlack)
            .statusBarsPadding()
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "BROWSE",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Channels • curated shelves",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        // Category tab chips row (matching video 00:54)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) DramaRed else DramaCard)
                        .clickable { onCategorySelect(cat) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3-Column Poster Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 84.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDramas) { drama ->
                DramaPosterCard(
                    drama = drama,
                    isFavorite = favoriteIds.contains(drama.id),
                    onDramaClick = onDramaClick,
                    onFavoriteToggle = onFavoriteToggle,
                    showTitleBelow = true
                )
            }
        }
    }
}
