package com.example.fcboard.application.dto.req

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String,
)
