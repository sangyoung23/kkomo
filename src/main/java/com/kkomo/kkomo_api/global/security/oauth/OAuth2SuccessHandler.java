package com.kkomo.kkomo_api.global.security.oauth;

import com.kkomo.kkomo_api.global.security.jwt.JwtProvider;
import com.kkomo.kkomo_api.global.security.principal.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        String token =
                jwtProvider.createToken(principal.getUserId());

        // TODO
        // 1. 하드코딩 된 localhost 주소 수정
        response.sendRedirect(
                "http://localhost:3000/login/success?token=" + token
        );
    }
}
