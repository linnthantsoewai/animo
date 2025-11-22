package com.ltsw.animo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ltsw.animo.AnimoApplication
import com.ltsw.animo.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as AnimoApplication
    val userRepository = application.userRepository

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo/Icon
            Image(
                painter = painterResource(id = R.drawable.ic_animo_logo),
                contentDescription = "Animo Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "Animo",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Tagline
            Text(
                text = "Your Pet Care Companion",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Welcome Text
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please sign in to continue.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Your Name") },
                placeholder = { Text("Enter your name") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Name")
                },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Name is required") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { Text("Email Address") },
                placeholder = { Text("Enter your email") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Email")
                },
                isError = emailError,
                supportingText = if (emailError) {
                    { Text("Valid email is required") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Create Account Button
            Button(
                onClick = {
                    var hasError = false

                    if (name.isBlank()) {
                        nameError = true
                        hasError = true
                    }

                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = true
                        hasError = true
                    }

                    if (!hasError) {
                        isLoading = true
                        scope.launch {
                            try {
                                userRepository.registerUser(name.trim(), email.trim())
                                onLoginSuccess()
                            } catch (e: Exception) {
                                isLoading = false
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${e.message}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.large
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Privacy Note
            Text(
                text = "Your data is stored locally on your device.\nWe respect your privacy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

