package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.movieworldcup.domain.PlayHistory
import com.chlqudco.movieworldcup.domain.TournamentSession
import com.chlqudco.movieworldcup.ui.components.BrandMark
import com.chlqudco.movieworldcup.ui.components.MetaPill
import com.chlqudco.movieworldcup.ui.components.MoviePoster
import com.chlqudco.movieworldcup.ui.components.PrimaryActionButton
import com.chlqudco.movieworldcup.ui.components.SectionTitle
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaOutline
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaSurfaceHigh
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun HomeScreen(
    session: TournamentSession?,
    history: List<PlayHistory>,
    onStart: () -> Unit,
    onQuickStart: () -> Unit,
    onResume: () -> Unit,
    onGenreStart: () -> Unit,
    onHistory: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        BrandMark()

        Column {
            Text(
                text = "둘 중 하나,\n끝까지 고르면",
                style = MaterialTheme.typography.displayLarge,
                color = CinemaWhite
            )
            Text(
                text = "취향이 보인다.",
                style = MaterialTheme.typography.displayLarge,
                color = CinemaRed
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "포스터 두 장 중 더 끌리는 영화를 고르세요.\n16강부터 결승까지, 선택은 단순하게.",
                style = MaterialTheme.typography.bodyLarge,
                color = CinemaMuted
            )
        }

        PrimaryActionButton(text = "월드컵 시작", onClick = onStart)

        if (session != null) {
            ContinueCard(session = session, onClick = onResume)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("빠른 시작", "20초 안에 첫 매치를 시작해 보세요")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickModeCard(
                    eyebrow = "POPULAR",
                    title = "인기 영화\n16강",
                    accent = CinemaRed,
                    onClick = onQuickStart,
                    modifier = Modifier.weight(1f)
                )
                QuickModeCard(
                    eyebrow = "GENRE",
                    title = "장르 골라\n시작",
                    accent = CinemaGold,
                    onClick = onGenreStart,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionTitle("최근 우승작")
                if (history.isNotEmpty()) {
                    TextButton(onClick = onHistory) {
                        Text("전체 보기", color = CinemaMuted)
                    }
                }
            }
            if (history.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CinemaSurface
                ) {
                    Text(
                        text = "첫 월드컵을 완주하면 우승 영화가 여기에 쌓여요.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                }
            } else {
                history.take(3).forEach { item ->
                    RecentWinnerCard(history = item)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onHistory) {
                Text("플레이 기록", color = CinemaMuted)
            }
            Text("·", modifier = Modifier.padding(top = 12.dp), color = CinemaOutline)
            TextButton(onClick = onAbout) {
                Text("앱 정보", color = CinemaMuted)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ContinueCard(
    session: TournamentSession,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = CinemaSurface,
        border = BorderStroke(1.dp, CinemaRed.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(15.dp),
                color = CinemaRed.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (session.completed) "★" else "▶", color = CinemaRed, fontSize = 20.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (session.completed) "마지막 결과 다시 보기" else "진행 중인 월드컵",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (session.completed) {
                        session.champion?.title.orEmpty()
                    } else {
                        "${session.roundLabel()} ${session.matchIndex + 1}/${session.matchCount()} · ${session.genreName ?: "인기 영화"}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text("→", color = CinemaRed, fontSize = 22.sp)
        }
    }
}

@Composable
private fun QuickModeCard(
    eyebrow: String,
    title: String,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1.25f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = CinemaSurface
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                letterSpacing = 1.1.sp
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = CinemaWhite
            )
        }
    }
}

@Composable
private fun RecentWinnerCard(history: PlayHistory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CinemaSurface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePoster(
                movie = history.champion,
                modifier = Modifier.size(width = 62.dp, height = 88.dp),
                cornerRadius = 12
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                MetaPill("WINNER", emphasized = true)
                Spacer(Modifier.height(8.dp))
                Text(
                    history.champion.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${history.size}강 · ${history.modeName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted
                )
            }
        }
    }
}
