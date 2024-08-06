package com.blog.BloggingApp.RequestDTOs;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;

    private String newPassword;
}
