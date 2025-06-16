package com.nyinyi.quickfeed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nyinyi.quickfeed.R

@Composable
fun CircleProfileIcon(
    imageUrl: String? = null,
    placeholderIcon: ImageVector = Icons.Default.Person,
    size: Dp = 48.dp,
    shadowElevation: Dp = 4.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .shadow(
                    elevation = shadowElevation,
                    shape = CircleShape,
                    clip = false,
                ).clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.1f)),
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Optional placeholder
            )
        } else {
            Icon(
                imageVector = placeholderIcon,
                contentDescription = "Default profile",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                tint = Color.Gray,
            )
        }
    }
}

@Composable
fun SimpleCircleProfileIcon(
    profilePictureUrl: String? = null,
    icon: ImageVector = Icons.Default.Person,
    size: Dp = 48.dp,
    shadowElevation: Dp = 4.dp,
    backgroundColor: Color = Color.LightGray,
    iconTint: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .shadow(
                    elevation = shadowElevation,
                    shape = CircleShape,
                    clip = false,
                ).clip(CircleShape)
                .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Profile icon",
            modifier = Modifier.size(size * 0.6f),
            tint = iconTint,
        )
    }
}

@Preview
@Composable
fun ProfileIconPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircleProfileIcon(
            imageUrl = "https://example.com/profile.jpg",
            size = 64.dp,
        )

        SimpleCircleProfileIcon(
            icon = Icons.Default.Person,
            size = 56.dp,
            backgroundColor = MaterialTheme.colorScheme.primary,
            iconTint = Color.White,
        )

        SimpleCircleProfileIcon(
            icon = Icons.Default.AccountCircle,
            size = 72.dp,
            shadowElevation = 8.dp,
            backgroundColor = Color.Blue,
            iconTint = Color.White,
        )
    }
}
