package com.reviewdashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.model.UserDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link ReviewClientController}.
 *
 * <p>Tests the main endpoints of the controller, including success and not-found scenarios.
 */
@WebMvcTest(ReviewClientController.class)
public class ReviewClientControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReviewService reviewService;

  @MockBean
  private CompanyService companyService;

  private ObjectMapper objectMapper;
  private ReviewDto review;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();

    // Create a sample review
    review = new ReviewDto();
    review.setComment("Great product!");
    review.setRating(5);
    UserDto user = new UserDto();
    user.setUsername("testuser");
    review.setUser(user);
  }

  /**
   * Test scenario: Successfully adding a review.
   */
  @Test
  public void testAddReview_Success() throws Exception {
    when(reviewService.addReview(anyString(), any(ReviewDto.class))).thenReturn(review);

    mockMvc.perform(post("/review/product/123")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.comment").value("Great product!"))
        .andExpect(jsonPath("$.rating").value(5))
        .andExpect(jsonPath("$.user.username").value("testuser"));
  }

  /**
   * Test scenario: Fetch average rating for a product that exists.
   */
  @Test
  public void testGetProductAverageRating_Success() throws Exception {
    when(reviewService.getAverageRating("123")).thenReturn(ResponseEntity.ok(4.5));

    mockMvc.perform(get("/review/product/123/average-rating"))
        .andExpect(status().isOk())
        .andExpect(content().string("4.5"));
  }

  /**
   * Test scenario: Fetch average rating for a product that does not exist (404).
   */
  @Test
  public void testGetProductAverageRating_NotFound() throws Exception {
    when(reviewService.getAverageRating("123")).thenReturn(ResponseEntity.ok(null));

    mockMvc.perform(get("/review/product/123/average-rating"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("No reviews found for productId: 123"));
  }
}
