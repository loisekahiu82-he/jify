package com.example.gigify.Screens.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gigify.Models.Chat
import com.example.gigify.Models.Contact
import com.example.gigify.Models.ViewModels.ChatViewModel
import com.example.gigify.Navigation.ROUTE_CHAT
import com.example.gigify.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(navController: NavController, viewModel: ChatViewModel = viewModel()) {
    val context = LocalContext.current
    val chats by viewModel.chats.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    
    var showContactDialog by remember { mutableStateOf(false) }
    
    var hasContactPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactPermission = isGranted
        if (isGranted) {
            viewModel.fetchContacts(context)
            showContactDialog = true
        } else {
            Toast.makeText(context, "Contact permission denied. You can't start chats with your contacts.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(hasContactPermission) {
        if (hasContactPermission) {
            viewModel.fetchContacts(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Messages", color = Color.Black, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (hasContactPermission) {
                            showContactDialog = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    },
                    containerColor = AppPrimary,
                    contentColor = AppBackground
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "New Chat")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (chats.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No messages yet", color = Color.Black.copy(alpha = 0.5f))
                            if (!hasContactPermission) {
                                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                                    Text("Enable Contact Permission", color = AppPrimary)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chats) { chat ->
                            ChatItem(chat = chat) {
                                navController.navigate("${ROUTE_CHAT}/${chat.chatId}/User")
                            }
                        }
                    }
                }
            }
        }

        if (showContactDialog) {
            AlertDialog(
                onDismissRequest = { showContactDialog = false },
                title = { Text("Select a Contact", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        if (contacts.isEmpty()) {
                            Text("No contacts found.", color = Color.Gray)
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(contacts) { contact ->
                                    ContactItem(contact = contact) {
                                        // Here you would typically check if a chat already exists
                                        // or create a new one in Firebase. 
                                        // For now, we'll just show a toast and close.
                                        Toast.makeText(context, "Starting chat with ${contact.name}", Toast.LENGTH_SHORT).show()
                                        showContactDialog = false
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showContactDialog = false }) {
                        Text("Close", color = AppPrimary)
                    }
                },
                containerColor = White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun ContactItem(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.take(1).uppercase(),
                color = AppPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = contact.name, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = contact.phoneNumber, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = AppBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "User Name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = chat.lastMessage,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(chat.lastMessageTimestamp))
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}
