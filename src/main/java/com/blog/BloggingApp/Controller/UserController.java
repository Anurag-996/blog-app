package com.blog.BloggingApp.Controller;

import com.blog.BloggingApp.Entities.CustomUserDetails;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.Jwt.JwtService;
import com.blog.BloggingApp.ResponseDTOs.PostResponse;
import com.blog.BloggingApp.ResponseDTOs.UserResponse;
import com.blog.BloggingApp.Service.PostService;
import com.blog.BloggingApp.Service.UserService;
import com.blog.BloggingApp.Transformers.UserTransformer;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ResponseEntity<Object> authenticatedUser() {
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

            UserResponse userResponse = UserTransformer.convertToUserResponse(currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> findAll() {
        try {
            // Directly obtain and convert the data
            List<UserResponse> userResponses = userService.findAll().stream()
                    .map(UserTransformer::convertToUserResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(userResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(HttpServletRequest request) {
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
            Integer userId = currentUserDetails.getUser().getUserId(); // Assuming `getUserId` returns the user ID

            // Extract the token from the request header
            String token = jwtService.extractTokenFromHeader(request);

            // If a token is present and not blacklisted, blacklist it
            if (token != null && !jwtService.isTokenBlacklisted(token)) {
                Date expirationDate = jwtService.extractExpiration(token);
                jwtService.blacklistToken(token, expirationDate);
            }

            // Delete the user account
            userService.deleteAccount(userId);

            // Clear the security context to ensure the user is logged out
            SecurityContextHolder.clearContext();

            return ResponseEntity.status(HttpStatus.OK).body("User account deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/posts/commented")
    public ResponseEntity<Object> getPostsCommentedByAuthenticatedUser() {
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

            List<PostResponse> postResponses = postService.getPostsCommentedByUser(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(postResponses);

        } catch (IllegalArgumentException e) {
            // Handle cases where the user or data is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> getPostsCreatedByAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = currentUserDetails.getUser();

            List<PostResponse> postResponses = postService.getPostsCreatedByUser(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(postResponses);

        } catch (IllegalArgumentException e) {
            // Handle cases where the user or data is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/posts/liked")
    public ResponseEntity<Object> getPostsLikedByAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = currentUserDetails.getUser();

            List<PostResponse> postResponses = postService.getPostsLikedByUser(currentUser);

            return ResponseEntity.status(HttpStatus.OK).body(postResponses);

        } catch (IllegalArgumentException e) {
            // Handle cases where the user or data is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    // @PostMapping("/forgot-password")
    // public ResponseEntity<Object> forgotPassword(@RequestBody
    // ForgetPasswordRequest forgotPasswordRequest, @RequestBody HttpServletRequest
    // httpServletRequest) {
    // try {
    // // Validate if email exists and send reset link with token
    // userService.generatePasswordResetToken(forgotPasswordRequest,
    // httpServletRequest);
    // return ResponseEntity.status(HttpStatus.OK).body("Reset link sent
    // successfully.");
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    // } catch (Exception e) {
    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    // }
    // }

    // @PutMapping("/reset-password")
    // public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest
    // resetPasswordRequest) {
    // try {
    // // Validate token and update password
    // userService.resetPassword(resetPasswordRequest.getToken(),
    // resetPasswordRequest.getNewPassword());
    // return ResponseEntity.status(HttpStatus.OK).body("Password reset
    // successfully.");
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    // } catch (Exception e) {
    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    // }
    // }
}
