package com.reviewdashboard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reviewdashboard.model.CreateUserRequest;
import com.reviewdashboard.service.AuthService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link AuthController}.
 *
 * <p>This test class validates the behavior of the controller in isolation by mocking the {@link
 * AuthService} dependency. It covers all equivalence partitions for the user creation endpoint,
 * including success, validation failures, and various downstream error conditions.
 */
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock private AuthService authService;

  private AuthController controller;

  @BeforeEach
  public void setUp() {
    controller = new AuthController(authService);
  }

  @Test
  public void testCreateUser_Success() {
    when(authService.createUser(anyString())).thenReturn(ResponseEntity.status(201).build());

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("user123"));

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("User created", response.getBody());
  }

  @Test
  public void testCreateUser_MissingUserId() {
    ResponseEntity<?> response = controller.createUser(new CreateUserRequest(" \t\n"));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Please provide a non-empty userId in the body", response.getBody());
  }

  @Test
  public void testCreateUser_Conflict_UserTaken() {
    when(authService.createUser(anyString()))
        .thenThrow(buildFeignException(409, "User already exists"));

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("user123"));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("This user ID is already taken. Please choose another.", response.getBody());
  }

  @Test
  public void testCreateUser_BadRequest() {
    when(authService.createUser(anyString())).thenThrow(buildFeignException(400, "Invalid userId"));

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("bad id"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid userId. Please try a different value.", response.getBody());
  }

  @Test
  public void testCreateUser_InternalError() {
    when(authService.createUser(anyString())).thenThrow(new RuntimeException("Upstream down"));

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("user123"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create user: Upstream down", response.getBody());
  }

  @Test
  public void testCreateUser_NullRequest() {
    ResponseEntity<?> response = controller.createUser(null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Please provide a non-empty userId in the body", response.getBody());
  }

  @Test
  public void testCreateUser_UnexpectedUpstreamStatus() {
    when(authService.createUser(anyString())).thenReturn(ResponseEntity.status(500).build());

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("user123"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create user: unexpected upstream status", response.getBody());
  }

  @Test
  public void testCreateUser_OtherFeignException() {
    when(authService.createUser(anyString()))
        .thenThrow(buildFeignException(503, "Service Unavailable"));

    ResponseEntity<?> response = controller.createUser(new CreateUserRequest("user123"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  private FeignException buildFeignException(int status, String message) {
    Request request =
        Request.create(
            Request.HttpMethod.POST,
            "/users",
            Collections.emptyMap(),
            message != null ? message.getBytes(StandardCharsets.UTF_8) : null,
            StandardCharsets.UTF_8,
            new RequestTemplate());
    return new FeignException.FeignClientException(status, message, request, null, null);
  }
}
