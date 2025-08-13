package com.vakya.post_service.feign;

import com.vakya.post_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/name/{name}")
    UserResponse getUserByName(@PathVariable("name") String name);

    @PostMapping("/users/{id}/posts/{postId}")
    void addPostIdToUser(@PathVariable("id") Integer userId, @PathVariable("postId") Long postId);

    @GetMapping("/users/validate-token")
    String validateToken(@RequestHeader("Authorization") String token);
}

