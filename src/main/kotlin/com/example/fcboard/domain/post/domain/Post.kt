package com.example.fcboard.domain.post.domain

import com.example.fcboard.domain.common.entity.BaseEntity
import com.example.fcboard.domain.post.dto.req.PostUpdateRequest
import com.example.fcboard.domain.post.exception.PostNotUpdatableException
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Post(
    createdBy: String,
    title: String,
    content: String,
) : BaseEntity(createdBy) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
    var title = title
        protected set
    var content = content
        protected set

    fun update(update: PostUpdateRequest) {
        if (update.updatedBy != this.createdBy) throw PostNotUpdatableException()
        this.title = update.title
        this.content = update.content
        super.updatedBy(update.updatedBy)
    }
}
