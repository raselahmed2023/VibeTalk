package com.example.vibetalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
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

    val bgColor = Color(0xFF0B0F1E)
    val purple = Color(0xFF7F77DD)
    val micColor = if (isRecording) Color.Red else purple

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VibeTalk",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onSettingsClick) {
                    Text("⚙️ Settings", color = Color(0xFF8B93B8), fontSize = 13.sp)
                }
            }

            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎙️", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Tap the mic and start speaking",
                                    color = Color(0xFF535A7A),
                                    fontSize = 14.sp
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

            // Status + Mic
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = statusText,
                    color = Color(0xFF8B93B8),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onMicClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = micColor),
                    modifier = Modifier.size(72.dp)
                ) {
                    Text(
                        text = if (isRecording) "⏹" else "🎙️",
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRecording) "Tap to stop" else "Tap to speak",
                    color = Color(0xFF535A7A),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val purple = Color(0xFF7F77DD)
    val darkCard = Color(0xFF151B30)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isUser) purple else darkCard,
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
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = message.time,
            color = Color(0xFF535A7A),
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}