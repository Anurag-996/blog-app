package com.blog.BloggingApp.Controller;

import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.Entities.CustomUserDetails;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.RequestDTOs.PostRequestDTO;
import com.blog.BloggingApp.ResponseDTOs.PostResponse;
import com.blog.BloggingApp.Service.CommentService;
import com.blog.BloggingApp.Service.PostService;
import com.blog.BloggingApp.Transformers.PostTransformer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@RequestBody PostRequestDTO postRequestDTO) {
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

            // Create the post and set the current user as the author
            Post post = PostTransformer.convertToPost(postRequestDTO);
            post.setUser(currentUser);

            // Save the post using the service layer
            PostResponse createdPost = postService.createPost(post);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPost(@PathVariable Integer postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.status(HttpStatus.OK).body(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPosts() {
        try {
            List<PostResponse> posts = postService.getAllPosts();
            if(posts.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Post Found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<Object> getCommentsByPost(@PathVariable Integer postId) {
        try {
            Post post = postService.getPostById(postId);
            List<Comment> comments = commentService.getCommentsByPost(post);
            return ResponseEntity.status(HttpStatus.OK).body(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/edit/{postId}")
    public ResponseEntity<Object> editPost(@PathVariable Integer postId, @RequestBody PostRequestDTO postRequestDTO) {
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

            // Fetch the existing post from the database (assuming a PostService exists)
            Post existingPost = postService.getPostById(postId); // You need to implement this method in your
                                                                 // PostService

            // Check if the post exists
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }

            // Check if the post belongs to the current user
            if (!existingPost.getUser().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to update this post");
            }

            // Update the post
            existingPost.setTitle(postRequestDTO.getTitle());
            existingPost.setContent(postRequestDTO.getContent());
            existingPost.setUpdatedAt(new Date());

            // Save the updated post (assuming savePost is a method in your PostService)
            PostResponse updatedPost = postService.updatePost(existingPost);

            return ResponseEntity.status(HttpStatus.OK).body(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Integer postId) {
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

            // Fetch the existing post from the database
            Post existingPost = postService.getPostById(postId);

            // Check if the post exists
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }

            // Check if the post belongs to the current user
            if (!existingPost.getUser().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You do not have permission to delete this post");
            }

            // Delete the post
            postService.deletePost(existingPost);

            return ResponseEntity.status(HttpStatus.OK).body("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
