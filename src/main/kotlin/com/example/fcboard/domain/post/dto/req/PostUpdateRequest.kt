package com.example.fcboard.domain.post.dto.req

data class PostUpdateRequest(
    val title: String,
    val content: String,
    val updatedBy: String,
)
