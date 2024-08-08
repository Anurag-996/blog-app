package com.blog.BloggingApp.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE p.user = :user")
    List<Post> findByCreatedBy(@Param("user") User user);

    @Query("SELECT p FROM Post p JOIN p.comments c WHERE c.user = :user")
    List<Post> findByCommentsUser(@Param("user") User user);

    @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.user = :user")
    List<Post> findByLikesUser(@Param("user") User user);
}
