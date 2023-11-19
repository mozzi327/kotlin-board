package com.example.fcboard.domain.post.infra

import com.example.fcboard.domain.post.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long> {
}
