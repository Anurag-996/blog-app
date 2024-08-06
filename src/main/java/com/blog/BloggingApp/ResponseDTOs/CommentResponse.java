package com.blog.BloggingApp.ResponseDTOs;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Integer commentId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
