
package com.example.zentask.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.zentask.model.TaskStatus

@Composable
fun CollaborationCard(
    task: TaskItem,
    invite: CollabInviteEntity?,
    members: List<CollabMemberInfo>,
    onCreateInvite: () -> Unit,
    onRevokeInvite: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onLeave: () -> Unit,
    onToggleMemberCompletion: (Boolean) -> Unit = {},
    currentUserId: String = ""
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val now = System.currentTimeMillis()
    val activeInvite = invite?.takeIf { it.expiresAt > now }
    val red = Color(0xFFDC2626)

    val completedMembers = members.count { it.isCompleted }
    val totalMembers = members.size

    val myMemberInfo = members.firstOrNull { it.userId == currentUserId }
    val isMyPortionDone = myMemberInfo?.isCompleted
        ?: (task.isOwner && task.status == TaskStatus.COMPLETED)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Collaboration header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Collaboration Status",
                    fontWeight = FontWeight.Bold,
                    color = ZenTaskTheme.TextPrimary
                )

//                if (totalMembers > 0) {
//                    Surface(
//                        color = if (completedMembers == totalMembers) {
//                            Color(0xFFD1FAE5)
//                        } else {
//                            Color(0xFFEDE9FE)
//                        },
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Text(
//                            text = "👥 $completedMembers/$totalMembers Portion Done",
//                            modifier = Modifier.padding(
//                                horizontal = 10.dp,
//                                vertical = 4.dp
//                            ),
//                            color = if (completedMembers == totalMembers) {
//                                Color(0xFF059669)
//                            } else {
//                                Color(0xFF6E56CF)
//                            },
//                            fontSize = 11.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
            }

            // Individual portion completion
//            Button(
//                onClick = {
//                    onToggleMemberCompletion(!isMyPortionDone)
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (isMyPortionDone) {
//                        Color(0xFF10B981)
//                    } else {
//                        ZenTaskTheme.Primary
//                    }
//                ),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = if (isMyPortionDone) {
//                        "Reopen My Portion ↺"
//                    } else {
//                        "Mark My Portion Done ✓"
//                    },
//                    fontWeight = FontWeight.Bold
//                )
//            }

            if (!task.isOwner) {

                Text(
                    "Shared with you",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZenTaskTheme.Primary,
                    modifier = Modifier
                        .background(
                            ZenTaskTheme.PrimaryContainer,
                            RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                )

//                Text(
//                    "Mark your portion as done when you finish your part of this task. " +
//                            "Each member updates their own status transparently.",
//                    fontSize = 13.sp,
//                    color = ZenTaskTheme.TextSecondary
//                )

//                OutlinedButton(onClick = onLeave) {
//                    Text("Leave task", color = red)
//                }

            } else {

                // Invite management
                if (activeInvite != null) {

                    Text(
                        "Invite code",
                        fontSize = 12.sp,
                        color = ZenTaskTheme.TextSecondary
                    )

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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(
                                    AnnotatedString(activeInvite.code)
                                )
                                Toast.makeText(
                                    context,
                                    "Code copied",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Text("Copy")
                        }

                        OutlinedButton(
                            onClick = {
                                val send = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Join my ZenTask \"${task.title}\" with invite code " +
                                                "${activeInvite.code}. The code is valid for 24 hours."
                                    )
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        send,
                                        "Share invite code"
                                    )
                                )
                            }
                        ) {
                            Text("Share")
                        }

                        TextButton(onClick = onRevokeInvite) {
                            Text("Revoke", color = red)
                        }
                    }

                } else {

                    Text(
                        "Invite another ZenTask account to work on this task together and track individual portions.",
                        fontSize = 13.sp,
                        color = ZenTaskTheme.TextSecondary
                    )

                    Button(
                        onClick = onCreateInvite,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZenTaskTheme.Primary
                        )
                    ) {
                        Text("Create invite code")
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE2E8F0)
                )

                // Members list
                Text(
                    "Member",
                    fontSize = 12.sp,
                    color = ZenTaskTheme.TextSecondary
                )

                if (members.isEmpty()) {

                    Text(
                        "No one has joined yet.",
                        fontSize = 13.sp,
                        color = ZenTaskTheme.TextSecondary
                    )

                } else {

                    members.forEach { member ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (member.userId == currentUserId) {
                                        "${member.name} (You)"
                                    } else {
                                        member.name
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZenTaskTheme.TextPrimary
                                )

                                Text(
                                    member.email,
                                    fontSize = 12.sp,
                                    color = ZenTaskTheme.TextSecondary
                                )
                            }

//                            Surface(
//                                color = if (member.isCompleted) {
//                                    Color(0xFFD1FAE5)
//                                } else {
//                                    Color(0xFFF1F5F9)
//                                },
//                                shape = RoundedCornerShape(8.dp)
//                            ) {
//                                Text(
//                                    text = if (member.isCompleted) {
//                                        "Portion Done ✓"
//                                    } else {
//                                        "Working..."
//                                    },
//                                    modifier = Modifier.padding(
//                                        horizontal = 8.dp,
//                                        vertical = 4.dp
//                                    ),
//                                    color = if (member.isCompleted) {
//                                        Color(0xFF059669)
//                                    } else {
//                                        Color(0xFF64748B)
//                                    },
//                                    fontSize = 11.sp,
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }

                            if (
                                member.userId != currentUserId &&
                                task.isOwner
                            ) {
                                Spacer(Modifier.width(8.dp))

                                TextButton(
                                    onClick = {
                                        onRemoveMember(member.userId)
                                    }
                                ) {
                                    Text("Remove", color = red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Standalone Leave Task button
// Used at the bottom of TaskDetailScreen
@Composable
fun LeaveTaskButton(
    onLeave: () -> Unit
) {
    OutlinedButton(
        onClick = onLeave,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFFECACA)
        ),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFDC2626)
        )
    ) {
        Text(
            text = "Leave task",
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun expiresIn(
    expiresAt: Long,
    now: Long
): String {
    val minutes = ((expiresAt - now) / 60_000).coerceAtLeast(0)

    return if (minutes >= 60) {
        "${minutes / 60}h ${minutes % 60}m"
    } else {
        "${minutes}m"
    }
}
