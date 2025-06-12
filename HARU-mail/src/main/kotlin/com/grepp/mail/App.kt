package com.grepp.mail

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment

@SpringBootApplication
class HaruMailApplication

fun main(args: Array<String>) {

    // 1. .env 로드
    val env = Dotenv.configure().ignoreIfMissing().load()
        .entries()
        .associate { it.key to it.value }

    // 2. .env 값을 Spring Environment에 등록
    val springEnv = object : StandardEnvironment() {
        override fun customizePropertySources(propertySources: MutablePropertySources) {
            super.customizePropertySources(propertySources)
            propertySources.addLast(MapPropertySource("dotenvProperties", env))
        }
    }

    // 3. Spring 실행
    SpringApplicationBuilder(HaruMailApplication::class.java)
        .environment(springEnv)
        .run(*args)
}