package com.example.enlearn.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun ClickableLoginText(
    onLoginClicked: () -> Unit,
    defaultTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = defaultTextColor, fontSize = 14.sp)) {
            append("Already an account? ")
        }
        pushStringAnnotation(tag = "LOGIN", annotation = "login")
        withStyle(style = SpanStyle(color = linkColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
            append("Log in")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                .firstOrNull()?.let {
                    onLoginClicked()
                }
        }
    )
}
