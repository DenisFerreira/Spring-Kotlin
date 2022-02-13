package com.example.blog

import com.example.blog.configuration.BlogProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(BlogProperties::class)
@EnableScheduling
class BlogApplication

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
