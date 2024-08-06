package com.blog.BloggingApp.ResponseDTOs;

import java.util.Date;
import java.util.List;
import com.blog.BloggingApp.Entities.Comment;
import com.blog.BloggingApp.Entities.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private Integer postId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private List<Comment> comments;
    private List<Like> likes;
}
