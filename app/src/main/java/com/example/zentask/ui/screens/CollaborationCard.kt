package com.example.zentask.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentask.ZenTaskTheme
import com.example.zentask.data.local.CollabInviteEntity
import com.example.zentask.data.local.CollabMemberInfo
import com.example.zentask.model.TaskItem
import androidx.compose.foundation.background

@Composable
fun CollaborationCard(
    task: TaskItem,
    invite: CollabInviteEntity?,
    members: List<CollabMemberInfo>,
    onCreateInvite: () -> Unit,
    onRevokeInvite: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onLeave: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val now = System.currentTimeMillis()
    val activeInvite = invite?.takeIf { it.expiresAt > now }
    val red = Color(0xFFDC2626)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Collaboration", fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)

            if (!task.isOwner) {
                Text(
                    "Shared with you",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTaskTheme.Primary,
                    modifier = Modifier
                        .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Text(
                    "You can update the status and progress of this task. " +
                            "Only the owner can edit it, delete it, or invite others.",
                    fontSize = 13.sp,
                    color = ZenTaskTheme.TextSecondary
                )
                OutlinedButton(onClick = onLeave) { Text("Leave task", color = red) }
            } else {
                if (activeInvite != null) {
                    Text("Invite code", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                    Text(
                        activeInvite.code,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 6.sp,
                        color = ZenTaskTheme.Primary
                    )
                    Text(
                        "Expires in ${expiresIn(activeInvite.expiresAt, now)}",
                        fontSize = 12.sp,
                        color = ZenTaskTheme.TextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            clipboard.setText(AnnotatedString(activeInvite.code))
                            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                        }) { Text("Copy") }
                        OutlinedButton(onClick = {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Join my ZenTask \"${task.title}\" with invite code " +
                                            "${activeInvite.code}. The code is valid for 24 hours."
                                )
                            }
                            context.startActivity(Intent.createChooser(send, "Share invite code"))
                        }) { Text("Share") }
                        TextButton(onClick = onRevokeInvite) { Text("Revoke", color = red) }
                    }
                } else {
                    Text(
                        "Invite another ZenTask account to update the progress of this task with you.",
                        fontSize = 13.sp,
                        color = ZenTaskTheme.TextSecondary
                    )
                    Button(
                        onClick = onCreateInvite,
                        colors = ButtonDefaults.buttonColors(containerColor = ZenTaskTheme.Primary)
                    ) { Text("Create invite code") }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text("People with access", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                if (members.isEmpty()) {
                    Text("No one has joined yet.", fontSize = 13.sp, color = ZenTaskTheme.TextSecondary)
                } else {
                    members.forEach { member ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    member.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZenTaskTheme.TextPrimary
                                )
                                Text(member.email, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                            }
                            TextButton(onClick = { onRemoveMember(member.userId) }) {
                                Text("Remove", color = red)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun expiresIn(expiresAt: Long, now: Long): String {
    val minutes = ((expiresAt - now) / 60_000).coerceAtLeast(0)
    return if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes}m"
}