package com.example.quranapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.data.AyahEntity
import com.example.quranapp.data.QuranRepository
import com.example.quranapp.data.SearchResult
import com.example.quranapp.data.SurahInfo
import com.example.quranapp.data.TafsirEntity
import com.example.quranapp.data.TranslationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SurahListUiState(
    val surahs: List<SurahInfo> = emptyList(),
    val loading: Boolean = true
)

data class SurahUiState(
    val surahNumber: Int = 0,
    val surahName: String = "",
    val ayat: List<AyahEntity> = emptyList(),
    val translations: Map<Int, TranslationEntity> = emptyMap(),
    val ayahIdsWithTafsir: Set<Int> = emptySet(),
    val showTranslation: Boolean = true,
    val loading: Boolean = true
)

data class TafsirUiState(
    val surahName: String = "",
    val ayahNumber: Int = 0,
    val entries: List<TafsirEntity> = emptyList(),
    val loading: Boolean = true
)

data class SearchUiState(
    val query: String = "",
    val includeQuran: Boolean = true,
    val includeTranslation: Boolean = true,
    val includeTafsir: Boolean = true,
    val results: List<SearchResult> = emptyList(),
    val loading: Boolean = false
)

class QuranViewModel(private val repo: QuranRepository) : ViewModel() {

    private val _surahList = MutableStateFlow(SurahListUiState())
    val surahList: StateFlow<SurahListUiState> = _surahList.asStateFlow()

    private val _surah = MutableStateFlow(SurahUiState())
    val surah: StateFlow<SurahUiState> = _surah.asStateFlow()

    private val _tafsir = MutableStateFlow(TafsirUiState())
    val tafsir: StateFlow<TafsirUiState> = _tafsir.asStateFlow()

    private val _search = MutableStateFlow(SearchUiState())
    val search: StateFlow<SearchUiState> = _search.asStateFlow()

    fun loadSurahList() = viewModelScope.launch {
        _surahList.value = SurahListUiState(loading = true)
        val list = repo.getSurahList()
        _surahList.value = SurahListUiState(list, loading = false)
    }

    fun loadSurah(surahNumber: Int) = viewModelScope.launch {
        val keepTranslationPref = _surah.value.showTranslation
        _surah.value = SurahUiState(surahNumber = surahNumber, showTranslation = keepTranslationPref, loading = true)
        val ayat = repo.getSurah(surahNumber)
        val translations = if (ayat.isNotEmpty())
            repo.getTranslations(ayat.first().globalAyahId, ayat.last().globalAyahId)
                .associateBy { it.globalAyahId }
        else emptyMap()
        val tafsirIds = repo.getAyahIdsWithTafsir(surahNumber)
        _surah.value = SurahUiState(
            surahNumber = surahNumber,
            surahName = ayat.firstOrNull()?.surahNameFa ?: "",
            ayat = ayat,
            translations = translations,
            ayahIdsWithTafsir = tafsirIds,
            showTranslation = keepTranslationPref,
            loading = false
        )
    }

    fun toggleTranslationVisible() {
        _surah.value = _surah.value.copy(showTranslation = !_surah.value.showTranslation)
    }

    fun loadTafsir(globalAyahId: Int, surahName: String, ayahNumber: Int) = viewModelScope.launch {
        _tafsir.value = TafsirUiState(surahName = surahName, ayahNumber = ayahNumber, loading = true)
        val entries = repo.getTafsirForAyah(globalAyahId)
        _tafsir.value = TafsirUiState(surahName, ayahNumber, entries, loading = false)
    }

    fun updateQuery(q: String) {
        _search.value = _search.value.copy(query = q)
    }

    fun toggleFilter(kind: String) {
        val s = _search.value
        _search.value = when (kind) {
            "quran" -> s.copy(includeQuran = !s.includeQuran)
            "translation" -> s.copy(includeTranslation = !s.includeTranslation)
            "tafsir" -> s.copy(includeTafsir = !s.includeTafsir)
            else -> s
        }
    }

    fun runSearch() = viewModelScope.launch {
        val s = _search.value
        if (s.query.isBlank()) {
            _search.value = s.copy(results = emptyList(), loading = false)
            return@launch
        }
        _search.value = s.copy(loading = true)
        val results = repo.search(s.query, s.includeQuran, s.includeTranslation, s.includeTafsir)
        // اگر کاربر در حین جستجو متن را عوض کرده، این نتیجه‌ی قدیمی را نادیده بگیر
        if (_search.value.query == s.query) {
            _search.value = _search.value.copy(results = results, loading = false)
        }
    }
}
