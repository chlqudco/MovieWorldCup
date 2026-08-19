package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.movieworldcup.domain.MovieGenre
import com.chlqudco.movieworldcup.domain.TournamentMode
import com.chlqudco.movieworldcup.ui.components.PrimaryActionButton
import com.chlqudco.movieworldcup.ui.components.ScreenHeader
import com.chlqudco.movieworldcup.ui.components.SectionTitle
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaOutline
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaSurfaceHigh
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun SetupScreen(
    selectedMode: TournamentMode,
    selectedSize: Int,
    selectedGenre: MovieGenre,
    genres: List<MovieGenre>,
    hasActiveSession: Boolean,
    onBack: () -> Unit,
    onModeSelected: (TournamentMode) -> Unit,
    onSizeSelected: (Int) -> Unit,
    onGenreSelected: (MovieGenre) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenHeader(title = "월드컵 설정", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Column {
                Text(
                    text = "어떤 영화로\n붙여볼까요?",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "취향대로 모드와 토너먼트 크기를 선택하세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("게임 모드")
                ModeCard(
                    title = "인기 영화",
                    description = "인지도와 인기를 기준으로 엄선",
                    symbol = "↗",
                    selected = selectedMode == TournamentMode.POPULAR,
                    onClick = { onModeSelected(TournamentMode.POPULAR) }
                )
                ModeCard(
                    title = "장르별",
                    description = "좋아하는 장르 안에서 최애 찾기",
                    symbol = "◎",
                    selected = selectedMode == TournamentMode.GENRE,
                    onClick = { onModeSelected(TournamentMode.GENRE) }
                )
            }

            if (selectedMode == TournamentMode.GENRE) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("장르 선택", "옆으로 밀어 더 많은 장르를 볼 수 있어요")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genres.forEach { genre ->
                            GenreChip(
                                genre = genre,
                                selected = genre.id == selectedGenre.id,
                                onClick = { onGenreSelected(genre) }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionTitle("토너먼트 크기")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SizeCard(
                        size = 16,
                        matchCount = 15,
                        selected = selectedSize == 16,
                        onClick = { onSizeSelected(16) },
                        modifier = Modifier.weight(1f)
                    )
                    SizeCard(
                        size = 32,
                        matchCount = 31,
                        selected = selectedSize == 32,
                        onClick = { onSizeSelected(32) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (hasActiveSession) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CinemaGold.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, CinemaGold.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "새 게임을 시작하면 현재 진행 상태가 새 토너먼트로 교체됩니다.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaGold
                    )
                }
            }

            PrimaryActionButton(
                text = "${selectedSize}강 시작하기",
                onClick = onStart
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    symbol: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) CinemaRed.copy(alpha = 0.12f) else CinemaSurface,
        border = BorderStroke(
            1.dp,
            if (selected) CinemaRed else CinemaOutline
        )
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (selected) CinemaRed else CinemaSurfaceHigh
            ) {
                Text(
                    text = symbol,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    fontSize = 20.sp,
                    color = CinemaWhite
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CinemaMuted
                )
            }
            Text(if (selected) "●" else "○", color = if (selected) CinemaRed else CinemaMuted)
        }
    }
}

@Composable
private fun GenreChip(
    genre: MovieGenre,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) CinemaRed else CinemaSurface,
        border = BorderStroke(1.dp, if (selected) CinemaRed else CinemaOutline)
    ) {
        Text(
            text = genre.name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = CinemaWhite
        )
    }
}

@Composable
private fun SizeCard(
    size: Int,
    matchCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(128.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) CinemaRed.copy(alpha = 0.12f) else CinemaSurface,
        border = BorderStroke(1.dp, if (selected) CinemaRed else CinemaOutline)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${size}강",
                style = MaterialTheme.typography.headlineMedium,
                color = if (selected) CinemaRed else CinemaWhite,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "총 ${matchCount}번 선택",
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted
            )
        }
    }
}
