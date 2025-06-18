package com.grepp.diary.app.model.auth.domain;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal implements Principal {
    private final String name;     // userId
    private final String email;
    private final String role;

    @Override
    public String getName() {
        return name; // userId 또는 username
    }
}
