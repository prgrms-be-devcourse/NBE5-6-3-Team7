package com.grepp.mail

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HaruMailApplication

fun main(args: Array<String>) {
    val dotenv = dotenv {
        filename = ".env"
    }

    val myEnvVar = dotenv["SMTP_ID"]

    runApplication<HaruMailApplication>(*args)
}