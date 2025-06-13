package com.nyinyi.quickfeed.ui.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyinyi.quickfeed.R
import com.nyinyi.quickfeed.ui.theme.QuickFeedTheme

data class LoginScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

@Composable
fun LoginScreen(
    onLoginClicked: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    val uiState = LoginScreenState()

    LoginContent(
        uiState = uiState,
        onLoginClicked = onLoginClicked,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPassword = onForgotPassword,
    )
}

@Composable
fun LoginContent(
    uiState: LoginScreenState,
    onLoginClicked: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val isLoading = uiState.isLoading
    val errorMessage = uiState.error

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.surface,
                                ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY,
                        ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_logo_content_description),
                modifier =
                    Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = stringResource(R.string.welcome_back_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = stringResource(R.string.login_to_continue_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.email_address_label)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = stringResource(R.string.email_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true,
                isError = errorMessage != null,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password_label)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = stringResource(R.string.password_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription =
                                if (isPasswordVisible) {
                                    stringResource(R.string.hide_password_desc)
                                } else {
                                    stringResource(
                                        R.string.show_password_desc,
                                    )
                                },
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions =
                    KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onLoginClicked(email, password)
                    }),
                singleLine = true,
                isError = errorMessage != null,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_link),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier =
                        Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onForgotPassword,
                            ).padding(vertical = 4.dp),
                )
            }

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier =
                        Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLoginClicked(email, password)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 6.dp,
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.log_in_button),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.dont_have_account_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.sign_up_link),
                    modifier =
                        Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onNavigateToRegister,
                            ).padding(4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun LoginScreenLightPreview() {
    QuickFeedTheme(
        darkTheme = false,
    ) {
        LoginScreen(
            onLoginClicked = { _, _ -> },
            onNavigateToRegister = {},
            onForgotPassword = {},
        )
    }
}

@Preview(showBackground = true, name = "Login Screen Dark")
@Composable
fun LoginScreenDarkPreview() {
    QuickFeedTheme(
        darkTheme = true,
    ) {
        LoginScreen(
            onLoginClicked = { _, _ -> },
            onNavigateToRegister = {},
            onForgotPassword = {},
        )
    }
}
