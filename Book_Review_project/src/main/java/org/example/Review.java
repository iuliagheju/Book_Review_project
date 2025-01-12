//Review.java
/*package org.example;

public class Review implements Savable {
    private String reviewText;
    private int rating;

    public Review(String reviewText, int rating) {
        this.reviewText = reviewText;
        this.rating = rating;
    }

    @Override
    public void save() {
        System.out.println("Saving review: " + reviewText + " with rating " + rating);
        // Code to save review data
    }

    public String getReviewText() {
        return reviewText;
    }
    public int getRating() {
        return rating;
    }
}*/
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Review implements Savable {
    private int reviewId;
    private String reviewText;
    private int rating;
    private int userId;
    private int bookId;

    public Review(String reviewText, int rating, int userId, int bookId) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.userId = userId;
        this.bookId = bookId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getRating() {
        return rating;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    @Override
    public void save() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO reviews (book_id, user_id, review_text, rating) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE review_text = ?, rating = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            stmt.setString(3, reviewText);
            stmt.setInt(4, rating);
            stmt.setString(5, reviewText);
            stmt.setInt(6, rating);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

