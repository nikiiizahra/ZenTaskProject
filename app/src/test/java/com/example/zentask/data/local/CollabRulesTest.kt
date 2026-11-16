package com.example.zentask.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
}