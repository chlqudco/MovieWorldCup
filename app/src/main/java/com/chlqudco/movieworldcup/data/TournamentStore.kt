package com.chlqudco.movieworldcup.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chlqudco.movieworldcup.domain.PlayHistory
import com.chlqudco.movieworldcup.domain.TournamentSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.movieWorldCupDataStore by preferencesDataStore(name = "movie_world_cup")

class TournamentStore(context: Context) {
    private val dataStore = context.applicationContext.movieWorldCupDataStore
    private val gson = Gson()
    private val historyType = object : TypeToken<List<PlayHistory>>() {}.type

    suspend fun loadSession(): TournamentSession? {
        val json = dataStore.data.first()[activeSessionKey] ?: return null
        return runCatching { gson.fromJson(json, TournamentSession::class.java) }.getOrNull()
    }

    suspend fun loadHistory(): List<PlayHistory> {
        val json = dataStore.data.first()[historyKey] ?: return emptyList()
        return parseHistory(json)
    }

    suspend fun saveSession(session: TournamentSession) {
        dataStore.edit { preferences ->
            preferences[activeSessionKey] = gson.toJson(session)
        }
    }

    suspend fun completeSession(session: TournamentSession, history: PlayHistory) {
        dataStore.edit { preferences ->
            val currentHistory = preferences[historyKey]?.let(::parseHistory).orEmpty()
            val updatedHistory = (listOf(history) + currentHistory.filterNot {
                it.tournamentId == history.tournamentId
            }).take(maxHistoryCount)
            preferences[activeSessionKey] = gson.toJson(session)
            preferences[historyKey] = gson.toJson(updatedHistory, historyType)
        }
    }

    suspend fun restoreCompletedSession(session: TournamentSession, tournamentId: String) {
        dataStore.edit { preferences ->
            val currentHistory = preferences[historyKey]?.let(::parseHistory).orEmpty()
            preferences[activeSessionKey] = gson.toJson(session)
            preferences[historyKey] = gson.toJson(
                currentHistory.filterNot { it.tournamentId == tournamentId },
                historyType
            )
        }
    }

    suspend fun clearSession() {
        dataStore.edit { it.remove(activeSessionKey) }
    }

    private fun parseHistory(json: String): List<PlayHistory> = runCatching {
        gson.fromJson<List<PlayHistory>>(json, historyType).orEmpty()
    }.getOrDefault(emptyList())

    companion object {
        private val activeSessionKey = stringPreferencesKey("active_session")
        private val historyKey = stringPreferencesKey("play_history")
        private const val maxHistoryCount = 50
    }
}
