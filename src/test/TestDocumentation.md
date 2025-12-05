# Project Test Documentation

This document provides a comprehensive overview of the testing strategy and test suites for the Review Dashboard application, with a focus on the equivalence partitions covered by each test.

## 1. Testing Strategy

Our testing strategy is divided into two main categories:

-   **Unit Tests**: These tests focus on individual components (classes and methods) in isolation. We use **JUnit 5** for the test structure and **Mockito** to mock dependencies, ensuring that we are only testing the logic of the unit under test. Each test is designed to cover a specific equivalence partition (EP) of the possible inputs.
-   **Integration Tests**: Integration test classes : `ReviewDashBoardApplicationTest`,`ReviewClientControllerTest`, and `ReviewClientControllerIntegrationTest`
## 2. Unit Test Suite

Unit tests are located in `src/test/java/com/reviewdashboard/` and are designed to be fast and focused.

### 2.1. Controller Tests (`ReviewClientControllerTest.java`)

-   **Objective**: To verify that the controller correctly handles web requests, delegates calls to its service dependencies, and returns the appropriate HTTP responses based on different input partitions.
-   **Frameworks**: `@WebMvcTest`, Mockito

---

#### **Endpoint: `POST /review/product/{productId}`**

| Test Case                       | Equivalence Partition (EP)                               | Expected Outcome      |
| ------------------------------- | -------------------------------------------------------- | --------------------- |
| `addReview_success`             | **EP: Valid** - A valid review DTO is submitted.         | `201 CREATED`         |
| `addReview_invalidRatingLow`    | **EP: Invalid** - The review's rating is less than 1.    | `400 BAD REQUEST`     |
| `addReview_invalidRatingHigh`   | **EP: Invalid** - The review's rating is greater than 5. | `400 BAD REQUEST`     |
| `addReview_internalError`       | **EP: Invalid** - The service layer throws an exception. | `500 INTERNAL SERVER ERROR` |

---

#### **Endpoint: `GET /review/product/{productId}/average-rating`**

| Test Case                          | Equivalence Partition (EP)                               | Expected Outcome      |
| ---------------------------------- | -------------------------------------------------------- | --------------------- |
| `getProductAverageRating_success`  | **EP: Valid** - The product has existing reviews.        | `200 OK`              |
| `getProductAverageRating_notFound` | **EP: Valid** - The product has no reviews.              | `404 NOT FOUND`       |
| `getProductAverageRating_invalidId`| **EP: Invalid** - The product ID in the URL is malformed.| `405 METHOD NOT ALLOWED`|
| `getProductAverageRating_internalError`| **EP: Invalid** - The service layer throws an exception. | `500 INTERNAL SERVER ERROR` |

---

#### **Endpoint: `GET /review/company/{companyId}/average-rating`**

| Test Case                         | Equivalence Partition (EP)                               | Expected Outcome      |
| --------------------------------- | -------------------------------------------------------- | --------------------- |
| `getCompanyAverageRating_success` | **EP: Valid** - The company has existing reviews.        | `200 OK`              |
| `getCompanyAverageRating_notFound`| **EP: Valid** - The company has no reviews.              | `404 NOT FOUND`       |
| `getCompanyAverageRating_invalidId`| **EP: Invalid** - The company ID in the URL is malformed.| `404 NOT FOUND`       |
| `getCompanyAverageRating_internalError`| **EP: Invalid** - The service layer throws an exception. | `500 INTERNAL SERVER ERROR` |

---

### 2.2. Service Tests

#### `ReviewServiceTest.java`

-   **Objective**: To test the business logic within the `ReviewService` and its interaction with the `ProductClient`.

| Method                  | Test Case                             | Equivalence Partition (EP)                               | Expected Outcome                  |
| ----------------------- | ------------------------------------- | -------------------------------------------------------- | --------------------------------- |
| `addReview`             | `testAddReview_Valid`                 | **EP: Valid** - Valid product ID and review DTO.         | Returns `ReviewDto`.              |
|                         | `testAddReview_InvalidProductId`      | **EP: Invalid** - The product ID is not valid.           | Throws `IllegalArgumentException`.|
|                         | `testAddReview_InvalidReview`         | **EP: Invalid** - The review DTO is not valid.           | Throws `IllegalArgumentException`.|
|                         | `testAddReview_UnexpectedError`       | **EP: Invalid** - The client throws a `RuntimeException`.| Throws `RuntimeException`.        |
| `getAverageRating`      | `testGetAverageRating_ValidWithRatings`| **EP: Valid** - The product has ratings.                 | Returns `ResponseEntity<Double>`. |
|                         | `testGetAverageRating_ValidNoRatings` | **EP: Valid** - The product has no ratings.              | Returns `ResponseEntity` with `null` body. |
|                         | `testGetAverageRating_InvalidProductId`| **EP: Invalid** - The product ID is not valid.           | Throws `IllegalArgumentException`.|
|                         | `testGetAverageRating_UnexpectedError`| **EP: Invalid** - The client throws a `RuntimeException`.| Throws `RuntimeException`.        |

---

#### `CompanyServiceTest.java`

-   **Objective**: To test the business logic within the `CompanyService` and its interaction with the `CompanyClient`.

| Method                  | Test Case                               | Equivalence Partition (EP)                               | Expected Outcome                  |
| ----------------------- | --------------------------------------- | -------------------------------------------------------- | --------------------------------- |
| `getAverageRating`      | `testGetCompanyAverageRating_ValidWithRatings`| **EP: Valid** - The company has ratings.                 | Returns `ResponseEntity<Double>`. |
|                         | `testGetCompanyAverageRating_ValidWithNoRatings`| **EP: Valid** - The company has no ratings.              | Returns `ResponseEntity` with `null` body. |
|                         | `testGetCompanyAverageRating_InvalidCompanyId`| **EP: Invalid** - The company ID is not valid.           | Throws `IllegalArgumentException`.|
|                         | `testGetCompanyAverageRating_UnexpectedError`| **EP: Invalid** - The client throws a `RuntimeException`.| Throws `RuntimeException`.        |

---

### 2.3. Model (DTO) Tests (`ReviewDtoTest.java`, `UserDtoTest.java`)

-   **Objective**: To ensure the integrity of the data transfer objects.
-   **Equivalence Partition**: The tests for each getter/setter pair cover the **EP of all valid inputs** for that field's data type. For example, `testGetAndSetUsername` covers the partition of all valid `String` inputs.

---

## 3. Integration Test Suite

### `ReviewDashBoardApplicationTest.java`

| Test Case       | Equivalence Partition (EP)                               | Expected Outcome                  |
| --------------- | -------------------------------------------------------- | --------------------------------- |
| `contextLoads()`| **EP: Valid Configuration** - All beans and properties are correctly configured. | Application context loads successfully. |

## 4. How to Run Tests

You can run all tests using the following Maven command from the project root:

```bash
mvn clean test
```

## 5. Code Coverage

To generate a code coverage report, run:

```bash
mvn clean test jacoco:report
```

The report will be available at `target/site/jacoco/index.html`.
