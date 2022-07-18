package com.sp.fc.user.service;

import com.sp.fc.user.domain.SpAuthority;
import com.sp.fc.user.domain.SpOAuth2User;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.repository.SpOAuth2UserRepository;
import com.sp.fc.user.repository.SpUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpUserService implements UserDetailsService {

    @Autowired
    private  SpUserRepository spUserRepository;

    @Autowired
    private com.sp.fc.user.repository.SpOAuth2UserRepository SpOAuth2UserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return spUserRepository.findSpUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public Optional<SpUser> findUser(String email) {
        return spUserRepository.findSpUserByEmail(email);
    }

    public SpUser save(SpUser user) {
        return spUserRepository.save(user);
    }

    public void addAuthority(Long userId, String authority){
        spUserRepository.findById(userId).ifPresent(user->{
            SpAuthority newRole = new SpAuthority(user.getUserId(), authority);
            if(user.getAuthorities() == null){ // 기존에 권한이 없었다면  새로운 hashSet 만들어서 권한 삽입 후 저장
                HashSet<SpAuthority> authorities = new HashSet<>();
                authorities.add(newRole);
                user.setAuthorities(authorities);
                save(user);
            }else if(!user.getAuthorities().contains(newRole)){ // 권한이 있다면 hashSet 새로 만들어서 기존 권한 넣고, 새로운 권한 추가
                HashSet<SpAuthority> authorities = new HashSet<>();
                authorities.addAll(user.getAuthorities());
                authorities.add(newRole);
                user.setAuthorities(authorities);
                save(user);
            }
        });
    }

    public void removeAuthority(Long userId, String authority){
        spUserRepository.findById(userId).ifPresent(user->{
            if(user.getAuthorities()==null) return;
            SpAuthority targetRole = new SpAuthority(user.getUserId(), authority);
            if(user.getAuthorities().contains(targetRole)){
                user.setAuthorities(
                        user.getAuthorities().stream().filter(auth->!auth.equals(targetRole))
                                .collect(Collectors.toSet())
                );
                save(user);
            }
        });
    }

    // 소셜 로그인 한 유저가 db에 있으면 spUser 객체를 리턴하고, 없으면 SpUser 만들어서 우리의 유저 테이블에 저장한다.
    public SpUser load(final SpOAuth2User oauth2User){
        SpOAuth2User user = SpOAuth2UserRepository.findById(oauth2User.getOauth2UserId()).orElseGet(()->{
            SpUser spUser = new SpUser();
            spUser.setEmail(oauth2User.getEmail());
            spUser.setName(oauth2User.getName());
            spUser.setEnabled(true);
            spUser.setPassword("");
            spUserRepository.save(spUser);
            addAuthority(spUser.getUserId(), "ROLE_USER");

            oauth2User.setUserId(spUser.getUserId());
            oauth2User.setCreated(LocalDateTime.now());
            return SpOAuth2UserRepository.save(oauth2User);
        });
        return spUserRepository.findById(user.getUserId()).get();
    }
}
