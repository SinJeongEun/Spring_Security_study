package com.sp.fc.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sp_oauth2_user")
public class SpOAuth2User {

    public static enum OAuth2Provider {
        google {
            public SpOAuth2User convert(OAuth2User user){
                return SpOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), user.getAttribute("sub"))) //providerID 값을 sub 로 가져와야 됨
                        .provider(google)
                        .email(user.getAttribute("email"))
                        .name(user.getAttribute("name"))
                        .created(LocalDateTime.now())
                        .build();
            }
        },
        naver {
            public SpOAuth2User convert(OAuth2User user){
                Map<String, Object> resp = user.getAttribute("response"); // providerID 값을 response 로 받아야 됨
                System.out.println(resp.get("id"));

                return SpOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), resp.get("id")))
                        .provider(naver)
                        .email(""+resp.get("email"))
                        .name(""+resp.get("name"))
                        .build();
            }
        },
        kakao {
            public SpOAuth2User convert(OAuth2User user){
                Map<String, Object> resp = user.getAttributes();
                Map<String, Object> kakao_account = user.getAttribute("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");
//                System.out.println(user.getAttributes());
                System.out.println(user.toString());
                System.out.println(resp.get("id"));
                System.out.println(kakao_account.get("email"));
                System.out.println(profile.get("nickname"));
                return SpOAuth2User.builder()
                        .oauth2UserId(format("%s_%s", name(), resp.get("id"))) //kakao_ ~ 로 저장된다.
                        .provider(kakao)
                        .email("" + kakao_account.get("email"))
                        .name("" + profile.get("nickname"))
                        .build();
            }
        };

        public abstract SpOAuth2User convert(OAuth2User user);
    }

    @Id
    private String oauth2UserId;

    private Long userId;

    private String name;
    private String email;

    private OAuth2Provider provider;

    private LocalDateTime created;

    // 등록일, ....
}
