package com.reviewdashboard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewdashboard.model.ReviewDto;
import com.reviewdashboard.service.CompanyService;
import com.reviewdashboard.service.ReviewService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link ReviewClientController} focusing on logger branch coverage.
 *
 * <p>This test class uses Logback's ListAppender to capture log events and tests logging behavior
 * with different log levels to cover the logger.isXxxEnabled() branches.
 */
@WebMvcTest(ReviewClientController.class)
class ReviewClientControllerLoggerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ReviewService reviewService;
  @MockBean private CompanyService companyService;

  private Logger controllerLogger;
  private ListAppender<ILoggingEvent> listAppender;
  private Level originalLevel;

  @BeforeEach
  void setUp() {
    controllerLogger = (Logger) LoggerFactory.getLogger(ReviewClientController.class);
    originalLevel = controllerLogger.getLevel();

    listAppender = new ListAppender<>();
    listAppender.start();
    controllerLogger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    controllerLogger.detachAppender(listAppender);
    controllerLogger.setLevel(originalLevel);
  }

  // =======================================================================
  // addReview - Logger level OFF (skips all logging)
  // =======================================================================

  @Test
  void addReview_success_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    ReviewDto review = createValidReview();
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated());

    assertNoLogsAtLevel(Level.INFO);
  }

  @Test
  void addReview_invalidRating_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    ReviewDto review = createInvalidRatingReview();
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1"))).thenReturn(review);

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(review)))
        .andExpect(status().isCreated());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void addReview_illegalArgument_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new IllegalArgumentException("Invalid input"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createValidReview())))
        .andExpect(status().isBadRequest());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void addReview_unauthorized_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(buildFeignException(401, "Unauthorized"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createValidReview())))
        .andExpect(status().isUnauthorized());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void addReview_feignException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(buildFeignException(500, "Server error"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createValidReview())))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  @Test
  void addReview_genericException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.addReview(eq("123"), any(), eq("U1")))
        .thenThrow(new RuntimeException("Unexpected"));

    mockMvc
        .perform(
            post("/review/product/123")
                .header("X-User-Id", "U1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createValidReview())))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  // =======================================================================
  // getProductAverageRating - Logger level OFF
  // =======================================================================

  @Test
  void getProductAverageRating_success_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(4.5));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk());

    assertNoLogsAtLevel(Level.INFO);
  }

  @Test
  void getProductAverageRating_notFound_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1")).thenReturn(ResponseEntity.ok(null));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isNotFound());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getProductAverageRating_illegalArgument_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1"))
        .thenThrow(new IllegalArgumentException("Invalid productId"));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isBadRequest());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getProductAverageRating_unauthorized_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1"))
        .thenThrow(buildFeignException(401, "Unauthorized"));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isUnauthorized());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getProductAverageRating_feignException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1"))
        .thenThrow(buildFeignException(500, "Server error"));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  @Test
  void getProductAverageRating_genericException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(reviewService.getAverageRating("123", "U1"))
        .thenThrow(new RuntimeException("Unexpected"));

    mockMvc
        .perform(get("/review/product/123/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  // =======================================================================
  // getCompanyAverageRating - Logger level OFF
  // =======================================================================

  @Test
  void getCompanyAverageRating_success_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1")).thenReturn(ResponseEntity.ok(3.8));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isOk());

    assertNoLogsAtLevel(Level.INFO);
  }

  @Test
  void getCompanyAverageRating_notFound_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1")).thenReturn(ResponseEntity.ok(null));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isNotFound());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getCompanyAverageRating_illegalArgument_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1"))
        .thenThrow(new IllegalArgumentException("Invalid companyId"));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isBadRequest());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getCompanyAverageRating_unauthorized_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1"))
        .thenThrow(buildFeignException(401, "Unauthorized"));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isUnauthorized());

    assertNoLogsAtLevel(Level.WARN);
  }

  @Test
  void getCompanyAverageRating_feignException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1"))
        .thenThrow(buildFeignException(500, "Server error"));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  @Test
  void getCompanyAverageRating_genericException_withLoggingOff() throws Exception {
    controllerLogger.setLevel(Level.OFF);
    Mockito.when(companyService.getAverageRating("C1", "U1"))
        .thenThrow(new RuntimeException("Unexpected"));

    mockMvc
        .perform(get("/review/company/C1/average-rating").header("X-User-Id", "U1"))
        .andExpect(status().isInternalServerError());

    assertNoLogsAtLevel(Level.ERROR);
  }

  // =======================================================================
  // Helper methods
  // =======================================================================

  private ReviewDto createValidReview() {
    ReviewDto review = new ReviewDto();
    review.setRating(4);
    review.setComment("Good product");
    return review;
  }

  private ReviewDto createInvalidRatingReview() {
    ReviewDto review = new ReviewDto();
    review.setRating(6);
    review.setComment("Invalid rating");
    return review;
  }

  private FeignException buildFeignException(int status, String message) {
    Request request =
        Request.create(
            Request.HttpMethod.GET,
            "/test",
            Collections.emptyMap(),
            message != null ? message.getBytes(StandardCharsets.UTF_8) : null,
            StandardCharsets.UTF_8,
            new RequestTemplate());
    return new FeignException.FeignClientException(status, message, request, null, null);
  }

  private void assertNoLogsAtLevel(Level level) {
    List<ILoggingEvent> logs = listAppender.list;
    boolean hasLogsAtLevel = logs.stream().anyMatch(event -> event.getLevel().equals(level));
    if (hasLogsAtLevel) {
      throw new AssertionError("Expected no logs at level " + level + " but found some");
    }
  }
}
