package com.github.uragiristereo.mejiboard.domain.entity.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Query(value = "SELECT * FROM session ORDER BY sequence ASC")
    fun getAll(): List<PostSession>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(posts: List<PostSession>)

    @Query("DELETE FROM session")
    fun deleteAll()
}