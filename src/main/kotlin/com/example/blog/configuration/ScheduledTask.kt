package com.example.blog.configuration

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*



@Component
class ScheduledTasks {
    @Scheduled(fixedRate = 300000)
    fun reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(Date()))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ScheduledTasks::class.java)
        private val dateFormat = SimpleDateFormat("HH:mm:ss")
    }
}