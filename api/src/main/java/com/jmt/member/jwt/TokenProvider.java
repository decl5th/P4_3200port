package com.jmt.member.jwt;

import com.jmt.global.exceptions.UnAuthorizedException;
import com.jmt.member.MemberInfo;
import com.jmt.member.services.MemberInfoService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class TokenProvider {

    private final JwtProperties properties;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberInfoService infoService;

    /**
     * jwt 토큰 생성
     * @param email
     * @param password
     * @return
     */

    // 토큰 발급
    public String createToken(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

       if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) { // 로그인 성공시 -> jwt 토큰 발급
           long now = (new Date()).getTime();
           Date validity = new Date(now + properties.getValidSeconds() * 1000);

           return Jwts.builder()
                   .setSubject(authentication.getName()) // 사용자 email
                   .signWith(getKey(), SignatureAlgorithm.HS512) // HMAC + SHA512
                   .expiration(validity)
                   .compact();
       }

        return null;
    }

    /**
     * 토큰으로 회원 인증 객체 조회
     * @param token
     * @return
     */
    // 회원 인증 객체 정보
    public Authentication getAuthentication(String token) {
        // 토큰 검증
        validateToken(token);

        Claims claims = Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token).getPayload();

        String email = claims.getSubject(); // email

        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(memberInfo, token, memberInfo.getAuthorities());
    }

    /**
     * 토큰 유효성 검사
     *
     * @param token
     */

    // 토큰 검증
    public void validateToken(String token) {
        String errorCode = null;

        try {
            Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token).getPayload();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 변조된 jwt 토큰
            errorCode = "Malformed.jwt";
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            // 유효시간이 만료된 jwt 토큰
            errorCode = "Expired.jwt";
            e.printStackTrace();

        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 형식의 jwt 토큰
            errorCode = "Unsupported.jwt";
            e.printStackTrace();
        } catch (Exception e) {
            errorCode = "Error.jwt";
            e.printStackTrace();
        }

        if (errorCode != null) {
            throw new UnAuthorizedException(errorCode);
        }
    }


    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret()); // 서버 쪽에서 키 검증?
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
