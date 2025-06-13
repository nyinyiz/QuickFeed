package com.nyinyi.quickfeed.ui.screen.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.components.DefaultAppGradientBackground
import com.nyinyi.quickfeed.ui.components.ParticleAnimationBackground
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme
import com.nyinyi.quickfeed.ui.theme.ThemeColors

@Preview
@Composable
fun WelcomeScreen(
    onToggleTheme: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    QuickFeedTheme(
        darkTheme = false,
    ) {
    }
    WelcomeContent(
        onToggleTheme = onToggleTheme,
        onSignUpClick = onSignUpClick,
        onSignInClick = onSignInClick,
    )
}

@Preview
@Composable
fun WelcomeScreenDarkTheme(
    onToggleTheme: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    QuickFeedTheme(
        darkTheme = true,
    ) {
        WelcomeContent(
            onToggleTheme = onToggleTheme,
            onSignUpClick = onSignUpClick,
            onSignInClick = onSignInClick,
        )
    }
}

@Composable
fun WelcomeContent(
    onToggleTheme: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier =
                        Modifier
                            .padding(horizontal = 8.dp, vertical = 32.dp),
                ) {
                    Text(
                        text = if (ThemeColors.isDarkTheme) "☀️" else "🌙",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        },
    ) { paddingValues ->
        DefaultAppGradientBackground(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            ParticleAnimationBackground(
                particleBaseColor = MaterialTheme.colorScheme.secondary,
                particleCount = 17,
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Spacer(modifier = Modifier.weight(0.3f))

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.app_logo_content_description),
                    modifier =
                        Modifier
                            .size(150.dp)
                            .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit,
                )

                Text(
                    text = stringResource(id = R.string.app_name),
                    style =
                        MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            shadow =
                                Shadow(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offset = Offset(x = 2f, y = 2f),
                                    blurRadius = 1f,
                                ),
                        ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text = stringResource(id = R.string.welcome_message),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = onSignUpClick,
                        shape = RoundedCornerShape(12.dp),
                        elevation =
                            ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 6.dp,
                            ),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                    ) {
                        Text(text = stringResource(id = R.string.sign_up))
                    }

                    OutlinedButton(
                        onClick = onSignInClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                    ) {
                        Text(text = stringResource(id = R.string.sign_in))
                    }

                    Text(
                        text = stringResource(id = R.string.trouble_signing_in),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
