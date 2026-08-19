package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chlqudco.movieworldcup.R
import com.chlqudco.movieworldcup.ui.components.BrandMark
import com.chlqudco.movieworldcup.ui.components.ScreenHeader
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaOutline
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = modifier) {
        ScreenHeader(title = "앱 정보", onBack = onBack)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            BrandMark()
            Text(
                text = "선택은 가볍게,\n취향 발견은 선명하게.",
                style = MaterialTheme.typography.headlineLarge,
                color = CinemaWhite
            )
            Text(
                text = "영화 포스터 두 장 중 하나를 반복 선택해 나만의 최애 영화를 찾는 토너먼트 게임입니다.",
                style = MaterialTheme.typography.bodyLarge,
                color = CinemaMuted
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = CinemaSurface,
                border = BorderStroke(1.dp, CinemaOutline)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text("영화 데이터 제공", style = MaterialTheme.typography.titleLarge)
                    AsyncImage(
                        model = R.raw.tmdb_logo,
                        contentDescription = "TMDB 로고",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                    Text(
                        text = "TMDB 웹사이트 열기  →",
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://www.themoviedb.org")
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = CinemaGold
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = CinemaSurface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("데이터 안내", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "진행 중인 대진표와 플레이 기록은 기기에 저장됩니다. 영화 후보와 포스터는 TMDB에서 불러옵니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CinemaMuted
                    )
                }
            }

            Text(
                text = "Movie World Cup · Version 1.0",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
