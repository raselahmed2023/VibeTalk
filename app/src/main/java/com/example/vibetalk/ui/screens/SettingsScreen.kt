package com.example.vibetalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibetalk.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val autoSpeak by viewModel.autoSpeak.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()

    val bgColor = Color(0xFF0B0F1E)
    val cardColor = Color(0xFF151B30)
    val purple = Color(0xFF7F77DD)
    val textColor = Color(0xFFD0D4F0)
    val subTextColor = Color(0xFF8B93B8)
    val dividerColor = Color(0xFF1E2440)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = purple, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Voice section
        Text(
            text = "VOICE",
            color = subTextColor,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Voice speed
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Voice Speed", color = textColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("How fast AI speaks", color = subTextColor, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Slow", "Normal", "Fast").forEach { speed ->
                        val selected = voiceSpeed == speed
                        Button(
                            onClick = { viewModel.setVoiceSpeed(speed) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) purple else Color(0xFF1E2440)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                speed,
                                color = if (selected) Color.White else subTextColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Auto speak toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto Speak", color = textColor, fontSize = 14.sp)
                    Text("AI reads reply aloud", color = subTextColor, fontSize = 12.sp)
                }
                Switch(
                    checked = autoSpeak,
                    onCheckedChange = { viewModel.setAutoSpeak(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = purple
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App section
        Text(
            text = "APP",
            color = subTextColor,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clear history
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Clear Chat History", color = textColor, fontSize = 14.sp)
                Text("Remove all messages", color = subTextColor, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.clearMessages()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3D1515)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear History", color = Color(0xFFD85A30))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App info
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("VibeTalk", color = subTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Version 1.0", color = Color(0xFF535A7A), fontSize = 11.sp)
            }
        }
    }
}