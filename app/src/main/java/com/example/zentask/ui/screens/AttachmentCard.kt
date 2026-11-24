package com.example.zentask.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.zentask.ZenTaskTheme
import java.io.File

@Composable
fun AttachmentCard(path: String) {
    val context = LocalContext.current
    val file = remember(path) { File(path) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Attachment", fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.PictureAsPdf, null,
                    tint = ZenTaskTheme.Primary, modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Scanned document (PDF)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTaskTheme.TextPrimary
                    )
                    Text(
                        file.name,
                        fontSize = 12.sp,
                        color = ZenTaskTheme.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = { openPdf(context, file) },
                    colors = ButtonDefaults.buttonColors(containerColor = ZenTaskTheme.Primary)
                ) { Text("Open") }
            }
        }
    }
}

private fun openPdf(context: Context, file: File) {
    if (!file.exists()) {
        Toast.makeText(context, "The scanned file is no longer available.", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val view = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(view)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No app can open PDF files.", Toast.LENGTH_SHORT).show()
    }
}