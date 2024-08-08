package com.blog.BloggingApp.Repository;

import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);

    List<Comment> findByUser(User user);
}
