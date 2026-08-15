package com.example.quranapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirScreen(
    viewModel: QuranViewModel,
    globalAyahId: Int,
    surahName: String,
    ayahNumber: Int,
    onBack: () -> Unit
) {
    val state by viewModel.tafsir.collectAsState()

    LaunchedEffect(globalAyahId) {
        viewModel.loadTafsir(globalAyahId, surahName, ayahNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفسیر البرهان — $surahName، آیه $ayahNumber") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (state.entries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("تفسیری برای این آیه ثبت نشده است.")
            }
            return@Scaffold
        }

        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(state.entries) { entry ->
                Card(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(entry.source, style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(entry.textFa, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
