package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.movieworldcup.domain.MovieCandidate
import com.chlqudco.movieworldcup.domain.TournamentSession
import com.chlqudco.movieworldcup.ui.components.MoviePoster
import com.chlqudco.movieworldcup.ui.theme.CinemaBlack
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaOutline
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun MatchScreen(
    session: TournamentSession,
    selectedMovieId: Int?,
    isSelectionLocked: Boolean,
    roundAnnouncement: String?,
    onSelectMovie: (Int) -> Unit,
    onUndo: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pair = session.currentPair() ?: return
    var detailMovie by remember { mutableStateOf<MovieCandidate?>(null) }
    val overallProgress = session.selectedMovieIds.size.toFloat() / (session.size - 1).coerceAtLeast(1)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onExit) {
                    Text("나가기", color = CinemaMuted)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = session.roundLabel(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${session.matchIndex + 1} / ${session.matchCount()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                }
                TextButton(
                    onClick = onUndo,
                    enabled = session.undoCheckpoint != null && !isSelectionLocked
                ) {
                    Text(
                        "되돌리기",
                        color = if (session.undoCheckpoint != null) CinemaMuted else CinemaOutline
                    )
                }
            }

            LinearProgressIndicator(
                progress = { overallProgress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = CinemaRed,
                trackColor = CinemaSurface
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "더 끌리는 영화를 선택하세요",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = pair,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                transitionSpec = {
                    fadeIn(tween(180)) togetherWith fadeOut(tween(130))
                },
                label = "matchTransition"
            ) { activePair ->
                Box(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MatchMovieCard(
                            movie = activePair.first,
                            selected = selectedMovieId == activePair.first.id,
                            dimmed = selectedMovieId != null && selectedMovieId != activePair.first.id,
                            enabled = !isSelectionLocked,
                            onClick = { onSelectMovie(activePair.first.id) },
                            onLongClick = { detailMovie = activePair.first },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        MatchMovieCard(
                            movie = activePair.second,
                            selected = selectedMovieId == activePair.second.id,
                            dimmed = selectedMovieId != null && selectedMovieId != activePair.second.id,
                            enabled = !isSelectionLocked,
                            onClick = { onSelectMovie(activePair.second.id) },
                            onLongClick = { detailMovie = activePair.second },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                        shape = CircleShape,
                        color = CinemaBlack,
                        border = BorderStroke(2.dp, CinemaRed)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "VS",
                                color = CinemaRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "길게 누르면 줄거리와 평점을 볼 수 있어요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        AnimatedVisibility(
            visible = roundAnnouncement != null,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.92f),
            exit = fadeOut(tween(220)) + scaleOut(targetScale = 1.08f)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = CinemaBlack.copy(alpha = 0.94f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NEXT ROUND",
                        style = MaterialTheme.typography.labelLarge,
                        color = CinemaGold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = roundAnnouncement.orEmpty(),
                        style = MaterialTheme.typography.displayLarge,
                        color = CinemaWhite
                    )
                }
            }
        }
    }

    detailMovie?.let { movie ->
        MovieDetailDialog(movie = movie, onDismiss = { detailMovie = null })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MatchMovieCard(
    movie: MovieCandidate,
    selected: Boolean,
    dimmed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.035f else 1f,
        animationSpec = tween(180),
        label = "selectedScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (dimmed) 0.42f else 1f,
        animationSpec = tween(180),
        label = "selectedAlpha"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) CinemaRed else CinemaOutline,
        animationSpec = tween(180),
        label = "selectedBorder"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(alpha)
            .semantics {
                contentDescription = "${movie.title}, ${movie.releaseYear}. 선택하려면 두 번 탭하세요."
            }
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = CinemaSurface,
        border = BorderStroke(if (selected) 3.dp else 1.dp, borderColor)
    ) {
        Column {
            MoviePoster(
                movie = movie,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                cornerRadius = 19
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 11.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CinemaWhite,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = movie.releaseYear,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted
                )
            }
        }
    }
}

@Composable
private fun MovieDetailDialog(
    movie: MovieCandidate,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(movie.title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(movie.releaseYear, color = CinemaMuted)
                    if (movie.voteAverage > 0.0) {
                        Text("평점 %.1f".format(movie.voteAverage), color = CinemaGold)
                    }
                }
                Text(
                    text = movie.overview.ifBlank { "등록된 줄거리 정보가 없습니다." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = CinemaWhite
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인", color = CinemaRed)
            }
        },
        containerColor = CinemaSurface
    )
}
