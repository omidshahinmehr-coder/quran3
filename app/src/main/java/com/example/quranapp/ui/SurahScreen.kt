package com.example.quranapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SurahScreen(
    viewModel: QuranViewModel,
    surahNumber: Int,
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenTafsir: (globalAyahId: Int, surahName: String, ayahNumber: Int) -> Unit
) {
    val state by viewModel.surah.collectAsState()

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.surahName.ifBlank { "سوره" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "فهرست سوره‌ها")
                    }
                },
                actions = {
                    Text(
                        "نمایش ترجمه",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Switch(
                        checked = state.showTranslation,
                        onCheckedChange = { viewModel.toggleTranslationVisible() }
                    )
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

        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            items(state.ayat) { ayah ->
                val hasTafsir = ayah.globalAyahId in state.ayahIdsWithTafsir
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                if (hasTafsir) {
                                    onOpenTafsir(ayah.globalAyahId, state.surahName, ayah.ayahNumber)
                                }
                            }
                        )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "﴾${ayah.ayahNumber}﴿  ${ayah.textArabic}",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.showTranslation) {
                            state.translations[ayah.globalAyahId]?.let { tr ->
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    tr.textFa,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        if (hasTafsir) {
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "برای مشاهده تفسیر البرهان، آیه را نگه دارید",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
