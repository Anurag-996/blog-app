package com.blog.BloggingApp.Transformers;

import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.RequestDTOs.CommentRequestDTO;
import com.blog.BloggingApp.ResponseDTOs.CommentResponse;

public class CommentTransformer {
    public static Comment convertToComment(CommentRequestDTO commentRequestDTO){
        return Comment.builder()
        .content(commentRequestDTO.getContent())
        .build();
    }

    public static CommentResponse convertToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
