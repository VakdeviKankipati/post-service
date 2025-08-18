package com.vakya.post_service.controller;

import com.vakya.post_service.feign.UserClient;
import com.vakya.post_service.model.Post;
import com.vakya.post_service.model.Tag;
import com.vakya.post_service.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostControllerTest {

    @Autowired
    private PostController postController;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private PostService postService;

    @Test
    void createPost() {
        Post post = new Post();
        post.setTitle("spring");
        post.setExcerpt("java is good");
        post.setContent("java is object oriented language");

        List<Tag> tagList = new ArrayList<>();
        Tag javaTag = new Tag();
        javaTag.setName("java");

        Tag springTag = new Tag();
        springTag.setName("spring");

        tagList.add(javaTag);
        tagList.add(springTag);

        post.setTagList(tagList);

        when(userClient.validateToken("Bearer vakyaToken")).thenReturn("vakya");
        when(postService.createPost(post, "java", "vakya")).thenReturn(post);

        Post response = postController.createPost(post,"java","vakyaToken");

        assertEquals(response,post);
    }

    @Test
    void getAllPostsApi() {
        Post postOne = new Post();
        postOne.setTitle("java");
        postOne.setExcerpt("java is good");
        postOne.setContent("java is object oriented language");

        Post postTwo = new Post();
        postTwo.setTitle("java");
        postTwo.setExcerpt("java is good");
        postTwo.setContent("java is object oriented language");

        Post postThree = new Post();
        postThree.setTitle("java");
        postThree.setExcerpt("java is good");
        postThree.setContent("java is object oriented language");

        List<Post> postList = Arrays.asList(postOne,postTwo,postThree);

        Page<Post> postPage = new PageImpl<>(postList);

        when(postService.getFilteredPosts(0, 10, "desc", null, null, null))
                .thenReturn(postPage);

        Page<Post> response = postController.getAllPostsApi(0,"desc",null,null,null);

        assertEquals(response,postPage);
    }

    @Test
    void getPostById() {
        Post post = new Post();
        long id = 1;
        post.setId(id);
        post.setTitle("java");
        post.setExcerpt("java is good");
        post.setContent("java is object oriented language");

        when(postService.getPostById(id)).thenReturn(post);

        Post response = postController.getPostById(id);

        assertEquals(post, response);
    }

    @Test
    void updatePost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("spring");
        post.setExcerpt("java is good");
        post.setContent("java is object oriented language");

        List<Tag> tagList = new ArrayList<>();
        Tag javaTag = new Tag();
        javaTag.setName("java");

        Tag springTag = new Tag();
        springTag.setName("spring");

        tagList.add(javaTag);
        tagList.add(springTag);

        post.setTagList(tagList);

        when(userClient.validateToken("Bearer vakyaToken")).thenReturn("vakya");
        when(postService.updatePost(1L,post, Collections.singletonList("java"))).
                thenReturn(post);

        Post response = postController.updatePost(1L,post,"java","vakyaToken");

        assertEquals(post,response);
    }

    @Test
    void deletePost() {
        when(userClient.validateToken("Bearer " + "vakyaToken")).thenReturn("vakya");
        doNothing().when(postService).verifyOwnership(1L, "vakya");
        doNothing().when(postService).deletePost(1L);
        ResponseEntity<String> response = postController.deletePost(1L, "vakyaToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Post deleted", response.getBody());
    }

    @Test
    void addCommentToPost() {

        Long postId = 1L;
        Long commentId = 1L;

        doNothing().when(postService).addCommentIdToPost(postId, commentId);

        postController.addCommentToPost(postId, commentId);

        verify(postService, times(1)).addCommentIdToPost(postId, commentId);
    }
}