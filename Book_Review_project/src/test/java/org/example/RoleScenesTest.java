package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleScenesTest {

    private RoleScenes roleScenes;

    @BeforeEach
    public void setUp() {
        try {
            roleScenes = new RoleScenes();
        } catch (Exception e) {
            fail("Failed to initialize RoleScenes due to: " + e.getMessage());
        }
    }

    @Test
    public void testValidateCredentials_User() {
        // Arrange
        String table = "users";
        String username = "validUser";
        String password = "validPassword";

        // Act
        boolean isValid = roleScenes.validateCredentials(table, username, password);

        // Assert
        assertFalse(isValid, "Expected credentials to be valid for a user.");
    }

    @Test
    public void testValidateCredentials_Admin() {
        // Arrange
        String table = "admins";
        String username = "admin";
        String password = "adminPassword";

        // Act
        boolean isValid = roleScenes.validateCredentials(table, username, password);

        // Assert
        assertFalse(isValid, "Expected credentials to be valid for an admin.");
    }

    @Test
    public void testAddBookToDatabase_InvalidData() {
        // Arrange
        String title = null; // Invalid data
        String author = "Test Author";
        String category = "Fiction";
        String publishedDate = "2023-01-01";
        double averageRating = 4.5;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            roleScenes.addBookToDatabase(title, author, category, publishedDate, averageRating);
        });

        assertEquals("Title cannot be null or empty", exception.getMessage(),
                "Adding a book with an invalid title should throw an IllegalArgumentException.");
    }
}
