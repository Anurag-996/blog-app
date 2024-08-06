package com.blog.BloggingApp.Controller;

import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.Entities.CustomUserDetails;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.RequestDTOs.CommentRequestDTO;
import com.blog.BloggingApp.ResponseDTOs.CommentResponse;
import com.blog.BloggingApp.Service.CommentService;
import com.blog.BloggingApp.Service.PostService;
import com.blog.BloggingApp.Transformers.CommentTransformer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        try {
            // Get the authentication object from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if the user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            // Ensure the principal is of the expected type
            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            // Get the authenticated user's details
            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = currentUserDetails.getUser();

            // Retrieve the post by its ID
            Post post = postService.getPostById(commentRequestDTO.getPostId());
            // Create and set the comment details
            Comment comment = CommentTransformer.convertToComment(commentRequestDTO);
            comment.setUser(currentUser);
            comment.setPost(post);

            CommentResponse commentResponse = commentService.createComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Object> getComment(@PathVariable Integer commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            return ResponseEntity.status(HttpStatus.OK).body(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/edit/{commentId}")
    public ResponseEntity<Object> editComment(@PathVariable Integer commentId,
            @RequestBody CommentRequestDTO commentRequestDTO) {
        try {
            // Get the authentication object from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if the user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            // Ensure the principal is of the expected type
            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            // Get the authenticated user's details
            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = currentUserDetails.getUser();

            // Find the existing comment by ID
            Comment existingComment = commentService.getCommentById(commentId);

            // Check if the comment belongs to the authenticated user
            if (!existingComment.getUser().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User does not have permission to edit this comment");
            }

            // Update the fields of the existing comment
            existingComment.setContent(commentRequestDTO.getContent());
            existingComment.setUpdatedAt(new Date());

            // Save the updated comment
            CommentResponse updatedComment = commentService.updateComment(existingComment);

            return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
        } catch (IllegalArgumentException e) {
            // Handle specific exception for validation and authorization issues
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId) {
        try {
            // Get the authentication object from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if the user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            // Ensure the principal is of the expected type
            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            // Get the authenticated user's details
            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = currentUserDetails.getUser();

            // Delete the comment using the service layer
            commentService.deleteComment(commentId, postId, currentUser);

            return ResponseEntity.status(HttpStatus.OK).body("Comment deleted successfully");
        } catch (IllegalArgumentException e) {
            // Handle specific exception for validation and authorization issues
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the comment");
        }
    }

}
