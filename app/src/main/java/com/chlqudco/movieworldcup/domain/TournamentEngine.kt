package com.chlqudco.movieworldcup.domain

import java.util.UUID
import kotlin.random.Random

data class SelectionOutcome(
    val session: TournamentSession,
    val newRoundLabel: String? = null
)

class TournamentEngine {
    fun create(
        candidates: List<MovieCandidate>,
        genres: List<MovieGenre>,
        mode: TournamentMode,
        size: Int,
        seed: Long,
        genreId: Int? = null,
        genreName: String? = null,
        sessionId: String = UUID.randomUUID().toString(),
        startedAt: Long = System.currentTimeMillis()
    ): TournamentSession {
        require(size == 16 || size == 32)
        require(candidates.distinctBy(MovieCandidate::id).size >= size)
        val seededCandidates = candidates
            .distinctBy(MovieCandidate::id)
            .shuffled(Random(seed))
            .take(size)

        return TournamentSession(
            id = sessionId,
            mode = mode,
            size = size,
            seed = seed,
            genreId = genreId,
            genreName = genreName,
            allCandidates = seededCandidates,
            genres = genres,
            roundMovies = seededCandidates,
            roundSize = size,
            startedAt = startedAt
        )
    }

    fun select(session: TournamentSession, movieId: Int): SelectionOutcome {
        check(!session.completed)
        val pair = checkNotNull(session.currentPair())
        val winner = when (movieId) {
            pair.first.id -> pair.first
            pair.second.id -> pair.second
            else -> error("현재 경기에 포함되지 않은 영화입니다.")
        }
        val checkpoint = UndoCheckpoint(
            roundMovies = session.roundMovies,
            winners = session.winners,
            matchIndex = session.matchIndex,
            roundSize = session.roundSize,
            selectedMovieIds = session.selectedMovieIds
        )
        val nextWinners = session.winners + winner
        val selectedIds = session.selectedMovieIds + winner.id
        val isLastMatch = session.matchIndex == session.matchCount() - 1

        if (session.roundMovies.size == 2) {
            return SelectionOutcome(
                session.copy(
                    winners = nextWinners,
                    selectedMovieIds = selectedIds,
                    champion = winner,
                    completed = true,
                    undoCheckpoint = checkpoint
                )
            )
        }

        if (isLastMatch) {
            val nextSession = session.copy(
                roundMovies = nextWinners,
                winners = emptyList(),
                matchIndex = 0,
                roundSize = nextWinners.size,
                selectedMovieIds = selectedIds,
                undoCheckpoint = checkpoint
            )
            return SelectionOutcome(nextSession, nextSession.roundLabel())
        }

        return SelectionOutcome(
            session.copy(
                winners = nextWinners,
                matchIndex = session.matchIndex + 1,
                selectedMovieIds = selectedIds,
                undoCheckpoint = checkpoint
            )
        )
    }

    fun undo(session: TournamentSession): TournamentSession? {
        val checkpoint = session.undoCheckpoint ?: return null
        return session.copy(
            roundMovies = checkpoint.roundMovies,
            winners = checkpoint.winners,
            matchIndex = checkpoint.matchIndex,
            roundSize = checkpoint.roundSize,
            selectedMovieIds = checkpoint.selectedMovieIds,
            champion = null,
            completed = false,
            undoCheckpoint = null
        )
    }
}
