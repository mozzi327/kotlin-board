package com.example.fcboard.application.dto.req

data class PostUpdateRequest(
    val title: String,
    val content: String,
    val updatedBy: String,
)
