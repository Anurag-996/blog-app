package com.blog.BloggingApp.Controller;

import com.blog.BloggingApp.Entities.CustomUserDetails;
import com.blog.BloggingApp.Entities.RefreshToken;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.Jwt.JwtService;
import com.blog.BloggingApp.RequestDTOs.AddUserRequest;
import com.blog.BloggingApp.RequestDTOs.RefreshTokenRequestDTO;
import com.blog.BloggingApp.RequestDTOs.UserLoginRequest;
import com.blog.BloggingApp.ResponseDTOs.LoginResponse;
import com.blog.BloggingApp.Service.AuthenticationService;
import com.blog.BloggingApp.Service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> register(@RequestBody AddUserRequest addUserRequest) {
        try {
            LoginResponse loginResponse = authenticationService.registerAndLogin(addUserRequest);
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginRequest userLoginRequest) {
        try {
            LoginResponse loginResponse = authenticationService.login(userLoginRequest);
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Ensure the user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }

            if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user details");
            }

            // Get the authenticated user's details
            CustomUserDetails currentUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = currentUserDetails.getUser();

            // Validate the refresh token
            if (user.getRefreshToken() == null
                    || !user.getRefreshToken().getToken().equals(refreshTokenRequestDTO.getToken())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token");
            }

            String newAccessToken = refreshTokenService.refreshAccessToken(refreshTokenRequestDTO.getToken(),
                    jwtService);

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshTokenRequestDTO.getToken())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        try {
            // Extract the access token from the request header
            String accessToken = jwtService.extractTokenFromHeader(request);

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
            User user = currentUserDetails.getUser(); // Assuming CustomUserDetails has a getUser() method

            // Retrieve the refresh token associated with the user
            RefreshToken refreshToken = user.getRefreshToken();

            // Blacklist the access token
            if (accessToken != null) {
                if (jwtService.isTokenBlacklisted(accessToken)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access token is already blacklisted");
                }

                // Blacklist the access token
                Date expirationDate = jwtService.extractExpiration(accessToken);
                jwtService.blacklistToken(accessToken, expirationDate);
            }

            // Delete the refresh token from the database
            if (refreshToken != null) {
                refreshTokenService.deleteToken(refreshToken);
            }

            // Return a success response
            return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
        } catch (Exception e) {
            // Handle exceptions and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
