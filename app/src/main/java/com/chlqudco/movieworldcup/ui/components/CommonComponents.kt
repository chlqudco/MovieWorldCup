package com.chlqudco.movieworldcup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.chlqudco.movieworldcup.domain.MovieCandidate
import com.chlqudco.movieworldcup.ui.theme.CinemaBlack
import com.chlqudco.movieworldcup.ui.theme.CinemaGold
import com.chlqudco.movieworldcup.ui.theme.CinemaMuted
import com.chlqudco.movieworldcup.ui.theme.CinemaRed
import com.chlqudco.movieworldcup.ui.theme.CinemaSurface
import com.chlqudco.movieworldcup.ui.theme.CinemaSurfaceHigh
import com.chlqudco.movieworldcup.ui.theme.CinemaWhite

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF170C13),
                        CinemaBlack,
                        CinemaBlack
                    )
                )
            ),
        content = content
    )
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onBack) {
            Text("←", fontSize = 24.sp, color = CinemaWhite)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionLabel, color = CinemaMuted)
            }
        } else {
            Spacer(Modifier.padding(horizontal = 32.dp))
        }
    }
}

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CinemaRed,
            contentColor = CinemaWhite,
            disabledContainerColor = CinemaSurfaceHigh,
            disabledContentColor = CinemaMuted
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SectionTitle(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted
            )
        }
    }
}

@Composable
fun MetaPill(
    text: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (emphasized) CinemaRed.copy(alpha = 0.18f) else CinemaSurfaceHigh
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (emphasized) CinemaRed else CinemaMuted
        )
    }
}

@Composable
fun MoviePoster(
    movie: MovieCandidate,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 18
) {
    var failed by remember(movie.posterUrl) { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(CinemaSurfaceHigh),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (failed) "포스터를 불러오지 못했어요" else "포스터 로딩 중",
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                color = CinemaWhite,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (!failed) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "${movie.title} 포스터",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { failed = true }
            )
        }
    }
}

@Composable
fun BrandMark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = CinemaRed
        ) {
            Text(
                text = "MW",
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                color = CinemaWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }
        Text(
            text = "  MOVIE WORLD CUP",
            style = MaterialTheme.typography.labelLarge,
            color = CinemaGold,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CinemaSurface
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("◇", color = CinemaRed, fontSize = 34.sp)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
