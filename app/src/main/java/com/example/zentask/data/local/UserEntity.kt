
package com.example.zentask.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(
            value = ["email"],
            unique = true
        )
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val email: String,

    // Store a salted password hash, never a raw password.
    val passwordHash: String,

    val passwordSalt: String,

    val createdAt: Long =
        System.currentTimeMillis()
)
