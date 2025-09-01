package com.example.chatbot

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import android.util.Log

class ChatViewModel:ViewModel() {

    val MessageList by lazy {
        mutableStateListOf<ChatMessageModel>()
    }
    
    val isLoading = mutableStateOf(false)

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash", // Updated model name
        // Alternative models to try if this doesn't work:
        // "gemini-1.5-pro" - More capable but slower
        // "gemini-1.0-pro" - Older but stable model
        // "gemini-1.0-pro-001" - Alternative version
        apiKey = ApiKey
    )

    fun sendMessage(question:String){
        if (question.isBlank()) return

        viewModelScope.launch {
            try {
                isLoading.value = true
                
                // Add user message first
                MessageList.add(ChatMessageModel(question,"user"))
                
                // Use generateContent instead of startChat for simpler implementation
                val response = generativeModel.generateContent(question)
                
                // Check if response is valid
                if (response != null && response.text != null) {
                    MessageList.add(ChatMessageModel(response.text.toString(),"model"))
                } else {
                    // Handle null response
                    MessageList.add(ChatMessageModel("Sorry, I couldn't generate a response. Please try again.", "model"))
                }
                
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.message}", e)
                
                // Handle specific error cases
                val errorMessage = when {
                    e.message?.contains("API key") == true -> "Invalid API key. Please check your Google AI Studio API key."
                    e.message?.contains("model") == true -> "Model not available. Please check the model name."
                    e.message?.contains("network") == true -> "Network error. Please check your internet connection."
                    e.message?.contains("quota") == true -> "API quota exceeded. Please try again later."
                    else -> "Sorry, an error occurred: ${e.message}"
                }
                
                MessageList.add(ChatMessageModel(errorMessage, "model"))
            } finally {
                isLoading.value = false
            }
        }
    }
}