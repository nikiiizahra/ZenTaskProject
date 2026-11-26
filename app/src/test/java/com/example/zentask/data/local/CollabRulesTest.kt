package com.example.zentask.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Assert.assertFalse

class CollabRulesTest {

    private fun invite(expiresAt: Long = 2_000L, revoked: Boolean = false) =
        CollabInviteEntity("123456", "t1", "owner", createdAt = 0L, expiresAt = expiresAt, revoked = revoked)

    private fun evaluate(
        invite: CollabInviteEntity? = invite(),
        userId: String = "guest",
        taskOwnerId: String? = "owner",
        alreadyMember: Boolean = false
    ) = CollabRules.evaluate(invite, now = 1_000L, userId = userId, taskOwnerId = taskOwnerId, alreadyMember = alreadyMember)

    @Test fun normalize_keeps_only_six_digits() =
        assertEquals("123456", CollabRules.normalizeCode(" 12-34 56 78 "))

    @Test fun valid_join_has_no_failure() = assertNull(evaluate())

    @Test fun unknown_code_is_invalid() = assertEquals(JoinResult.InvalidCode, evaluate(invite = null))

    @Test fun missing_task_is_invalid() = assertEquals(JoinResult.InvalidCode, evaluate(taskOwnerId = null))

    @Test fun revoked_code_is_rejected() = assertEquals(JoinResult.Revoked, evaluate(invite = invite(revoked = true)))

    @Test fun expired_code_is_rejected() = assertEquals(JoinResult.Expired, evaluate(invite = invite(expiresAt = 500L)))

    @Test fun owner_cannot_join_own_task() = assertEquals(JoinResult.OwnTask, evaluate(userId = "owner"))

    @Test fun duplicate_join_is_rejected() = assertEquals(JoinResult.AlreadyMember, evaluate(alreadyMember = true))

    @Test fun generated_codes_are_six_digits() {
        repeat(200) {
            val code = CollabRules.generateCode()
            assertEquals(6, code.length)
            assertTrue(code.all { it.isDigit() })
        }
    }

    @Test fun progress_is_zero_when_nothing_is_done() =
        assertEquals(ProgressResult(0, "NOT_STARTED"), CollabRules.progressOf(0, 3))

    @Test fun progress_is_partial_when_some_are_done() =
        assertEquals(ProgressResult(33, "IN_PROGRESS"), CollabRules.progressOf(1, 3))

    @Test fun progress_is_completed_when_all_are_done() =
        assertEquals(ProgressResult(100, "COMPLETED"), CollabRules.progressOf(3, 3))

    @Test fun progress_handles_empty_total() =
        assertEquals(ProgressResult(0, "NOT_STARTED"), CollabRules.progressOf(0, 0))

    @Test fun progress_never_exceeds_total() =
        assertEquals(ProgressResult(100, "COMPLETED"), CollabRules.progressOf(5, 3))

    @Test fun owner_and_assignee_can_toggle_subtask() {
        assertTrue(CollabRules.canToggleSubtask("owner", "guest", "owner"))
        assertTrue(CollabRules.canToggleSubtask("owner", "guest", "guest"))
    }

    @Test fun other_members_cannot_toggle_subtask() {
        assertFalse(CollabRules.canToggleSubtask("owner", "guest", "other"))
        assertFalse(CollabRules.canToggleSubtask("owner", null, "guest"))
    }
}