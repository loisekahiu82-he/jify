package com.example.gigify.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gigify.Navigation.ROUTE_POST_JOB
import com.example.gigify.ui.theme.*

data class WorkerDisplay(
    val name: String,
    val profession: String,
    val distance: String,
    val rating: String,
    val reviews: String,
    val status: String,
    val initials: String,
    val buttonText: String = "Hire",
    val isButtonLight: Boolean = false
)

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MainPurple else LightPurple.copy(alpha = 0.4f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MainPurple else MainPurple.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = label,
            color = if (isSelected) White else MainPurple,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun WorkerCard(worker: WorkerDisplay, navController: NavController? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightPurple, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MainPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(worker.initials, color = White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(worker.name, color = MainPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${worker.profession} · ${worker.distance}", color = MainPurple.copy(alpha = 0.6f), fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = LightPurple.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            worker.status,
                            color = MainPurple,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MainPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(worker.rating, color = MainPurple, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                    Text("(${worker.reviews})", color = MainPurple.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }

            Button(
                onClick = { 
                    navController?.navigate(ROUTE_POST_JOB)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (worker.isButtonLight) White else MainPurple,
                    contentColor = if (worker.isButtonLight) MainPurple else White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = if (worker.isButtonLight) Modifier.border(1.dp, MainPurple, RoundedCornerShape(8.dp)) else Modifier,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(worker.buttonText)
            }
        }
    }
}

@Composable
fun InfoBanner() {
    Surface(
        color = LightPurple.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, MainPurple.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pay securely via M-Pesa — no cash needed",
                color = MainPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
