package com.vakya.post_service.service;

import com.vakya.post_service.dto.UserResponse;
import com.vakya.post_service.feign.UserClient;
import com.vakya.post_service.model.Post;
import com.vakya.post_service.model.Tag;
import com.vakya.post_service.repository.PostRepository;
import com.vakya.post_service.repository.TagRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserClient userClient;

    public PostService(PostRepository postRepository, TagRepository tagRepository, UserClient userClient) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userClient = userClient;
    }


    public Post createPost(Post post, String tagsString,String loggedInEmail) {
        if (loggedInEmail == null || loggedInEmail.isBlank()) {
            throw new RuntimeException("Username header missing");
        }

        UserResponse user = userClient.getUserByEmail(loggedInEmail);
        post.setAuthor(user.getUsername());

        post.setTitle(post.getTitle());
        post.setExcerpt(post.getExcerpt());
        post.setContent(post.getContent());
//        post.setAuthor(post.getAuthor());
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        List<Tag> tags = new ArrayList<>();
        if(tagsString!=null){
            String[] tagNames = tagsString.split(",");
            for(String tagName :tagNames){
                tagName = tagName.trim().toLowerCase();
                if(!tagName.isEmpty()){
                    List<Tag> existingTags = tagRepository.findAllByName(tagName);
                    Tag tag;

                    if (existingTags.isEmpty()) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag = tagRepository.save(tag);
                    } else {
                        tag = existingTags.get(0);
                    }

                    tags.add(tag);
                }
            }

        }
        post.setTagList(tags);
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findByIdIs(id);
    }

    public Page<Post> getFilteredPosts(int page, int size, String sortDir,
                                       String query, List<String> authors, List<String> tags) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());

        if ("asc".equalsIgnoreCase(sortDir)) {
            pageable = PageRequest.of(page, size, Sort.by("publishedAt").ascending());
        }

        if ((authors != null && !authors.isEmpty()) && (tags != null && !tags.isEmpty())
                && query != null && !query.isEmpty()) {
            return postRepository.findByQueryAndAuthorsAndTags(query, authors, tags, pageable);
        }

        if ((authors != null && !authors.isEmpty()) && (tags != null && !tags.isEmpty())) {
            return postRepository.findByAuthorsAndTags(authors, tags, pageable);
        }

        if ((authors != null && !authors.isEmpty()) && query != null && !query.isEmpty()) {
            return postRepository.findByQueryAndAuthors(query, authors, pageable);
        }

        if ((tags != null && !tags.isEmpty()) && query != null && !query.isEmpty()) {
            return postRepository.findByQueryAndTags(query, tags, pageable);
        }

        if (authors != null && !authors.isEmpty()) {
            return postRepository.findByAuthorIn(authors, pageable);
        }

        if (tags != null && !tags.isEmpty()) {
            return postRepository.findByTagsIn(tags, pageable);
        }

        if (query != null && !query.isEmpty()) {
            return postRepository.searchPosts(query, pageable);
        }

        return postRepository.findAll(pageable);
    }

    public Post updatePost(Long id, Post updatedPost, List<String> tags) {
        Post existingPost = getPostById(id);

        if (updatedPost.getTitle() != null && !updatedPost.getTitle().isBlank()) {
            existingPost.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getExcerpt() != null && !updatedPost.getExcerpt().isBlank()) {
            existingPost.setExcerpt(updatedPost.getExcerpt());
        }
        if (updatedPost.getContent() != null && !updatedPost.getContent().isBlank()) {
            existingPost.setContent(updatedPost.getContent());
        }
        if (updatedPost.getAuthor() != null && !updatedPost.getAuthor().isBlank()) {
            existingPost.setAuthor(updatedPost.getAuthor());
        }

        existingPost.setUpdatedAt(LocalDateTime.now());

        if (tags != null && !tags.isEmpty()) {
            List<Tag> tagList = new ArrayList<>();
            for (String tagName : tags) {
                tagName = tagName.toLowerCase();
                Tag tag = new Tag();
                tag.setName(tagName);
                tagList.add(tag);
            }
            existingPost.setTagList(tagList);
        }

        return postRepository.save(existingPost);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findByIdIs(id);

        if (post != null) {
            post.getTagList().clear();
            postRepository.save(post);
            postRepository.delete(post);
        }
    }
}
