# Review Dashboard - Test Documentation

This document provides a detailed overview of the tests for the `review-dashboard` project. It outlines the purpose of each test class and describes the specific equivalence partitions covered by each test method.

---

## 1. Application Context Test

### `ReviewDashBoardApplicationTest.java`

This is a foundational test to ensure the application's integrity.

- **`contextLoads()`**
    - **Equivalence Class**: Successful application startup.
    - **Verification**: Ensures the Spring Boot application context loads without any configuration errors or missing bean dependencies.

---

## 2. Model (DTO) Tests

These are basic unit tests verifying the data integrity of the Data Transfer Objects (DTOs) by testing their getters and setters.

### `UserDtoTest.java`

- **`testGetAndSetId()`**
    - **Equivalence Class**: Valid `String` input for the user ID.
    - **Verification**: Ensures the `id` field is set and retrieved correctly.
- **`testGetAndSetUsername()`**
    - **Equivalence Class**: Valid `String` input for the username.
    - **Verification**: Ensures the `username` field is set and retrieved correctly.

### `ReviewDtoTest.java`

- **`testGetAndSetComment()`**
    - **Equivalence Class**: Valid `String` input for the comment.
    - **Verification**: Ensures the `comment` field is set and retrieved correctly.
- **`testGetAndSetRating()`**
    - **Equivalence Class**: Valid `double` input for the rating.
    - **Verification**: Ensures the `rating` field is set and retrieved correctly.
- **`testGetAndSetUser()`**
    - **Equivalence Class**: Valid `UserDto` object.
    - **Verification**: Ensures the `user` object is set and retrieved correctly.

---

## 3. Service Layer Tests

These unit tests focus on the business logic within the service layer. They use Mockito to mock the Feign clients, ensuring that the service logic is tested in isolation from external network dependencies.

### `CompanyServiceTest.java`

#### `getAverageRating()` Method

- **`testGetCompanyAverageRating_ValidWithRatings()`**
    - **Equivalence Class**: Successful client response with a rating.
    - **Verification**: Ensures the service returns a `ResponseEntity` with the correct rating when the client provides one.
- **`testGetCompanyAverageRating_ValidWithNoRatings()`**
    - **Equivalence Class**: Successful client response with no rating data.
    - **Verification**: Ensures the service returns a `ResponseEntity` with a `null` body when the client indicates no ratings are available.
- **`testGetCompanyAverageRating_InvalidCompanyId()`**
    - **Equivalence Class**: Invalid input detected by the client.
    - **Verification**: Ensures the service propagates the `IllegalArgumentException` thrown by the client.
- **`testGetCompanyAverageRating_UnexpectedError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the service propagates the `RuntimeException` when the client fails unexpectedly.

### `ReviewServiceTest.java`

#### `addReview()` Method

- **`testAddReview_Valid()`**
    - **Equivalence Class**: Successful client interaction.
    - **Verification**: Ensures the service correctly delegates to the client and returns the created `ReviewDto`.
- **`testAddReview_InvalidProductId()`**
    - **Equivalence Class**: Invalid product ID detected by the client.
    - **Verification**: Ensures the service propagates the `IllegalArgumentException` from the client.
- **`testAddReview_InvalidReview()`**
    - **Equivalence Class**: Invalid review data detected by the client.
    - **Verification**: Ensures the service propagates the `IllegalArgumentException` from the client.
- **`testAddReview_UnexpectedError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the service propagates the `RuntimeException` from the client.

#### `getAverageRating()` Method

- **`testGetAverageRating_ValidWithRatings()`**
    - **Equivalence Class**: Successful client response with a rating.
    - **Verification**: Ensures the service returns a `ResponseEntity` with the correct rating.
- **`testGetAverageRating_ValidNoRatings()`**
    - **Equivalence Class**: Successful client response with no rating data.
    - **Verification**: Ensures the service returns a `ResponseEntity` with a `null` body.
- **`testGetAverageRating_InvalidProductId()`**
    - **Equivalence Class**: Invalid product ID detected by the client.
    - **Verification**: Ensures the service propagates the `IllegalArgumentException` from the client.
