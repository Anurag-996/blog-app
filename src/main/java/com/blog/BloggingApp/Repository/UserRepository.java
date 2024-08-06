package com.blog.BloggingApp.Repository;

import com.blog.BloggingApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmailId(String emailId);

    Optional<User> findByUser(User user);
}
