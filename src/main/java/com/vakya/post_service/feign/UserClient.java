package com.vakya.post_service.feign;

import com.vakya.post_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/users/name/{name}")
    UserResponse getUserByName(@PathVariable("name") String name);
}

