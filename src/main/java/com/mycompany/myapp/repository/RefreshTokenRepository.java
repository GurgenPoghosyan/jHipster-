package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RefreshToken;
import com.mycompany.myapp.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long id);

    Optional<RefreshToken> findByToken(String token);
    //    int deleteByUser(User user);
}
