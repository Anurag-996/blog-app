package com.blog.BloggingApp.Repository;

import com.blog.BloggingApp.Entities.Like;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserAndPost(User user, Post post);

    List<Like> findByUser(User user);
}
