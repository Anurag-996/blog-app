package com.blog.BloggingApp.Service;

import com.blog.BloggingApp.Entities.Post;
import com.blog.BloggingApp.Entities.User;
import com.blog.BloggingApp.Repository.PostRepository;
import com.blog.BloggingApp.ResponseDTOs.PostResponse;
import com.blog.BloggingApp.Transformers.PostTransformer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse createPost(Post post) {
        // Ensure the Post has a User set
        if (post.getUser() == null) {
            throw new IllegalArgumentException("Post must be associated with a User");
        }

        // Ensure the User's list of posts is updated
        User user = post.getUser();
        if (user.getPosts() == null) {
            user.setPosts(new ArrayList<>());
        }
        user.getPosts().add(post);

        // Save the post using the postRepository (User will be saved if needed due to
        // cascading)
        Post savedPost = postRepository.save(post);

        // Convert to PostResponse DTO
        return PostTransformer.convertToPostResponse(savedPost);
    }

    public PostResponse updatePost(Post updatedPost) {
        // Ensure the Post has a User set
        if (updatedPost.getUser() == null) {
            throw new IllegalArgumentException("Post must be associated with a User");
        }

        // Directly save the updated post using the postRepository
        Post savedPost = postRepository.save(updatedPost);

        // Convert to PostResponse DTO
        return PostTransformer.convertToPostResponse(savedPost);
    }

    public Post getPostById(Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    public List<PostResponse> getAllPosts() {
        // Retrieve all posts from the repository
        List<Post> posts = postRepository.findAll();

        // Convert each Post to PostResponse
        return posts.stream()
                .map(PostTransformer::convertToPostResponse) // Convert to PostResponse
                .collect(Collectors.toList()); // Collect into a list
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public List<PostResponse> getPostsCommentedByUser(User user) {
        // Ensure the User is not null
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        // Fetch the posts commented by the user and convert to PostResponse in a single
        // stream
        return user.getComments().stream()
                .map(comment -> comment.getPost())
                .distinct()
                .map(PostTransformer::convertToPostResponse) // Convert to PostResponse
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsLikedByUser(User user) {
        // Ensure the User is not null
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        // Fetch the posts liked by the user and convert to PostResponse in a single
        // stream
        return user.getLikes().stream()
                .map(like -> like.getPost()) // Extract Post from Like
                .distinct() // Ensure posts are unique
                .map(PostTransformer::convertToPostResponse) // Convert Post to PostResponse
                .collect(Collectors.toList()); // Collect into a list
    }

}
