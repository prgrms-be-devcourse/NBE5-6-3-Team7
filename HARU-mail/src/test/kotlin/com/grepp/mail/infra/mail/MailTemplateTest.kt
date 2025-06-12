package com.grepp.mail.infra.mail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class MailTemplateTest {

    @Autowired
    lateinit var template: MailTemplate

    @Test
    fun testSend() = runBlocking {
        val dto = SmtpDto(
            from = "HARU",
            subject = "mail test",
            to = "yeawon6566@gmail.com",
            templatePath = "/mail/regist-verification",
        )

        val semaphore = Semaphore(3)
        for(i in 1 .. 10){
            launch(Dispatchers.IO) {
                semaphore.withPermit {
                    template.send(dto)
                }
            }
        }

    }

}