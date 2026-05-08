package com.example.gigify.Models.ViewModels

import android.content.Context
import android.provider.ContactsContract
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigify.Models.Chat
import com.example.gigify.Models.Contact
import com.example.gigify.Models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    init {
        fetchChats()
    }

    fun fetchChats() {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("Chats")
            .whereArrayContains("participants", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val chatList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Chat::class.java)?.copy(chatId = doc.id)
                } ?: emptyList()
                
                _chats.value = chatList
            }
    }

    fun fetchContacts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contactList = mutableListOf<Contact>()
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val number = it.getString(numberIndex)
                    contactList.add(Contact(name, number))
                }
            }
            
            withContext(Dispatchers.Main) {
                _contacts.value = contactList.distinctBy { it.phoneNumber }
            }
        }
    }

    fun listenToMessages(chatId: String) {
        db.collection("Chats")
            .document(chatId)
            .collection("Messages")
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val msgs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(messageId = doc.id)
                } ?: emptyList()
                
                _messages.value = msgs
            }
    }
    
    fun sendMessage(chatId: String, text: String) {
        val uid = auth.currentUser?.uid ?: return
        if (text.isEmpty()) return

        _isSending.value = true
        val messageId = db.collection("Chats").document(chatId).collection("Messages").document().id
        val message = Message(
            messageId = messageId,
            chatId = chatId,
            senderId = uid,
            text = text,
            sentAt = System.currentTimeMillis()
        )

        db.collection("Chats").document(chatId).collection("Messages").document(messageId).set(message)
            .addOnCompleteListener {
                _isSending.value = false
            }
        
        db.collection("Chats").document(chatId).update(
            "lastMessage", text,
            "lastMessageTimestamp", System.currentTimeMillis()
        )
    }

    fun deleteChat(chatId: String, context: Context) {
        db.collection("Chats").document(chatId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Chat deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete chat", Toast.LENGTH_SHORT).show()
            }
    }
}
