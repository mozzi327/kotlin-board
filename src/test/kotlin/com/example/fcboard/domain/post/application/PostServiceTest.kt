package com.example.fcboard.domain.post.application

import com.example.fcboard.domain.post.domain.Post
import com.example.fcboard.domain.post.dto.req.PostCreateRequest
import com.example.fcboard.domain.post.dto.req.PostUpdateRequest
import com.example.fcboard.domain.post.dto.req.toEntity
import com.example.fcboard.domain.post.dto.res.PostSearchRequestDto
import com.example.fcboard.domain.post.exception.PostNotDeletableException
import com.example.fcboard.domain.post.exception.PostNotFoundException
import com.example.fcboard.domain.post.exception.PostNotUpdatableException
import com.example.fcboard.domain.post.infra.PostRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
) : BehaviorSpec({
    beforeSpec {
        postRepository.saveAll(
            listOf(
                Post(createdBy = "harris1", title = "title1", content = "sententiae"),
                Post(createdBy = "harris1", title = "title13", content = "sententiae"),
                Post(createdBy = "harris1", title = "title14", content = "sententiae"),
                Post(createdBy = "harris1", title = "title15", content = "sententiae"),
                Post(createdBy = "harris6", title = "title6", content = "sententiae"),
                Post(createdBy = "harris7", title = "title7", content = "sententiae"),
                Post(createdBy = "harris8", title = "title8", content = "sententiae"),
                Post(createdBy = "harris10", title = "title44", content = "sententiae"),
                Post(createdBy = "harris9", title = "title9", content = "sententiae"),
                Post(createdBy = "harris1", title = "title12", content = "sententiae")
            )
        )
    }

    given("게시글 생성 시") {
        When("게시글 입력이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequest(
                    title = "제목",
                    content = "내용",
                    createdBy = "harris"
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
                saved.id,
                PostUpdateRequest(
                    title = "instructior",
                    content = "ad",
                    updatedBy = "justo"
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
                        9999L,
                        PostUpdateRequest(
                            title = "saperet",
                            content = "natoque",
                            updatedBy = "justo"
                        )
                    )
                }
            }
        }

        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L,
                        PostUpdateRequest(
                            title = "saperet",
                            content = "natoque",
                            updatedBy = "update justo"
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

    given("게시글 상세 조회 시") {
        val saved = postRepository.save(Post(createdBy = "harris", title = "title", content = "content"))
        When("정상 조회 시") {
            val post = postService.getPost(saved.id)
            then("게시글의 내용이 정상적으로 반환됨을 확인한다.") {
                post.id shouldBe saved.id
                post.title shouldBe "title"
                post.content shouldBe "content"
                post.createdBy shouldBe "harris"
            }
        }

        When("게시글이 없을 때") {
            then("게시글을 찾을 수 없습니다 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> { postService.getPost(9999L) }
            }
        }
    }

    given("게시글 목록 조회 시") {
        When("정상 조회 시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글 페이지가 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldContain "harris"
            }
        }

        When("타이틀로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "title1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title1"
                postPage.content[0].createdBy shouldContain "harris"
            }
        }

        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "harris1"))
            then("작성자에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title"
                postPage.content[0].createdBy shouldBe "harris1"
            }
        }
    }
})