- **`testGetAverageRating_UnexpectedError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the service propagates the `RuntimeException` from the client.

---

## 4. Controller Layer Tests

### `ReviewClientControllerTest.java`

These unit tests verify the behavior of the `ReviewClientController` in isolation. The service layer is mocked to simulate various scenarios, allowing for focused testing of the controller's request handling, response mapping, and error management logic.

#### `addReview()` Endpoint

- **`testAddReview_Success()`**
    - **Equivalence Class**: Successful review creation.
    - **Verification**: Ensures the endpoint returns `201 CREATED` with the review DTO in the body.
- **`testAddReview_BadRequest()`**
    - **Equivalence Class**: Invalid input from the client (e.g., bad product ID).
    - **Verification**: Ensures the endpoint returns `400 BAD REQUEST` with an error message when the service throws an `IllegalArgumentException`.
- **`testAddReview_InternalError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the endpoint returns `500 INTERNAL SERVER ERROR` with an error message when the service throws a `RuntimeException`.

#### `getProductAverageRating()` Endpoint

- **`testGetProductAverageRating_Success()`**
    - **Equivalence Class**: Product has an average rating.
    - **Verification**: Ensures the endpoint returns `200 OK` with the rating value in the body.
- **`testGetProductAverageRating_NotFound()`**
    - **Equivalence Class**: Product exists but has no rating data.
    - **Verification**: Ensures the endpoint returns `404 NOT FOUND` when the service returns a null body.
- **`testGetProductAverageRating_BadRequest()`**
    - **Equivalence Class**: Invalid product identifier.
    - **Verification**: Ensures the endpoint returns `400 BAD REQUEST` when the service throws an `IllegalArgumentException`.
- **`testGetProductAverageRating_InternalError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the endpoint returns `500 INTERNAL SERVER ERROR` when the service throws a `RuntimeException`.

#### `getCompanyAverageRating()` Endpoint

- **`testGetCompanyAverageRating_Success()`**
    - **Equivalence Class**: Company has an average rating.
    - **Verification**: Ensures the endpoint returns `200 OK` with the rating value in the body.
- **`testGetCompanyAverageRating_NotFound()`**
    - **Equivalence Class**: Company exists but has no rating data.
    - **Verification**: Ensures the endpoint returns `404 NOT FOUND` when the service returns a null body.
- **`testGetCompanyAverageRating_BadRequest()`**
    - **Equivalence Class**: Invalid company identifier.
    - **Verification**: Ensures the endpoint returns `400 BAD REQUEST` when the service throws an `IllegalArgumentException`.
- **`testGetCompanyAverageRating_InternalError()`**
    - **Equivalence Class**: Unexpected downstream failure.
    - **Verification**: Ensures the endpoint returns `500 INTERNAL SERVER ERROR` when the service throws a `RuntimeException`.

---

## 5. Web Layer Integration Tests

### `ReviewClientControllerIntegrationTest.java`

These tests use Spring's `MockMvc` to perform integration testing of the web layer. They verify that the `ReviewClientController` correctly handles HTTP requests and responses, including JSON serialization and deserialization, by mocking the service layer dependencies.

- **`testAddReview_Success()`**
    - **Equivalence Class**: Successful end-to-end creation at the web layer.
    - **Verification**: Asserts that a `POST` request to `/review/product/{productId}` with a valid JSON body returns `201 CREATED` and the correct JSON response.

- **`testGetProductAverageRating_Success()`**
    - **Equivalence Class**: Successful end-to-end retrieval of a product rating.
    - **Verification**: Asserts that a `GET` request to `/review/product/{productId}/average-rating` returns `200 OK` and the correct rating value in the response body.

- **`testGetProductAverageRating_NotFound()`**
    - **Equivalence Class**: End-to-end retrieval for a product with no rating.
    - **Verification**: Asserts that a `GET` request for a product with no rating data returns `404 NOT FOUND` and the expected error message.
