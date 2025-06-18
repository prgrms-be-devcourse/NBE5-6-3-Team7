package com.grepp.diary.infra.auth.token.filter;


import com.grepp.diary.infra.error.exceptions.AuthApiException;
import com.grepp.diary.infra.error.exceptions.AuthWebException;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Order(1) // 필터 순서를 명시해야 Spring Security가 등록 가능
public class AuthExceptionFilter extends OncePerRequestFilter {
    
    private final HandlerExceptionResolver handlerExceptionResolver;
    
    public AuthExceptionFilter(
        @Qualifier("handlerExceptionResolver")
        HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        try {
            filterChain.doFilter(request, response);
        } catch (CommonException ex) {
            throwAuthEx(request, response, ex.code());
        } catch (JwtException ex) {
            throwAuthEx(request, response, ResponseCode.UNAUTHORIZED);
        }
    }
    
    private void throwAuthEx(HttpServletRequest request, HttpServletResponse response,
        ResponseCode code) {
        if (request.getRequestURI().startsWith("/api")) {
            handlerExceptionResolver.resolveException(request, response, null,
                new AuthApiException(code));
            return;
        }
        
        if(code.equals(ResponseCode.INVALID_TOKEN) ||
            code.equals(ResponseCode.SECURITY_INCIDENT)
        ){
            handlerExceptionResolver.resolveException(request, response, null,
                new AuthWebException(code, "/member/signin"));
            return;
        }
        
        handlerExceptionResolver.resolveException(request, response, null,
            new AuthWebException(code));
    }
}