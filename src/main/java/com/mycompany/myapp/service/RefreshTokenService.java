package com.mycompany.myapp.service;

import static com.mycompany.myapp.security.session.SessionUser.SESSION_USER_KEY;

import com.mycompany.myapp.domain.RefreshToken;
import com.mycompany.myapp.repository.RefreshTokenRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.RefreshTokenException;
import com.mycompany.myapp.security.session.SessionUser;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class RefreshTokenService {

    @Value("${spring.refresh-token-validity-in-seconds}")
    private String refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(Long.parseLong(refreshTokenDurationMs)));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public SessionUser getSessionUser() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return (SessionUser) servletRequestAttributes.getRequest().getSession().getAttribute(SESSION_USER_KEY);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }
    //    @Transactional
    //    public int deleteByUserId(Long userId) {
    //        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    //    }
}
