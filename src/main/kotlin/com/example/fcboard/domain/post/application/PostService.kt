package com.example.fcboard.domain.post.application

import com.example.fcboard.domain.post.domain.Post
import com.example.fcboard.domain.post.dto.req.PostUpdateRequest
import com.example.fcboard.domain.post.dto.res.*
import com.example.fcboard.domain.post.exception.PostNotDeletableException
import com.example.fcboard.domain.post.exception.PostNotFoundException
import com.example.fcboard.domain.post.infra.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
) {

    @Transactional
    fun createPost(post: Post): Long {
        return postRepository.save(post).id
    }

    @Transactional
    fun updatePost(id: Long, req: PostUpdateRequest): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        post.update(req)
        return id
    }

    @Transactional
    fun deletePost(id: Long, deletedBy: String): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        if (post.createdBy != deletedBy) throw PostNotDeletableException()
        postRepository.delete(post)
        return id
    }

    fun getPost(id: Long): PostDetailResponseDto {
        return postRepository.findByIdOrNull(id)?.toDetailResponseDto() ?: throw PostNotFoundException()
    }

    fun findPageBy(pageRequest: Pageable, postSearchRequestDto: PostSearchRequestDto): Page<PostSummaryResponseDto> {
        return postRepository.findPageBy(pageRequest, postSearchRequestDto).toSummaryResponseDto()
    }
}
