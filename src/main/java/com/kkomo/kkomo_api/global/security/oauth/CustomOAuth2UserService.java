package com.kkomo.kkomo_api.global.security.oauth;

import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import com.kkomo.kkomo_api.global.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User =
                super.loadUser(userRequest);

        String provider =
                userRequest.getClientRegistration()
                        .getRegistrationId();

        OAuthAttributes attributes =
                OAuthAttributes.of(provider,
                        oAuth2User.getAttributes());

        User user =
                userRepository
                        .findByProviderAndProviderId(
                                attributes.getProvider(),
                                attributes.getProviderId()
                        )
                        .orElseGet(() ->
                                userRepository.save(
                                        attributes.toEntity()
                                )
                        );

        return new CustomUserPrincipal(user, attributes.getAttributes());
    }
}
