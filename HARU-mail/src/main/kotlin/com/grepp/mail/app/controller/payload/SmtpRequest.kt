package com.grepp.mail.app.controller.payload

data class SmtpRequest(
    val from: String,
    val subject: String,
    val to: List<String>,
    val properties: MutableMap<String, String> = mutableMapOf(),
    val eventType: String
)