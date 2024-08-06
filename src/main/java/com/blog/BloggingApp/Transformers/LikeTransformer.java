package com.blog.BloggingApp.Transformers;

import com.blog.BloggingApp.Entities.Like;
import com.blog.BloggingApp.ResponseDTOs.LikeResponse;

public class LikeTransformer {
    public static LikeResponse convertToLikeResponse(Like like) {
        return LikeResponse.builder()
                .likeId(like.getLikeId())
                .postId(like.getPost().getPostId())
                .likedBy(like.getUser().getUserName())
                .build();
    }
}
