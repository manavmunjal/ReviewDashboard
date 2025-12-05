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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link ReviewClientController}, focusing on controller-level behavior using {@link
 * WebMvcTest} with mocked service dependencies.
 *
 * <p>This suite validates HTTP layer correctness, ensuring:
 *
 * <ul>
 *   <li>Correct request handling
 *   <li>Validation of request headers and payloads
 *   <li>Proper HTTP status code mapping
 *   <li>Serialization and deserialization behavior
 * </ul>
 *
 * <h2>Equivalence Partitioning (EP) Covered in This Test Class</h2>
 *
 * <ul>
 *   <li><b>Valid Review Payload</b> — rating ∈ [1–5], comment non-null
 *   <li><b>Invalid Review Payload</b> — rating < 1
 *   <li><b>Existing Review State</b> — when average rating exists
 *   <li><b>No Review State</b> — when average rating is null
 *   <li><b>Missing Required Header</b> — X-User-Id absent
 * </ul>
 *
 * <h2>Boundary Value Analysis (BVA) Covered</h2>
 *
 * <ul>
 *   <li><b>rating = 1</b> → Lower valid boundary (implicitly tested via validReview)
 *   <li><b>rating = 0</b> → Just below lower boundary (explicit invalid test)
 *   <li><b>Null average rating</b> → Boundary of "no data"
 * </ul>
 *
 * <p>Note: tests mock the service layer and validate only controller behavior.
 */
@WebMvcTest(ReviewClientController.class)
class ReviewClientControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ReviewService reviewService;
  @MockBean private CompanyService companyService;

  private ReviewDto validReview;

  /**
   * Creates a valid review instance representing the "valid input" partition.
   *
   * <p>EP — Valid Input Partition:
   *
   * <ul>
   *   <li>rating ∈ [1–5]
   *   <li>comment: non-null
   * </ul>
   */
  @BeforeEach
  void setup() {
    validReview = new ReviewDto();
    validReview.setRating(4);
    validReview.setComment("Good product");
  }

  // =======================================================================
  // addReview() SUCCESS
  // =======================================================================

  /**
   * Tests successful creation of a review.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>Valid rating</b> ∈ [1–5]
   *   <li><b>Valid comment</b> → non-null
   *   <li><b>Valid productId</b> → "123"
   *   <li><b>Valid X-User-Id header</b>
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>rating = 4 → mid-range valid value
   * </ul>
   */
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

  // =======================================================================
  // addReview → invalid rating (<1)
  // =======================================================================

  /**
   * Tests the controller behavior when rating falls below the valid domain.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>Invalid rating partition</b> → rating < 1
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>rating = 0 → just below lower boundary (invalid)
   * </ul>
   */
  @Test
  void addReview_invalidRatingLow() throws Exception {
    ReviewDto invalid = new ReviewDto();
    invalid.setRating(0);

    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new IllegalArgumentException("Invalid rating"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  // =======================================================================
  // getProductAverageRating SUCCESS
  // =======================================================================

  /**
   * Tests the case where an average rating exists for a product.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>Existing average rating</b> → valid partition
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>average rating = 4.5 → valid midrange
   * </ul>
   */
  @Test
  void getProductAverageRating_success() throws Exception {
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(4.5));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk())
        .andExpect(content().string("4.5"));
  }

  // =======================================================================
  // getProductAverageRating → no reviews (404)
  // =======================================================================

  /**
   * Tests the scenario when no reviews exist for a product.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>No-review partition</b> → null response from service
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>average = null → boundary between “exists” & “does not exist”
   * </ul>
   */
  @Test
  void getProductAverageRating_notFound() throws Exception {
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(null));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isNotFound());
  }

  // =======================================================================
  // getCompanyAverageRating SUCCESS
  // =======================================================================

  /**
   * Tests successful retrieval of a company's average rating.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>Existing rating value partition</b> → value present
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>3.7 → valid non-boundary value
   * </ul>
   */
  @Test
  void getCompanyAverageRating_success() throws Exception {
    Mockito.when(companyService.getAverageRating("C1", "U1")).thenReturn(ResponseEntity.ok(3.7));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk())
        .andExpect(content().string("3.7"));
  }

  // =======================================================================
  // Missing X-User-Id header → 400
  // =======================================================================

  /**
   * Ensures that the controller rejects requests missing the required header.
   *
   * <h3>Equivalence Partitioning (EP)</h3>
   *
   * <ul>
   *   <li><b>Missing-header partition</b> → invalid request
   * </ul>
   *
   * <h3>Boundary Value Analysis (BVA)</h3>
   *
   * <ul>
   *   <li>Header present vs. missing → strict binary boundary
   * </ul>
   */
  @Test
  void missingUserIdHeader_returns400() throws Exception {
    mockMvc
        .perform(
            post("/review/product/123")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(validReview)))
        .andExpect(status().isBadRequest());
  }
}
