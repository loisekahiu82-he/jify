package com.example.gigify.Screens.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Models.Message
import com.example.gigify.Models.ViewModels.ChatViewModel
import com.example.gigify.ui.theme.DarkPurple
import com.example.gigify.ui.theme.GigifyTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    otherUserName: String = "User",
    viewModel: ChatViewModel = viewModel()
) {
    val currentUserId = try {
        FirebaseAuth.getInstance().currentUser?.uid ?: "user123"
    } catch (e: Exception) {
        "user123"
    }
    
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        viewModel.listenToMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    ChatContent(
        otherUserName = otherUserName,
        messages = messages,
        currentUserId = currentUserId,
        messageText = messageText,
        isSending = isSending,
        onMessageChange = { messageText = it },
        onBackClick = { navController.navigateUp() },
        onSendClick = {
            if (messageText.isNotBlank()) {
                viewModel.sendMessage(chatId, messageText.trim())
                messageText = ""
            }
        },
        listState = listState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    otherUserName: String,
    messages: List<Message>,
    currentUserId: String,
    messageText: String,
    isSending: Boolean,
    onMessageChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendClick: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState = rememberLazyListState()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DarkPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = otherUserName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(otherUserName, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Active now", color = Color(0xFF4CAF50), fontSize = 11.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = onMessageChange,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        placeholder = { Text("Type a message...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSendClick() })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSendClick,
                        enabled = messageText.isNotBlank() && !isSending,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (messageText.isNotBlank()) DarkPurple else Color.Gray)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No messages yet. Say hi!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message, isMine = message.senderId == currentUserId)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMine: Boolean) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bubbleColor = if (isMine) DarkPurple else Color(0xFFE9E9EB)
    val textColor = if (isMine) Color.White else Color.Black
    val shape = if (isMine) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = message.text, color = textColor, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.sentAt))
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = if (isMine) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    GigifyTheme {
        ChatContent(
            otherUserName = "John Mwangi",
            messages = listOf(
                Message(text = "Hi, is the plumbing job still available?", senderId = "other", sentAt = System.currentTimeMillis() - 600000),
                Message(text = "Yes it is! When can you start?", senderId = "me", sentAt = System.currentTimeMillis() - 300000),
                Message(text = "I can come by tomorrow morning.", senderId = "other", sentAt = System.currentTimeMillis())
            ),
            currentUserId = "me",
            messageText = "That works for me!",
            isSending = false,
            onMessageChange = {},
            onBackClick = {},
            onSendClick = {}
        )
    }
}
