package com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.posts

import com.squareup.moshi.Json
import java.util.*

data class DanbooruPost(
    val id: Int?,

    @field:Json(name = "created_at")
    val createdAt: Date,

    @field:Json(name = "uploader_id")
    val uploaderId: Int,
    val score: Int,
    val source: String,
    val md5: String?,

    @field:Json(name = "last_comment_bumped_at")
    val lastCommentBumpedAt: String?,
    val rating: String,

    @field:Json(name = "image_width")
    val imageWidth: Int,

    @field:Json(name = "image_height")
    val imageHeight: Int,

    @field:Json(name = "tag_string")
    val tagString: String,

    @field:Json(name = "fav_count")
    val favCount: Int,

    @field:Json(name = "file_ext")
    val fileExt: String,

    @field:Json(name = "last_noted_at")
    val lastNotedAt: Date?,

    @field:Json(name = "parent_id")
    val parentId: String?,

    @field:Json(name = "has_children")
    val hasChildren: Boolean,

    @field:Json(name = "approver_id")
    val approverId: String?,

    @field:Json(name = "tag_count_general")
    val tagCountGeneral: Int,

    @field:Json(name = "tag_count_artist")
    val tagCountArtist: Int,

    @field:Json(name = "tag_count_character")
    val tagCountCharacter: Int,

    @field:Json(name = "tag_count_copyright")
    val tagCountCopyright: Int,

    @field:Json(name = "file_size")
    val fileSize: Int,

    @field:Json(name = "up_score")
    val upScore: Int,

    @field:Json(name = "down_score")
    val downScore: Int,

    @field:Json(name = "is_pending")
    val isPending: Boolean,

    @field:Json(name = "is_flagged")
    val isFlagged: Boolean,

    @field:Json(name = "is_deleted")
    val isDeleted: Boolean,

    @field:Json(name = "tag_count")
    val tagCount: Int,

    @field:Json(name = "updated_at")
    val updatedAt: String,

    @field:Json(name = "is_banned")
    val isBanned: Boolean,

    @field:Json(name = "pixiv_id")
    val pixivId: Int?,

    @field:Json(name = "last_commented_at")
    val lastCommentedAt: String?,

    @field:Json(name = "has_active_children")
    val hasActiveChildren: Boolean,

    @field:Json(name = "bit_flags")
    val bitFlags: Int,

    @field:Json(name = "tag_count_meta")
    val tagCountMeta: Int,

    @field:Json(name = "has_large")
    val hasLarge: Boolean?,

    @field:Json(name = "has_visible_children")
    val hasVisibleChildren: Boolean,

    @field:Json(name = "tag_string_general")
    val tagStringGeneral: String,

    @field:Json(name = "tag_string_character")
    val tagStringCharacter: String,

    @field:Json(name = "tag_string_copyright")
    val tagStringCopyright: String,

    @field:Json(name = "tag_string_artist")
    val tagStringArtist: String,

    @field:Json(name = "tag_string_meta")
    val tagStringMeta: String,

    @field:Json(name = "file_url")
    val fileUrl: String?,

    @field:Json(name = "large_file_url")
    val largeFileUrl: String?,

    @field:Json(name = "preview_file_url")
    val previewFileUrl: String?,
)
