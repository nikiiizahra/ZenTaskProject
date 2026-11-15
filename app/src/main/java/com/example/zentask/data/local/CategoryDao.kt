
package com.example.zentask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // ================================================
    // CREATE CATEGORY
    // ================================================

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertCategory(
        category: CategoryEntity
    )

    // ================================================
    // CREATE MULTIPLE DEFAULT CATEGORIES
    // ================================================

    @Insert(
        onConflict = OnConflictStrategy.IGNORE
    )
    suspend fun insertCategories(
        categories: List<CategoryEntity>
    )

    // ================================================
    // OBSERVE USER'S CATEGORIES
    // ================================================

    // Automatically updates the UI when categories
    // are added, renamed, or removed.
    @Query(
        """
        SELECT *
        FROM categories
        WHERE ownerId = :ownerId
        ORDER BY isDefault DESC, name COLLATE NOCASE ASC
        """
    )
    fun observeCategoriesByOwner(
        ownerId: String
    ): Flow<List<CategoryEntity>>

    // ================================================
    // GET ALL CATEGORIES FOR ONE USER
    // ================================================

    @Query(
        """
        SELECT *
        FROM categories
        WHERE ownerId = :ownerId
        ORDER BY isDefault DESC, name COLLATE NOCASE ASC
        """
    )
    suspend fun getCategoriesByOwner(
        ownerId: String
    ): List<CategoryEntity>

    // ================================================
    // GET CATEGORY BY ID AND OWNER
    // ================================================

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :categoryId
          AND ownerId = :ownerId
        LIMIT 1
        """
    )
    suspend fun getCategoryById(
        categoryId: String,
        ownerId: String
    ): CategoryEntity?

    // ================================================
    // GET CATEGORY BY NAME AND OWNER
    // ================================================

    @Query(
        """
        SELECT *
        FROM categories
        WHERE ownerId = :ownerId
          AND name = :categoryName
        LIMIT 1
        """
    )
    suspend fun getCategoryByName(
        ownerId: String,
        categoryName: String
    ): CategoryEntity?

    // ================================================
    // RENAME CATEGORY
    // ================================================

    // A category can only be renamed by its owner.
    @Query(
        """
        UPDATE categories
        SET name = :newName,
            emoji = :newEmoji,
            colorArgb = :newColorArgb
        WHERE id = :categoryId
          AND ownerId = :ownerId
        """
    )
    suspend fun updateCategory(
        categoryId: String,
        ownerId: String,
        newName: String,
        newEmoji: String,
        newColorArgb: Int
    ): Int

    // ================================================
    // DELETE CATEGORY
    // ================================================

    // Only deletes the category record owned by
    // the specified account.
    @Query(
        """
        DELETE FROM categories
        WHERE id = :categoryId
          AND ownerId = :ownerId
        """
    )
    suspend fun deleteCategory(
        categoryId: String,
        ownerId: String
    ): Int

    // ================================================
    // CATEGORY COUNTS
    // ================================================

    @Query(
        """
        SELECT COUNT(*)
        FROM categories
        WHERE ownerId = :ownerId
        """
    )
    fun observeCategoryCount(
        ownerId: String
    ): Flow<Int>
}
