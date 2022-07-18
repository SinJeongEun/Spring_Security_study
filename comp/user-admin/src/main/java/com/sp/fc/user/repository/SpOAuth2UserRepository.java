package com.sp.fc.user.repository;

import com.sp.fc.user.domain.SpOAuth2User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpOAuth2UserRepository extends JpaRepository<SpOAuth2User, String > {
}
