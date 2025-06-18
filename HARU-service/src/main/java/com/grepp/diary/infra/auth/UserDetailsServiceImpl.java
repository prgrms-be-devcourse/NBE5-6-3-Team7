package com.grepp.diary.infra.auth;

import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final MemberRepository memberRepository;

    @Cacheable("user-authorities")
    public List<SimpleGrantedAuthority> findAuthorities(String username) {
        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        // 저장은 정상이지만, 가져올 때 Map으로 역직렬화되므로 방어 로직 추가
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> authorities = findAuthorities(username);
        return Principal.createPrincipal(member, authorities);
    }



















}
