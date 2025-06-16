package com.grepp.mail.app.model.code

enum class MailTemplatePath(val path: String){
    SIGNUP_VERIFY("mail/regist-verification"),
    SEND_CODE("mail/code-verification"),
    ON_REPLY("mail/on-reply")
}