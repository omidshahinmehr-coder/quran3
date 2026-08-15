package com.example.quranapp.data

data class AyahEntity(
    val globalAyahId: Int,
    val surahNumber: Int,
    val surahNameFa: String,
    val ayahNumber: Int,
    val textArabic: String
)

data class TranslationEntity(
    val globalAyahId: Int,
    val translator: String,
    val textFa: String
)

data class TafsirEntity(
    val id: Long,
    val source: String,
    val surahNumber: Int,
    val startAyahId: Int,
    val endAyahId: Int,
    val textFa: String
)

data class SearchResult(
    val globalAyahId: Int,
    val surahNumber: Int,
    val surahNameFa: String,
    val ayahNumber: Int,
    val snippet: String,
    val kind: String // "quran" | "translation" | "tafsir"
)

data class SurahInfo(
    val surahNumber: Int,
    val surahNameFa: String,
    val ayahCount: Int
)
