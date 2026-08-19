package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chlqudco.movieworldcup.domain.PlayHistory
import com.chlqudco.movieworldcup.ui.components.EmptyState
import com.chlqudco.movieworldcup.ui.components.MetaPill
import com.chlqudco.movieworldcup.ui.components.MoviePoster
import com.chlqudco.movieworldcup.ui.components.ScreenHeader
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    history: List<PlayHistory>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(title = "플레이 기록", onBack = onBack)
        if (history.isEmpty()) {
            EmptyState(
                title = "아직 기록이 없어요",
                body = "월드컵을 끝까지 완주하면 우승 영화와 취향 요약이 저장됩니다.",
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "${history.size}번의 취향 발견",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "최근 완주한 순서로 기록됩니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CinemaMuted
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
                items(history, key = PlayHistory::tournamentId) { item ->
                    HistoryCard(item)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(history: PlayHistory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CinemaSurface
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePoster(
                movie = history.champion,
                modifier = Modifier.size(width = 78.dp, height = 112.dp),
                cornerRadius = 13
            )
            Spacer(Modifier.width(15.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetaPill("WINNER", emphasized = true)
                    Text(
                        text = formatDate(history.playedAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                }
                Text(
                    text = history.champion.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${history.size}강 · ${history.modeName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted
                )
                val taste = (history.favoriteGenres.take(2) + history.favoriteDecade)
                    .filter(String::isNotBlank)
                    .joinToString(" · ")
                if (taste.isNotBlank()) {
                    Text(
                        text = taste,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String = runCatching {
    DateTimeFormatter.ofPattern("yyyy.MM.dd")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(timestamp))
}.getOrDefault("")
