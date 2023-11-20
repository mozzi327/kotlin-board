package com.example.fcboard.domain.post.presentation

import com.example.fcboard.domain.post.application.PostService
import com.example.fcboard.domain.post.dto.req.*
import com.example.fcboard.domain.post.dto.res.PostDetailResponse
import com.example.fcboard.domain.post.dto.res.PostSummaryResponse
import com.example.fcboard.domain.post.dto.res.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
class PostController(
    private val postService: PostService,
) {

    @PostMapping("/posts")
    fun createPost(
        @RequestBody req: PostCreateRequest,
    ): Long {
        return postService.createPost(req.toEntity())
    }

    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody req: PostUpdateRequest,
    ): Long {
        return postService.updatePost(id, req)
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestParam createdBy: String,
    ): Long {
        return postService.deletePost(id, createdBy)
    }

    @GetMapping("/posts/{id}")
    fun getPost(
        @PathVariable id: Long,
    ): PostDetailResponse {
        return postService.getPost(id).toResponse()
    }

    @GetMapping("/posts")
    fun getPosts(
        pageable: Pageable,
        req: PostSearchRequest,
    ): Page<PostSummaryResponse> {
        return postService.findPageBy(pageable, req.toDto()).toResponse()
    }
}
