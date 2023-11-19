package com.example.fcboard.domain.post.dto.req

import com.example.fcboard.domain.post.domain.Post

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String,
)

fun PostCreateRequest.toEntity() = Post(
    createdBy = createdBy, title = title, content = content
)
