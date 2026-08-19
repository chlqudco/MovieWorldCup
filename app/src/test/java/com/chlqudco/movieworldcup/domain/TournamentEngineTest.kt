package com.chlqudco.movieworldcup.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TournamentEngineTest {
    private val engine = TournamentEngine()

    @Test
    fun sixteenMovieTournamentCompletesAfterFifteenChoices() {
        var session = createSession(16)

        while (!session.completed) {
            val winnerId = checkNotNull(session.currentPair()).first.id
            session = engine.select(session, winnerId).session
        }

        assertTrue(session.completed)
        assertNotNull(session.champion)
        assertEquals(15, session.selectedMovieIds.size)
    }

    @Test
    fun thirtyTwoMovieTournamentCompletesAfterThirtyOneChoices() {
        var session = createSession(32)

        while (!session.completed) {
            val winnerId = checkNotNull(session.currentPair()).second.id
            session = engine.select(session, winnerId).session
        }

        assertTrue(session.completed)
        assertEquals(31, session.selectedMovieIds.size)
    }

    @Test
    fun finalMatchOfRoundCreatesNextRoundInWinnerOrder() {
        var session = createSession(16)
        val winners = mutableListOf<Int>()

        repeat(8) {
            val winnerId = checkNotNull(session.currentPair()).first.id
            winners += winnerId
            session = engine.select(session, winnerId).session
        }

        assertEquals(8, session.roundSize)
        assertEquals(winners, session.roundMovies.map(MovieCandidate::id))
        assertEquals(0, session.matchIndex)
        assertTrue(session.winners.isEmpty())
    }

    @Test
    fun undoRestoresMatchBeforeRoundTransition() {
        var session = createSession(16)

        repeat(8) {
            val winnerId = checkNotNull(session.currentPair()).first.id
            session = engine.select(session, winnerId).session
        }

        val restored = checkNotNull(engine.undo(session))

        assertEquals(16, restored.roundSize)
        assertEquals(7, restored.matchIndex)
        assertEquals(7, restored.winners.size)
        assertEquals(7, restored.selectedMovieIds.size)
        assertFalse(restored.completed)
    }

    @Test
    fun sameSeedCreatesSameBracket() {
        val candidates = movies(32)
        val first = engine.create(
            candidates = candidates,
            genres = emptyList(),
            mode = TournamentMode.POPULAR,
            size = 16,
            seed = 20260819L,
            sessionId = "first"
        )
        val second = engine.create(
            candidates = candidates,
            genres = emptyList(),
            mode = TournamentMode.POPULAR,
            size = 16,
            seed = 20260819L,
            sessionId = "second"
        )

        assertEquals(
            first.roundMovies.map(MovieCandidate::id),
            second.roundMovies.map(MovieCandidate::id)
        )
    }

    private fun createSession(size: Int): TournamentSession = engine.create(
        candidates = movies(size),
        genres = emptyList(),
        mode = TournamentMode.POPULAR,
        size = size,
        seed = 42L,
        sessionId = "test-$size",
        startedAt = 1L
    )

    private fun movies(size: Int): List<MovieCandidate> = (1..size).map { id ->
        MovieCandidate(
            id = id,
            title = "영화 $id",
            posterPath = "/$id.jpg",
            releaseDate = "2020-01-01",
            genreIds = listOf(18)
        )
    }
}
