package com.grepp.diary.infra.mail;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Setter
@EnableAsync
public class MailTemplate {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    // thymeleaf 의 context 에 전달할 데이터 저장
    private final Map<String, Object> properties = new LinkedHashMap<>();

    private String templatePath;
    private String to;
    @Value("${spring.mail.username}")
    private String from;
    private String subject;

    public void setProperties(String name, Object value){
        properties.put(name, value);
    }

    public Object getProperties(String name){
        return properties.get(name);
    }

    @Async
    public void send(){
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(render(), true);

            helper.addInline("mail-image", new ClassPathResource(
                "static/images/login/opend-mail.png"));


        });
    }

    private String render(){
        Context context = new Context();
        context.setVariables(properties);
        return templateEngine.process(templatePath, context);
    }





}
