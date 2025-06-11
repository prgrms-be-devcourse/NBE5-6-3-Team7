package com.grepp.diary.infra.util.xss;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class XssProtectionUtils {

    // 모든 HTML 특수 문자를 이스케이프 처리
    public String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return HtmlUtils.htmlEscape(input);
    }

    // 이스케이프 기능 + 개행 문자(\n) 를 <br/> 태그로 변환 기능
    public String escapeHtmlWithLineBreaks(String input) {
        if (input == null) {
            return "";
        }
        return HtmlUtils.htmlEscape(input).replace("\n", "<br/>");
    }

    // 이스케이프된 문자를 원래대로 복원
    public String unescapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return HtmlUtils.htmlUnescape(input);
    }

    public String unescapeHtmlWithLineBreaks(String input) {
        if (input == null) {
            return "";
        }
        return HtmlUtils.htmlUnescape(input.replace("<br/>", "\n"));
    }
}
