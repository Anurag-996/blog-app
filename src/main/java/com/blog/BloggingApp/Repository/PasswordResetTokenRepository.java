package com.blog.BloggingApp.Repository;

import com.blog.BloggingApp.Entities.PasswordReset;
import com.blog.BloggingApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordReset,Integer> {
    Optional<PasswordReset> findByResetTokenAndUser(String token, User user);

    PasswordReset findByResetToken(String token);
}
