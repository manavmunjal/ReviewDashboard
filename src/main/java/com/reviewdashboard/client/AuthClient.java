package com.reviewdashboard.client;

import com.reviewdashboard.model.CreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A Feign client for communicating with the Authentication service.
 *
 * <p>This interface defines a method to create users via the auth service.
 */
@FeignClient(name = "authClient", url = "${auth.client.url}")
public interface AuthClient {

  /**
   * Creates a new user with a chosen userId.
   *
   * @param request The create-user request payload containing {@code userId}.
   * @return A {@link ResponseEntity} with no body. Status 201 on success.
   */
  @PostMapping("/users")
  ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request);
}
