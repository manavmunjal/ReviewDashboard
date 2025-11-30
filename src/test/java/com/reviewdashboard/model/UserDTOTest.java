package com.reviewdashboard.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link UserDTO} class.
 * <p>
 * This test class verifies that the getters and setters of the UserDTO
 * function correctly.
 * </p>
 */
public class UserDTOTest {

    /**
     * Tests the getter and setter for the 'id' field.
     */
    @Test
    public void testGetAndSetId() {
        // Given
        UserDTO user = new UserDTO();
        String expectedId = "user123";

        // When
        user.setId(expectedId);
        String actualId = user.getId();

        // Then
        assertEquals(expectedId, actualId, "The ID should be correctly set and retrieved.");
    }

    /**
     * Tests the getter and setter for the 'username' field.
     */
    @Test
    public void testGetAndSetUsername() {
        // Given
        UserDTO user = new UserDTO();
        String expectedUsername = "testUser";

        // When
        user.setUsername(expectedUsername);
        String actualUsername = user.getUsername();

        // Then
        assertEquals(expectedUsername, actualUsername, "The username should be correctly set and retrieved.");
    }
}
