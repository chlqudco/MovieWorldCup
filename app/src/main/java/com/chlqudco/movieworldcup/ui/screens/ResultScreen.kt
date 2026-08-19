package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.movieworldcup.domain.TasteSummary
import com.chlqudco.movieworldcup.domain.TournamentSession
import com.chlqudco.movieworldcup.ui.components.MetaPill
import com.chlqudco.movieworldcup.ui.components.MoviePoster
import com.chlqudco.movieworldcup.ui.components.PrimaryActionButton
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaOutline
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaSurfaceHigh
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun ResultScreen(
    session: TournamentSession,
    summary: TasteSummary,
    onShare: () -> Unit,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val champion = session.champion ?: return
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onHome) {
                Text("← 홈", color = CinemaMuted)
            }
            MetaPill("TOURNAMENT COMPLETE", emphasized = true)
            Spacer(Modifier.width(44.dp))
        }

        Spacer(Modifier.height(18.dp))
        Text(
            text = "당신의 최애 영화",
            style = MaterialTheme.typography.bodyLarge,
            color = CinemaGold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = "WINNER",
            style = MaterialTheme.typography.displayLarge,
            color = CinemaWhite,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(22.dp))

        Box(contentAlignment = Alignment.TopCenter) {
            Surface(
                modifier = Modifier
                    .padding(top = 18.dp)
                    .size(width = 226.dp, height = 332.dp),
                shape = RoundedCornerShape(26.dp),
                color = CinemaSurface,
                border = BorderStroke(3.dp, CinemaGold)
            ) {
                MoviePoster(
                    movie = champion,
                    cornerRadius = 23
                )
            }
            Surface(
                shape = CircleShape,
                color = CinemaGold
            ) {
                Text(
                    text = "1",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = CinemaSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        Text(
            text = champion.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${champion.releaseYear} · ${session.size}강 우승",
            style = MaterialTheme.typography.bodyLarge,
            color = CinemaMuted
        )

        Spacer(Modifier.height(30.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CinemaSurface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("나의 취향 리포트", style = MaterialTheme.typography.titleLarge)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "선호 장르",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        summary.favoriteGenres.ifEmpty { listOf("취향 발견") }.forEach { genre ->
                            MetaPill(genre, emphasized = true)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TasteMetric(
                        label = "선호 시대",
                        value = summary.favoriteDecade,
                        modifier = Modifier.weight(1f)
                    )
                    TasteMetric(
                        label = "선택 횟수",
                        value = "${summary.choiceCount}회",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        PrimaryActionButton(text = "결과 이미지 공유", onClick = onShare)
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, CinemaOutline)
        ) {
            Text("새 월드컵 시작", color = CinemaWhite)
        }
        TextButton(onClick = onUndo) {
            Text("결승 선택 다시 하기", color = CinemaMuted)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TasteMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CinemaSurfaceHigh
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = CinemaMuted)
            Spacer(Modifier.height(5.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = CinemaWhite)
        }
    }
}
