package com.vakya.post_service.controller;

import com.vakya.post_service.model.Post;
import com.vakya.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

//    @PostMapping("/create")
//    public Post createPost(@RequestBody Post post, @RequestParam String tagsString, @RequestHeader("X-User-Email") String email){
//        return postService.createPost(post, tagsString,email);
//    }

    @PostMapping("/create")
    public Post createPost(@RequestBody Post post, @RequestParam String tagsString, @RequestHeader("X-User-Name") String name){
        return postService.createPost(post, tagsString,name);
    }

    @GetMapping("/all")
    public Page<Post> getAllPosts(@RequestParam(defaultValue = "0") int page,
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
    public Post updatePost(
            @PathVariable("id") Long id,
            @RequestBody Post updatedPost,
            @RequestParam(required = false) String tagsInput) {

        List<String> tags = new ArrayList<>();
        if (tagsInput != null && !tagsInput.isBlank()) {
            tags = Arrays.asList(tagsInput.split("\\s*,\\s*"));
        }

        return postService.updatePost(id, updatedPost, tags);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.ok("post deleted");
    }

}
