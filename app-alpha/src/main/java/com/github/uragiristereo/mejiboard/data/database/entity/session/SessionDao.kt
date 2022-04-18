package com.github.uragiristereo.mejiboard.data.database.entity.session

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Query(value = "SELECT * FROM session ORDER BY sequence ASC")
    fun getAll(): List<SessionPost>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(posts: List<SessionPost>)

    @Query("DELETE FROM session")
    fun deleteAll()
}