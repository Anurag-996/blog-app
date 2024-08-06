package com.blog.BloggingApp.Service;

import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.Repository.CommentRepository;
import com.blog.BloggingApp.ResponseDTOs.CommentResponse;
import com.blog.BloggingApp.Transformers.CommentTransformer;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponse createComment(Comment comment) {
        // Ensure the Comment has a Post set
        if (comment.getPost() == null) {
            throw new IllegalArgumentException("Comment must be associated with a Post");
        }

        // Ensure the Post's list of comments is updated
        Post post = comment.getPost();
        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        post.getComments().add(comment);

        // Save the comment using the commentRepository
        Comment savedComment = commentRepository.save(comment);

        // Convert to CommentResponse DTO
        return CommentTransformer.convertToCommentResponse(savedComment);
    }

    public Comment getCommentById(Integer commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new IllegalArgumentException("Comment not found");
        }
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }
    
    public CommentResponse updateComment(Comment updatedComment) {
        // Ensure the Comment has a Post set
        if (updatedComment.getPost() == null) {
            throw new IllegalArgumentException("Comment must be associated with a Post");
        }

        // Ensure the Comment has a User set
        if (updatedComment.getUser() == null) {
            throw new IllegalArgumentException("Comment must be associated with a User");
        }

        // Save the updated comment using the commentRepository
        Comment savedComment = commentRepository.save(updatedComment);

        // Convert to CommentResponse DTO
        return CommentTransformer.convertToCommentResponse(savedComment);
    }

    public void deleteComment(Integer commentId, Integer postId, User currentUser) {
        // Fetch the comment from the repository
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Post post = comment.getPost();

        // Check if the comment belongs to the specified post
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        // Ensure the user making the request is the owner of the comment
        if (!comment.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("User not authorized to delete this comment");
        }

        // Remove the comment from the post's comment list and user's comment list
        post.getComments().remove(comment);
        currentUser.getComments().remove(comment);
    

        // Delete the comment from the repository
        commentRepository.delete(comment);
    }
}
