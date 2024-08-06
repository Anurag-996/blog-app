package com.blog.BloggingApp.Controller;

import com.blog.BloggingApp.Entities.CustomUserDetails;
import com.blog.BloggingApp.Entities.Like;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.RequestDTOs.LikeRequestDTO;
import com.blog.BloggingApp.ResponseDTOs.LikeResponse;
import com.blog.BloggingApp.Service.LikeService;
import com.blog.BloggingApp.Service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    private final LikeService likeService;
    private final PostService postService;

    public LikeController(LikeService likeService, PostService postService) {
        this.likeService = likeService;
        this.postService = postService;
    }

    @PostMapping("/like")
    public ResponseEntity<Object> likePost(@RequestBody LikeRequestDTO likeRequestDTO) {
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

            Post post = postService.getPostById(likeRequestDTO.getPostId());
            Like like = Like.builder()
                    .user(currentUser)
                    .post(post)
                    .build();

            LikeResponse likeResponse = likeService.likePost(like);
            return ResponseEntity.status(HttpStatus.CREATED).body(likeResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/posts/{postId}/unlike")
    public ResponseEntity<Object> unlikePost(
            @PathVariable Integer postId) {
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

            // Call the service method to unlike the post
            likeService.unlikePost(currentUser, postId);

            return ResponseEntity.status(HttpStatus.OK).body("Post unliked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to unlike post: " + e.getMessage());
        }
    }

}
