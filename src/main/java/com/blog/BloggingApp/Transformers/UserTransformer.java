package com.blog.BloggingApp.Transformers;

import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.ResponseDTOs.UserResponse;

public class UserTransformer {
    public static UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .emailId(user.getEmailId())
                .createdAt(user.getCreatedAt())
                .userName(user.getUserName())
                .userId(user.getUserId())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus())
                .build();
    }
}
