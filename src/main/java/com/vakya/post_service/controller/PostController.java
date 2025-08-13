package com.vakya.post_service.controller;

import com.vakya.post_service.feign.UserClient;
import com.vakya.post_service.model.Post;
import com.vakya.post_service.service.PostService;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;
    private final UserClient userClient;

    public PostController(PostService postService, UserClient userClient) {
        this.postService = postService;
        this.userClient = userClient;
    }

    @PostMapping("/create")
    public Post createPost(@RequestBody Post post,
                           @RequestParam String tagsString,
                           @RequestHeader("Authorization") String token) {
        String username;
        try {
            String header = token != null && token.startsWith("Bearer ") ? token : ("Bearer " + token);
            username = userClient.validateToken(header);
        } catch (FeignException.Unauthorized e) {
            throw new RuntimeException("Invalid or expired token");
        }

        return postService.createPost(post, tagsString, username);
    }


    @GetMapping("/api/posts/all")
    public Page<Post> getAllPostsApi(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "desc") String sortDir,
                                         @RequestParam(required = false) String query,
                                         @RequestParam(required = false) List<String> author,
                                         @RequestParam(required = false) List<String> tag) {
        Page<Post> postPage = postService.getFilteredPosts(page, 10, sortDir, query, author, tag);
        return postPage;
    }

    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PutMapping("update/{id}")
    public Post updatePost(@PathVariable("id") Long id, @RequestBody Post updatedPost,
                           @RequestParam(required = false) String tagsInput,
                           @RequestHeader("Authorization") String token) {
        String username;
        try {
            String header = token != null && token.startsWith("Bearer ") ? token : ("Bearer " + token);
            username = userClient.validateToken(header);
        } catch (FeignException.Unauthorized e) {
            throw new RuntimeException("Invalid or expired token");
        }
        postService.verifyOwnership(id, username);

        List<String> tags = new ArrayList<>();
        if (tagsInput != null && !tagsInput.isBlank()) {
            tags = Arrays.asList(tagsInput.split("\\s*,\\s*"));
        }

        return postService.updatePost(id, updatedPost, tags);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        String username;
        try {
            String header = token != null && token.startsWith("Bearer ") ? token : ("Bearer " + token);
            username = userClient.validateToken(header);
        } catch (FeignException.Unauthorized e) {
            throw new RuntimeException("Invalid or expired token");
        }
        postService.verifyOwnership(id, username);

        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted");
    }

    @PostMapping("/posts/{postId}/add-comment/{commentId}")
    public void addCommentToPost(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.addCommentIdToPost(postId, commentId);
    }
}
