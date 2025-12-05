package com.reviewdashboard.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewdashboard.model.CreateUserRequest;
import com.reviewdashboard.service.AuthService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration-level Web MVC tests for {@link AuthController}.
 *
 * <p>These tests validate request/response behavior at the controller layer by mocking {@link
 * AuthService} and invoking the controller endpoints via {@link MockMvc}.
 *
 * <p>The test suite uses equivalence partitioning to validate:
 *
 * <ul>
 *   <li><b>Valid user creation</b> → service returns 201 CREATED
 *   <li><b>Duplicate user conflict</b> → service throws FeignException(409)
 * </ul>
 */
@WebMvcTest(AuthController.class)
public class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthService authService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
  }

  /**
   * Tests the successful user-creation workflow.
   *
   * <p><b>Equivalence Partition:</b> This test represents the partition where:
   *
   * <ul>
   *   <li>Input userId is valid
   *   <li>No user exists with this ID
   *   <li>AuthService successfully creates the user
   * </ul>
   *
   * <p><b>Expected Behavior:</b> Controller should return:
   *
   * <ul>
   *   <li>HTTP 201 Created
   *   <li>Body: "User created"
   * </ul>
   */
  @Test
  public void testCreateUser_Success() throws Exception {
    when(authService.createUser(anyString())).thenReturn(ResponseEntity.status(201).build());

    mockMvc
        .perform(
            post("/auth/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new CreateUserRequest("user999"))))
        .andExpect(status().isCreated())
        .andExpect(content().string("User created"));
  }

  /**
   * Tests the scenario where the user ID already exists.
   *
   * <p><b>Equivalence Partition:</b> This test covers the partition where:
   *
   * <ul>
   *   <li>Input userId is syntactically valid
   *   <li>A user with this ID already exists
   *   <li>AuthService throws a FeignException with status 409
   * </ul>
   *
   * <p><b>Expected Behavior:</b> Controller should map Feign 409 → HTTP 409 Conflict and return:
   *
   * <blockquote>
   *
   * "This user ID is already taken. Please choose another."
   *
   * </blockquote>
   */
  @Test
  public void testCreateUser_Conflict() throws Exception {
    when(authService.createUser(anyString()))
        .thenThrow(buildFeignException(409, "User already exists"));

    mockMvc
        .perform(
            post("/auth/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new CreateUserRequest("user999"))))
        .andExpect(status().isConflict())
        .andExpect(content().string("This user ID is already taken. Please choose another."));
  }

  /**
   * Helper method to construct a {@link FeignException} with the given status and message.
   *
   * <p>Used for simulating error responses from downstream Feign clients.
   *
   * @param status the HTTP status code to simulate
   * @param message error message included in the exception body
   * @return a configured {@link FeignException}
   */
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
