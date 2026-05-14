package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shishu_sneh.ui.viewmodels.ChatbotViewModel

@Composable
fun ChatbotScreen(navController: NavController, viewModel: ChatbotViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI Assistant", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Text(msg, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                label = { Text("Ask something...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                if (query.isNotBlank()) {
                    viewModel.sendMessage(query)
                    query = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}
