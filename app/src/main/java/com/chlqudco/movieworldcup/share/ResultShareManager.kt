package com.chlqudco.movieworldcup.share

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.chlqudco.movieworldcup.domain.TasteSummary
import com.chlqudco.movieworldcup.domain.TournamentSession
import java.io.File
import java.io.FileOutputStream

object ResultShareManager {
    fun share(
        context: Context,
        session: TournamentSession,
        summary: TasteSummary
    ): Boolean = runCatching {
        val champion = checkNotNull(session.champion)
        val bitmap = createBitmap(1080, 1350)
        val canvas = Canvas(bitmap)
        drawBackground(canvas)

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 200, 87)
            textSize = 34f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            letterSpacing = 0.08f
            textAlign = Paint.Align.CENTER
        }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 82f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val championPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 72f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(213, 210, 218)
            textSize = 38f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("MOVIE WORLD CUP", 540f, 145f, labelPaint)
        canvas.drawText("나의 최애 영화", 540f, 260f, titlePaint)

        val trophyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 79, 94)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(170f, 355f, 910f, 785f), 48f, 48f, trophyPaint)

        val rankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 200, 87)
            textSize = 108f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("1", 540f, 505f, rankPaint)
        canvas.drawText("WINNER", 540f, 575f, labelPaint)
        drawCenteredText(canvas, champion.title, championPaint, 540f, 675f, 740f, 2)
        canvas.drawText(champion.releaseYear, 540f, 750f, bodyPaint)

        val modeName = session.genreName ?: session.mode.displayName
        canvas.drawText("${session.size}강 · $modeName", 540f, 900f, labelPaint)
        val genres = summary.favoriteGenres.joinToString(" · ").ifBlank { "취향 분석 완료" }
        canvas.drawText(genres, 540f, 990f, bodyPaint)
        canvas.drawText("선호 시대  ${summary.favoriteDecade}", 540f, 1060f, bodyPaint)

        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(80, 255, 255, 255)
            strokeWidth = 2f
        }
        canvas.drawLine(180f, 1150f, 900f, 1150f, dividerPaint)
        canvas.drawText("영화 이상형 월드컵", 540f, 1235f, labelPaint)

        val directory = File(context.cacheDir, "shared_results").apply { mkdirs() }
        val file = File(directory, "movie_world_cup_${session.id}.png")
        FileOutputStream(file).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
        }
        bitmap.recycle()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "내 영화 이상형 월드컵 우승작은 ${champion.title}!")
            clipData = ClipData.newUri(context.contentResolver, "영화 월드컵 결과", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, "결과 공유하기").apply {
            if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }.isSuccess

    private fun drawBackground(canvas: Canvas) {
        val background = Paint().apply {
            shader = LinearGradient(
                0f,
                0f,
                1080f,
                1350f,
                intArrayOf(
                    Color.rgb(9, 10, 15),
                    Color.rgb(35, 15, 24),
                    Color.rgb(9, 10, 15)
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, 1080f, 1350f, background)
    }

    private fun drawCenteredText(
        canvas: Canvas,
        text: String,
        paint: Paint,
        centerX: Float,
        centerY: Float,
        maxWidth: Float,
        maxLines: Int
    ) {
        val lines = mutableListOf<String>()
        var current = ""
        for (character in text) {
            val candidate = current + character
            if (paint.measureText(candidate) <= maxWidth || current.isEmpty()) {
                current = candidate
            } else {
                lines += current.trim()
                current = character.toString()
            }
        }
        if (current.isNotBlank()) lines += current.trim()
        val visibleLines = lines.take(maxLines).ifEmpty { listOf(text.take(18)) }
        val lineHeight = paint.textSize * 1.08f
        val startY = centerY - ((visibleLines.size - 1) * lineHeight / 2f)
        visibleLines.forEachIndexed { index, line ->
            val displayed = if (index == maxLines - 1 && lines.size > maxLines) "$line…" else line
            canvas.drawText(displayed, centerX, startY + index * lineHeight, paint)
        }
    }
}
