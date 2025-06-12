package com.grepp.mail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HaruMailApplication

fun main(args: Array<String>) {

    runApplication<HaruMailApplication>(*args)
}