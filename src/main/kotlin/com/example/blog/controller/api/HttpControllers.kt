package com.example.blog.controller.api

import com.example.blog.domain.entity.Article
import com.example.blog.domain.entity.User
import com.example.blog.domain.repository.ArticleRepository
import com.example.blog.domain.repository.UserRepository
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/article")
@Api(value = "Articles", description = "Operations pertaining to articles")
class ArticleController(private val repository: ArticleRepository) {

    @ApiOperation(value = "View a list of available articles", response = Iterable::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Successfully retrieved list"),
        ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    ])
    @GetMapping("/")
    fun findAll(): Iterable<Article> {
        val list = repository.findAllByOrderByAddedAtDesc()
        list.forEach { article ->
            article.author.let {
//              Como o mesmo autor pode aparecer em diversos artigos é necessário verificar se o objeto já
//              recebeu o link de navegação
                if (!it.hasLinks())
                    it.add(linkTo(methodOn(UserController::class.java).findOne(article.author.login)).withSelfRel())
            }
            article.add(linkTo(methodOn(ArticleController::class.java).findOne(article.slug)).withSelfRel())
        }
        return list
    }

    @GetMapping("/{slug}")
    fun findOne(@PathVariable slug: String): Article {
        val article =
                repository.findBySlug(slug)
                        ?: throw ResponseStatusException(NOT_FOUND, "This article does not exist")
        article.add(linkTo(methodOn(ArticleController::class.java).findAll()).withRel("All articles"))
        article.author.let {
            it.add(linkTo(methodOn(UserController::class.java).findOne(it.login)).withSelfRel())
        }
        return article
    }

}

@RestController
@RequestMapping("/api/user")
class UserController(private val repository: UserRepository) {

    @GetMapping("/")
    fun findAll() = repository.findAll().map {
        it.add(linkTo(methodOn(UserController::class.java).findOne(it.login)).withSelfRel())
    }


    @GetMapping("/{login}")
    fun findOne(@PathVariable login: String): User {
        val user = repository.findByLogin(login)
                ?.add(linkTo(methodOn(UserController::class.java).findAll()).withRel("All users"))
                ?: throw ResponseStatusException(NOT_FOUND, "This user does not exist")
        return user.add(linkTo(methodOn(UserController::class.java).findOne(login)).withSelfRel())
    }
}
