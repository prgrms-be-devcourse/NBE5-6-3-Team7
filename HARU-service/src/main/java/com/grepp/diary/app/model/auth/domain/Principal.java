package com.grepp.diary.app.model.auth.domain;

import com.grepp.diary.app.model.member.entity.Member;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class Principal extends User {
    
    public Principal(String username, String password,
        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
    
    public static Principal createPrincipal(Member member,
        List<SimpleGrantedAuthority> authorities){
        return new Principal(member.getUserId(), member.getPassword(), authorities);
    }
}
