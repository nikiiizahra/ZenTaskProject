package com.example.zentask.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",

    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["ownerId"]),

        // Prevent duplicate category names
        // within the same user account.
        Index(
            value = ["ownerId", "name"],
            unique = true
        )
    ]
)
data class CategoryEntity(

    @PrimaryKey
    val id: String,

    // Each category belongs to one user.
    val ownerId: String,

    // Example: College / Study
    val name: String,

    // Example: 📚
    val emoji: String = "📁",

    // Store color as an ARGB integer.
    // Convert to Compose Color when displaying.
    val colorArgb: Int = 0xFF8B5CF6.toInt(),

    // Indicates whether this category is
    // one of the user's initial categories.
    val isDefault: Boolean = false,

    val createdAt: Long =
        System.currentTimeMillis()
)
