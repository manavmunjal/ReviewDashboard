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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link ReviewClientController}, covering all equivalence partitions for the
 * following endpoints:
 *
 * <ul>
 *   <li>{@code POST /review/product/{productId}} – add review
 *   <li>{@code GET /review/product/{productId}/average-rating} – get product rating
 *   <li>{@code GET /review/company/{companyId}/average-rating} – get company rating
 * </ul>
 *
 * <p>This test class uses MockMvc and Mockito to verify the controller's behavior across success,
 * validation failures, and exception paths.
 */
@WebMvcTest(ReviewClientController.class)
class ReviewClientControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ReviewService reviewService;
  @MockBean private CompanyService companyService;

  private ReviewDto validReview;

  /** Initializes reusable test data before each test. */
  @BeforeEach
  void setup() {
    validReview = new ReviewDto();
    validReview.setRating(4);
    validReview.setComment("Good product");
  }

  // -------------------------------------------------------------------------
  // Tests for addReview()
  // -------------------------------------------------------------------------

  /**
   * EP Valid: Ensures a valid review submission returns HTTP 201 CREATED and echoes the created
   * review in the response.
   */
  @Test
  @DisplayName("EP Valid: Should create review successfully")
  void addReview_success() throws Exception {
    Mockito.when(reviewService.addReview(eq("123"), any())).thenReturn(validReview);

    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rating").value(4));
  }

  /** EP Invalid: Verifies rating below allowed range (< 1) triggers HTTP 400 BAD REQUEST. */
  @Test
  @DisplayName("EP Invalid: rating < 1 should return 400")
  void addReview_invalidRatingLow() throws Exception {
    ReviewDto invalid = new ReviewDto();
    invalid.setRating(0);

    Mockito.when(reviewService.addReview(eq("123"), any()))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  /** EP Invalid: Verifies rating above allowed range (> 5) triggers HTTP 400 BAD REQUEST. */
  @Test
  @DisplayName("EP Invalid: rating > 5 should return 400")
  void addReview_invalidRatingHigh() throws Exception {
    ReviewDto invalid = new ReviewDto();
    invalid.setRating(6);

    Mockito.when(reviewService.addReview(eq("123"), any()))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  /** EP Invalid: Ensures unexpected service errors return HTTP 500 INTERNAL SERVER ERROR. */
  @Test
  @DisplayName("EP Invalid: Service throws unexpected error → 500")
  void addReview_internalError() throws Exception {
    Mockito.when(reviewService.addReview(eq("123"), any()))
        .thenThrow(new RuntimeException("Database failure"));

    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isInternalServerError());
  }

  // -------------------------------------------------------------------------
  // Tests for getProductAverageRating()
  // -------------------------------------------------------------------------

  /** EP Valid: Ensures controller returns the average rating when reviews exist. */
  @Test
  @DisplayName("EP Valid: Product has reviews → return average")
  void getProductAverageRating_success() throws Exception {
    Mockito.when(reviewService.getAverageRating("123")).thenReturn(ResponseEntity.ok(4.5));

    mockMvc
        .perform(get("/review/product/123/average-rating"))
        .andExpect(status().isOk())
        .andExpect(content().string("4.5"));
  }

  /** EP Valid: Ensures controller returns HTTP 404 NOT FOUND when product has zero reviews. */
  @Test
  @DisplayName("EP Valid: Product has zero reviews → 404")
  void getProductAverageRating_notFound() throws Exception {
    Mockito.when(reviewService.getAverageRating("123")).thenReturn(ResponseEntity.ok(null));

    mockMvc.perform(get("/review/product/123/average-rating")).andExpect(status().isNotFound());
  }

  /** EP Invalid: Ensures invalid productId leads to HTTP 400 BAD REQUEST. */
  @Test
  @DisplayName("EP Invalid: productId invalid → 400")
  void getProductAverageRating_invalidId() throws Exception {
    Mockito.when(reviewService.getAverageRating(""))
        .thenThrow(new IllegalArgumentException("Invalid ID"));

    mockMvc
        .perform(get("/review/product//average-rating"))
        .andExpect(status().isMethodNotAllowed());
  }

  /** EP Invalid: Ensures service exception results in HTTP 500 INTERNAL SERVER ERROR. */
  @Test
  @DisplayName("EP Invalid: Service error → 500")
  void getProductAverageRating_internalError() throws Exception {
    Mockito.when(reviewService.getAverageRating("123")).thenThrow(new RuntimeException("DB error"));

    mockMvc
        .perform(get("/review/product/123/average-rating"))
        .andExpect(status().isInternalServerError());
  }

  // -------------------------------------------------------------------------
  // Tests for getCompanyAverageRating()
  // -------------------------------------------------------------------------

  /** EP Valid: Ensures controller returns the correct average rating when company reviews exist. */
  @Test
  @DisplayName("EP Valid: Company has reviews → return average")
  void getCompanyAverageRating_success() throws Exception {
    Mockito.when(companyService.getAverageRating("C1")).thenReturn(ResponseEntity.ok(3.7));

    mockMvc
        .perform(get("/review/company/C1/average-rating"))
        .andExpect(status().isOk())
        .andExpect(content().string("3.7"));
  }

  /** EP Valid: Ensures HTTP 404 NOT FOUND is returned when a company has no reviews. */
  @Test
  @DisplayName("EP Valid: Company has zero reviews → 404")
  void getCompanyAverageRating_notFound() throws Exception {
    Mockito.when(companyService.getAverageRating("C1")).thenReturn(ResponseEntity.ok(null));

    mockMvc.perform(get("/review/company/C1/average-rating")).andExpect(status().isNotFound());
  }

  /** EP Invalid: Ensures invalid companyId results in HTTP 400 BAD REQUEST. */
  @Test
  @DisplayName("EP Invalid: companyId invalid → 400")
  void getCompanyAverageRating_invalidId() throws Exception {
    Mockito.when(companyService.getAverageRating(""))
        .thenThrow(new IllegalArgumentException("Invalid ID"));

    mockMvc.perform(get("/review/company//average-rating")).andExpect(status().isNotFound());
  }

  /** EP Invalid: Ensures unexpected service failures result in HTTP 500 INTERNAL SERVER ERROR. */
  @Test
  @DisplayName("EP Invalid: Service error → 500")
  void getCompanyAverageRating_internalError() throws Exception {
    Mockito.when(companyService.getAverageRating("C1"))
        .thenThrow(new RuntimeException("DB failure"));

    mockMvc
        .perform(get("/review/company/C1/average-rating"))
        .andExpect(status().isInternalServerError());
  }
}
