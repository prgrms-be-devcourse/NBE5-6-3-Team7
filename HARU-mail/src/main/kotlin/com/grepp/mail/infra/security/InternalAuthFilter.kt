package com.grepp.mail.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class InternalAuthFilter:OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val userId = request.getHeader("x-member-id") ?: "ANONYMOUS"
        val roles = request.getHeader("x-member-role") ?: "ROLE_ANONYMOUS"
        val authorities:MutableSet<SimpleGrantedAuthority> = mutableSetOf(SimpleGrantedAuthority(roles))

        val authentication = UsernamePasswordAuthenticationToken(userId,null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }
}