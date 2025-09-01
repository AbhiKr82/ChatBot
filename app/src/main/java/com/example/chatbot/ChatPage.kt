package com.example.chatbot

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.ui.theme.ModelColr
import com.example.chatbot.ui.theme.Purple40
import com.example.chatbot.ui.theme.UserColor


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun chatpage(modifier: Modifier=Modifier,viewModel: ChatViewModel){

    Column(modifier = modifier.padding(10.dp)) {
        AppHeader()
        MessageList(modifier= Modifier.weight(1f), messageList = viewModel.MessageList)
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            },
            isLoading = viewModel.isLoading.value
        )
    }
}
@Composable
fun AppHeader(){
    Box(
        modifier = Modifier.fillMaxWidth().background(shape = RoundedCornerShape(10.dp), color = Color.Transparent),
        contentAlignment = Alignment.Center


    ){
       Column(
           modifier = Modifier.fillMaxWidth().padding(10.dp),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Row(
               horizontalArrangement = Arrangement.SpaceBetween
           ) {

               Icon(
                   painter = painterResource(R.drawable.baseline_question_answer_24),
                   contentDescription = "",
                   modifier = Modifier.size(30.dp)

               )
               Spacer(Modifier.width(10.dp))
               Text("ChatBot", fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Monospace)

           }
       }


    }
}

@Composable
fun MessageInput(onMessageSend :(String) -> Unit, isLoading: Boolean = false){

    val context= LocalContext.current
    var message by remember { mutableStateOf("") }
    Row (
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        OutlinedTextField(
            value = message, 
            onValueChange = {message=it},
            label = { Text(text = "Ask me anything ?")},
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        )
        IconButton(
            onClick = {
                if(message.isEmpty()){
                    Toast.makeText(context,"Enter Something..",Toast.LENGTH_SHORT).show()
                }else{
                    onMessageSend(message)
                    message=""
                }
            },
            enabled = !isLoading && message.isNotEmpty()
        ) {
            if (isLoading) {
                // Show loading indicator
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageList(modifier: Modifier=Modifier,messageList:List<ChatMessageModel>){

        LazyColumn(
            modifier=modifier,
            reverseLayout = true // message will start printing from bottom
        ) {
            items(messageList.reversed()){
                MessageLayout(it)
            }
        }


}

@Composable
fun MessageLayout(messageModel: ChatMessageModel){

    val ismodel = messageModel.role=="model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.align(if (ismodel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if(ismodel) 8.dp else 70.dp,
                        end= if(ismodel) 70.dp else 8.dp,
                        top=8.dp,
                        bottom = 8.dp
                    )
                    .background(if(ismodel) UserColor else ModelColr, shape = RoundedCornerShape(10.dp))
                    .padding(15.dp),


            ) {
                SelectionContainer {
                    Text(text = messageModel.message, fontWeight = FontWeight.W500)
                }

            }
        }
    }
}