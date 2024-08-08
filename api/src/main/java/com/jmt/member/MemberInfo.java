package com.jmt.member;

import com.jmt.member.constants.Authority;
import com.jmt.member.entities.Member;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class MemberInfo implements UserDetails {

    private String email;
    private String password;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {


        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 임시 조치
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 임시 조치
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 임시 조치
    }

    @Override
    public boolean isEnabled() {
        return true; // 임시 조치
    }
}
