package com.example.quranapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(
    viewModel: QuranViewModel,
    onOpenSurah: (Int) -> Unit,
    onOpenSearch: () -> Unit
) {
    val state by viewModel.surahList.collectAsState()

    LaunchedEffect(Unit) {
        if (state.surahs.isEmpty()) viewModel.loadSurahList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("فهرست سوره‌ها") },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Default.Search, contentDescription = "جستجو")
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

        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(state.surahs) { surah ->
                ListItem(
                    headlineContent = { Text(surah.surahNameFa) },
                    supportingContent = { Text("${surah.ayahCount} آیه") },
                    leadingContent = {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(surah.surahNumber.toString(), style = MaterialTheme.typography.labelLarge)
                        }
                    },
                    modifier = Modifier.clickable { onOpenSurah(surah.surahNumber) }
                )
                HorizontalDivider()
            }
        }
    }
}
