package com.blog.BloggingApp.Transformers;

import java.util.ArrayList;

import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.RequestDTOs.PostRequestDTO;
import com.blog.BloggingApp.ResponseDTOs.PostResponse;

public class PostTransformer {

    public static Post convertToPost(PostRequestDTO postRequestDTO) {
        return Post.builder()
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .build();
    }

    public static PostResponse convertToPostResponse(Post post) {
        return PostResponse.builder()
                .title(post.getTitle())
                .postId(post.getPostId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(post.getComments() != null && !post.getComments().isEmpty() ? post.getComments()
                        : new ArrayList<>())
                .likes(post.getLikes() != null ? post.getLikes() : new ArrayList<>())
                .build();
    }

}
