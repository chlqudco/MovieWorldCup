package com.chlqudco.movieworldcup.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.movieworldcup.ui.components.PrimaryActionButton
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun LoadingScreen(
    isLoading: Boolean,
    loadingMessage: String,
    errorMessage: String?,
    suggestedSize: Int?,
    onRetry: () -> Unit,
    onSuggestedSize: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CinemaRed,
                strokeWidth = 5.dp
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = loadingMessage,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "포스터가 있는 영화를 모으고\n중복 없이 대진표를 만들고 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = "DISCOVER  ·  FILTER  ·  SHUFFLE",
                style = MaterialTheme.typography.labelLarge,
                color = CinemaRed,
                letterSpacing = 1.6.sp
            )
        } else {
            Text("!", color = CinemaRed, fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "후보를 준비하지 못했어요",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            if (suggestedSize != null) {
                PrimaryActionButton(
                    text = "${suggestedSize}강으로 시작",
                    onClick = { onSuggestedSize(suggestedSize) }
                )
                Spacer(Modifier.height(10.dp))
            }
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("다시 시도", color = CinemaWhite)
            }
            TextButton(onClick = onBack) {
                Text("설정으로 돌아가기", color = CinemaMuted)
            }
        }
    }
}
