package com.example.fcboard.domain.post.application

import com.example.fcboard.domain.post.domain.Post
import com.example.fcboard.domain.post.dto.req.PostCreateRequest
import com.example.fcboard.domain.post.dto.req.PostUpdateRequest
import com.example.fcboard.domain.post.dto.req.toEntity
import com.example.fcboard.domain.post.exception.PostNotDeletableException
import com.example.fcboard.domain.post.exception.PostNotFoundException
import com.example.fcboard.domain.post.exception.PostNotUpdatableException
import com.example.fcboard.domain.post.infra.PostRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
) : BehaviorSpec({

    given("게시글 생성 시") {
        When("게시글 입력이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequest(
                    title = "제목",
                    content = "내용",
                    createdBy = "harris",
                ).toEntity()
            )

            then("게시글이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "harris"
            }
        }
    }

    given("게시글 수정 시") {
        val saved = postRepository.save(Post(createdBy = "justo", title = "sollicitudin", content = "tristique"))
        When("정상 수정 시") {
            val updateId = postService.updatePost(
                saved.id, PostUpdateRequest(
                    title = "instructior", content = "ad", updatedBy = "justo"
                )
            )
            then("게시글이 정상적으로 수정됨을 확인한다.") {
                saved.id shouldBe updateId
                val updated = postRepository.findByIdOrNull(updateId)
                updated shouldNotBe null
                updated?.title shouldBe "instructior"
                updated?.content shouldBe "ad"
                updated?.updatedBy shouldBe "justo"
            }
        }

        When("게시글이 없을 때") {
            then("게시글을 찾을 수 없다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L, PostUpdateRequest(
                            title = "saperet", content = "natoque", updatedBy = "justo"
                        )
                    )
                }
            }
        }

        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L, PostUpdateRequest(
                            title = "saperet", content = "natoque", updatedBy = "update justo"
                        )
                    )
                }
            }
        }
    }

    given("게시글 삭제 시") {
        When("정상 삭제 시") {
            val saved = postRepository.save(Post(createdBy = "justo", title = "sollicitudin", content = "tristique"))
            val postId = postService.deletePost(saved.id, "justo")
            then("게시글이 정상적으로 삭제됨을 확인한다.") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }

        When("작성자가 동일하지 않으면") {
            val saved = postRepository.save(Post(createdBy = "justo", title = "sollicitudin", content = "tristique"))
            then("삭제할 수 없는 게시물입니다 예외가 발생한다.") {
                shouldThrow<PostNotDeletableException> {
                    postService.deletePost(saved.id, "updated justo")
                }
            }
        }
    }
})
