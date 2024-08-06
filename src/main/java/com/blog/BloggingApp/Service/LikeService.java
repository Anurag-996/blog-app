package com.blog.BloggingApp.Service;

import com.blog.BloggingApp.Entities.Like;
import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.Repository.LikeRepository;
import com.blog.BloggingApp.ResponseDTOs.LikeResponse;
import com.blog.BloggingApp.Transformers.LikeTransformer;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;

    public LikeService(LikeRepository likeRepository, PostService postService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
    }

    public LikeResponse likePost(Like like) {
        // Ensure the Like has a Post set
        if (like.getPost() == null) {
            throw new IllegalArgumentException("Like must be associated with a Post");
        }

        // Ensure the Like has a User set
        if (like.getUser() == null) {
            throw new IllegalArgumentException("Like must be associated with a User");
        }

        // Fetch the post associated with the like
        Post post = like.getPost();
        // Ensure the Post's list of likes is updated
        if (post.getLikes() == null) {
            post.setLikes(new ArrayList<>());
        }
        post.getLikes().add(like);

        // Fetch the user who liked the post
        User user = like.getUser();

        // Ensure the User's list of likes is updated
        if (user.getLikes() == null) {
            user.setLikes(new ArrayList<>());
        }
        user.getLikes().add(like);

        // Save the like using the likeRepository
        Like savedLike = likeRepository.save(like);

        // Convert to LikeResponse DTO
        return LikeTransformer.convertToLikeResponse(savedLike);
    }

    public List<Like> getLikesByUser(User user) {
        return likeRepository.findByUser(user);
    }
    
    public void unlikePost(User user, Integer postId) {
        // Fetch the post by ID
        Post post = postService.getPostById(postId);

        // Find the like entry in the repository
        Optional<Like> likeOpt = likeRepository.findByUserAndPost(user, post);

        if (likeOpt.isPresent()) {
            Like like = likeOpt.get();

            // Remove the like from the user's list of likes
            user.getLikes().remove(like);

            // Remove the like from the post's list of likes
            post.getLikes().remove(like);

            // Delete the like from the repository
            likeRepository.delete(like);
        } else {
            throw new IllegalArgumentException("Like not found for this user and post.");
        }
    }

}
