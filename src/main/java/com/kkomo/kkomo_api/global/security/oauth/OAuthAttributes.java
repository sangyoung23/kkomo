package com.kkomo.kkomo_api.global.security.oauth;

import com.kkomo.kkomo_api.domain.user.OAuthProvider;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String email;
    private String name;
    private String providerId;
    private OAuthProvider provider;

    public static OAuthAttributes of(String provider,
                                     Map<String, Object> attributes) {

        switch (provider) {
            case "kakao":
                return ofKakao(attributes);
            case "naver":
                return ofNaver(attributes);
            case "google":
                return ofGoogle(attributes);
            default:
                throw new RuntimeException("지원하지 않는 OAuth provider");
        }
    }

    /**
     * 카카오
     */
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {

        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");

        Map<String, Object> profile =
                (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .attributes(attributes)
                .email((String) kakaoAccount.get("email"))
                .name((String) profile.get("nickname"))
                .providerId(String.valueOf(attributes.get("id")))
                .provider(OAuthProvider.KAKAO)
                .build();
    }

    /**
     * 네이버
     */
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {

        Map<String, Object> response =
                (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .attributes(response)
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .providerId((String) response.get("id"))
                .provider(OAuthProvider.NAVER)
                .build();
    }

    /**
     * 구글
     */
    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .attributes(attributes)
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .providerId((String) attributes.get("sub"))
                .provider(OAuthProvider.GOOGLE)
                .build();
    }

    public User toEntity() {

        // TODO
        // 1. phoneNumber는 OAuth에서 모두 제공하지 않기 때문에 따로 추가 정보 입력 받도록 개선하기
        return User.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .role(UserRole.CUSTOMER)
                .phoneNumber("00000000000")
                .build();
    }
}