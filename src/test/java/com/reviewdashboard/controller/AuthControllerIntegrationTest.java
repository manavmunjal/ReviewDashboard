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

@WebMvcTest(AuthController.class)
public class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthService authService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
  }

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
