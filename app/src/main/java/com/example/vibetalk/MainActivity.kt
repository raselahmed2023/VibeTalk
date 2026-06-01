package com.example.vibetalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vibetalk.api.ChatMessage
import com.example.vibetalk.ui.screens.SettingsScreen
import com.example.vibetalk.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) toggleRecording()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation(viewModel = viewModel) {
                checkAndRecord()
            }
        }
    }

    private fun checkAndRecord() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            toggleRecording()
        } else {
            requestPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun toggleRecording() {
        if (viewModel.isRecording.value) {
            viewModel.stopRecording()
        } else {
            viewModel.startRecording()
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel, onMicClick: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            VibeTalkApp(
                viewModel = viewModel,
                onMicClick = onMicClick,
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun VibeTalkApp(
    viewModel: MainViewModel,
    onMicClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val messages by viewModel.messages.collectAsState()

    // Colors
    val bgColor = Color(0xFF070B1A)
    val purple = Color(0xFF7F77DD)
    val darkCard = Color(0xFF0F1428)

    // Mic pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070B1A),
                        Color(0xFF0D1128),
                        Color(0xFF070B1A)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(purple, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VibeTalk",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSettingsClick) {
                    Text("⚙️", fontSize = 20.sp)
                }
            }

            // Chat area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // AI Avatar
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .border(
                                            2.dp,
                                            Brush.sweepGradient(
                                                listOf(purple, Color(0xFF4CC9F0), purple)
                                            ),
                                            CircleShape
                                        )
                                        .padding(3.dp)
                                        .background(Color(0xFF151B30), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🤖", fontSize = 32.sp)
                                }
                                Text(
                                    "VibeTalk AI",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Tap mic and start speaking",
                                    color = Color(0xFF535A7A),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
                items(messages) { message ->
                    ChatBubble(message)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Bottom bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF070B1A))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Status
                if (statusText != "Tap mic to speak") {
                    Text(
                        text = statusText,
                        color = purple,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                }

                // Input row
                var textInput by remember { mutableStateOf("") }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Text field
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                "Type a message...",
                                color = Color(0xFF3A4060),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = purple,
                            unfocusedBorderColor = Color(0xFF1E2440),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = purple,
                            focusedContainerColor = Color(0xFF0F1428),
                            unfocusedContainerColor = Color(0xFF0F1428)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send button
                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendTextMessage(textInput)
                                textInput = ""
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (textInput.isNotBlank())
                                purple else Color(0xFF1E2440)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("➤", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Mic button with pulse
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isRecording) Modifier.scale(pulseScale) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onMicClick,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording)
                                    Color(0xFFE53935) else purple
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = if (isRecording) "⏹" else "🎙️",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val purple = Color(0xFF7F77DD)
    val darkCard = Color(0xFF0F1428)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // Name label
        Text(
            text = if (message.isUser) "You" else "VibeTalk AI",
            color = Color(0xFF535A7A),
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    brush = if (message.isUser)
                        Brush.linearGradient(listOf(purple, Color(0xFF5B54C7)))
                    else
                        Brush.linearGradient(listOf(darkCard, darkCard)),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .border(
                    width = if (message.isUser) 0.dp else 0.5.dp,
                    color = if (message.isUser) Color.Transparent else Color(0xFF1E2440),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Text(
            text = message.time,
            color = Color(0xFF353B55),
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}