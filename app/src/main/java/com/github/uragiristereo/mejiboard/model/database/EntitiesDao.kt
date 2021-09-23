package com.github.uragiristereo.mejiboard.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    fun get(): List<Bookmark>

    @Insert
    fun insert(vararg bookmarks: Bookmark)

    @Delete
    fun delete(bookmark: Bookmark)

    @Query("DELETE FROM bookmark")
    fun deleteAll()
}

@Dao
interface BlacklistDao {
    @Query("SELECT * FROM blacklist")
    fun get(): List<Blacklist>

    @Insert
    fun insert(vararg blacklists: Blacklist)

    @Delete
    fun delete(blacklist: Blacklist)

    @Query("DELETE FROM blacklist")
    fun deleteAll()
}