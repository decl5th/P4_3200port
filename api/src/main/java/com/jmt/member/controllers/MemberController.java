package com.jmt.member.controllers;

import com.jmt.global.Utils;
import com.jmt.global.exceptions.BadRequestException;
import com.jmt.global.rests.JSONData;
import com.jmt.member.jwt.TokenProvider;
import com.jmt.member.validators.JoinValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class MemberController {

    private final JoinValidator joinValidator;
    private final Utils utils;
    private final TokenProvider tokenProvider;

    //회원가입 처리
    @PostMapping
    public ResponseEntity join(@RequestBody @Valid RequestJoin form, Errors errors) {
        // json형태로 데이터를 내보낼 예정 - @RequestBody

        joinValidator.validate(form, errors);
        // 검증 실패시 json형태로
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 응답코드만 내보냄
    }

    @PostMapping("/token")
    public JSONData token(@RequestBody @Valid RequestJoin form, Errors errors) {

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        String token = tokenProvider.createToken(form.getEmail(), form.getPassword());

        return new JSONData(token);
    }

    @GetMapping("/test1")
    public void memberOnly() {
        log.info("회원전용!");
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void adminOnly() {
        log.info("관리자 전용!");
    }


}
