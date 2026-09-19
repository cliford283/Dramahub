package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DramaBlack
import com.example.ui.theme.DramaRed
import com.example.ui.theme.TextPrimary

@Composable
fun TopHeaderBar(
    onSearchClick: () -> Unit,
    onAdminClick: () -> Unit,
    isAdminLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand logo matching video
        Column(
            modifier = Modifier.clickable { /* Brand tap */ }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FREE",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(2.dp)
                        .background(DramaRed)
                )
            }
            Text(
                text = "SHORT DRAMA",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Search quick access
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search dramas",
                    tint = Color.White
                )
            }

            // Private Admin Dashboard Access Button
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("top_admin_button")
            ) {
                BadgedBox(
                    badge = {
                        if (isAdminLoggedIn) {
                            Badge(
                                containerColor = Color(0xFF00E676),
                                modifier = Modifier.size(8.dp)
                            )
                        } else {
                            Badge(
                                containerColor = DramaRed,
                                contentColor = Color.White
                            ) {
                                Text("ADMIN", fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAdminLoggedIn) {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF2C2638), Color(0xFF1E1A26))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Private Admin Dashboard",
                            tint = if (isAdminLoggedIn) Color.White else Color(0xFFFF9F1C),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
