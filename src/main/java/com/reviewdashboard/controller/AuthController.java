package com.reviewdashboard.controller;

import com.reviewdashboard.model.CreateUserRequest;
import com.reviewdashboard.service.AuthService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller exposing a local endpoint to create users via the auth service. */
@RestController
@RequestMapping("auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Creates a new user ID by delegating to the auth service.
   *
   * @param request The request containing the desired {@code userId}.
   * @return 201 Created on success; readable 400/409/500 messages on failure.
   */
  @PostMapping("users")
  public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
    String userId = request != null ? request.getUserId() : null;
    if (userId == null || userId.trim().isEmpty()) {
      logger.warn("userId missing or blank in createUser request");
      return ResponseEntity.badRequest().body("Please provide a non-empty userId in the body");
    }

    try {
      ResponseEntity<Void> response = authService.createUser(userId);
      if (response.getStatusCode().is2xxSuccessful()) {
        logger.info("User created successfully: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created");
      }
      // Fallback: unexpected status from upstream
      logger.error("Unexpected status from auth service: {}", response.getStatusCode());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create user: unexpected upstream status");

    } catch (FeignException e) {
      int status = e.status();
      if (status == 409) {
        logger.warn("User ID already exists: {}", userId);
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("This user ID is already taken. Please choose another.");
      }
      if (status == 400) {
        logger.warn("Invalid userId provided: {}", userId);
        return ResponseEntity.badRequest().body("Invalid userId. Please try a different value.");
      }
      logger.error("Auth service error while creating userId={}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create user: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Unexpected error while creating userId={}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to create user: " + e.getMessage());
    }
  }
}
