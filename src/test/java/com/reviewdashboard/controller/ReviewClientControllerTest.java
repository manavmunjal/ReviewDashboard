package com.reviewdashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import feign.FeignException;
import feign.Request;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewClientController.class)
class ReviewClientControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ReviewService reviewService;
  @MockBean private CompanyService companyService;

  private ReviewDto validReview;

  @BeforeEach
  void setup() {
    validReview = new ReviewDto();
    validReview.setRating(4);
    validReview.setComment("Good product");
  }

  // =======================================================================
  // addReview() tests
  // =======================================================================

  @Test
  void addReview_success() throws Exception {
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(validReview);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(4));
  }

  @Test
  void addReview_ratingLowerBoundary_success() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(1);
    review.setComment("Lower boundary test");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(1));
  }

  @Test
  void addReview_ratingUpperBoundary_success() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(5);
    review.setComment("Upper boundary test");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(5));
  }

  @Test
  void addReview_invalidRatingLow() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(0);
    review.setComment("Invalid rating test");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addReview_invalidRatingHigh() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(6);
    review.setComment("Invalid rating high test");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addReview_nullComment() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(3);
    review.setComment(null);

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(3));
  }

  @Test
  void addReview_runtimeException_returns500() throws Exception {
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new RuntimeException("Service down"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void missingUserIdHeader_returns400() throws Exception {
    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isBadRequest());
  }

  // =======================================================================
  // getProductAverageRating() tests
  // =======================================================================

  @Test
  void getProductAverageRating_success() throws Exception {
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(4.5));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk())
        .andExpect(content().string("4.5"));
  }

  @Test
  void getProductAverageRating_nullBody_returns404() throws Exception {
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(null));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getProductAverageRating_notFound() throws Exception {
    FeignException.NotFound ex =
        new FeignException.NotFound(
            "Not found",
            Request.create(
                Request.HttpMethod.GET, "", Collections.emptyMap(), null, StandardCharsets.UTF_8),
            null,
            null);

    Mockito.when(reviewService.getAverageRating("123", "U1")).thenThrow(ex);

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getProductAverageRating_internalError() throws Exception {
    Mockito.when(reviewService.getAverageRating("123", "U1"))
        .thenThrow(new RuntimeException("Service down"));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }

  // =======================================================================
  // getCompanyAverageRating() tests
  // =======================================================================

  @Test
  void getCompanyAverageRating_success() throws Exception {
    Mockito.when(companyService.getAverageRating("C1", "U1")).thenReturn(ResponseEntity.ok(3.7));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk())
        .andExpect(content().string("3.7"));
  }

  @Test
  void getCompanyAverageRating_nullBody_returns404() throws Exception {
    Mockito.when(companyService.getAverageRating("C1", "U1")).thenReturn(ResponseEntity.ok(null));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getCompanyAverageRating_notFound() throws Exception {
    FeignException.NotFound ex =
        new FeignException.NotFound(
            "Not found",
            Request.create(
                Request.HttpMethod.GET, "", Collections.emptyMap(), null, StandardCharsets.UTF_8),
            null,
            null);

    Mockito.when(companyService.getAverageRating("C1", "U1")).thenThrow(ex);

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getCompanyAverageRating_internalError() throws Exception {
    Mockito.when(companyService.getAverageRating("C1", "U1"))
        .thenThrow(new RuntimeException("Service down"));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void addReview_nullBody_returns400() throws Exception {
    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(""))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addReview_nullRating_returns400() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(0.0);
    review.setComment("No rating");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addReview_emptyComment_success() throws Exception {
    ReviewDto review = new ReviewDto();
    review.setRating(3);
    review.setComment("");

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(3))
        .andExpect(jsonPath("$.comment").value(""));
  }

  @Test
  void getProductAverageRating_missingUserId_returns400() throws Exception {
    mockMvc.perform(get("/review/product/123/average-rating")).andExpect(status().isBadRequest());
  }

  @Test
  void getCompanyAverageRating_missingUserId_returns400() throws Exception {
    mockMvc.perform(get("/review/company/C1/average-rating")).andExpect(status().isBadRequest());
  }

  // FeignException handling edge case (other than NotFound)
  @Test
  void getProductAverageRating_feignOtherException_returns500() throws Exception {
    FeignException.InternalServerError ex =
        new FeignException.InternalServerError(
            "Internal Error",
            Request.create(
                Request.HttpMethod.GET, "", Collections.emptyMap(), null, StandardCharsets.UTF_8),
            null,
            null);

    Mockito.when(reviewService.getAverageRating("123", "U1")).thenThrow(ex);

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getCompanyAverageRating_feignOtherException_returns500() throws Exception {
    FeignException.InternalServerError ex =
        new FeignException.InternalServerError(
            "Internal Error",
            Request.create(
                Request.HttpMethod.GET, "", Collections.emptyMap(), null, StandardCharsets.UTF_8),
            null,
            null);

    Mockito.when(companyService.getAverageRating("C1", "U1")).thenThrow(ex);

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());
  }
}
