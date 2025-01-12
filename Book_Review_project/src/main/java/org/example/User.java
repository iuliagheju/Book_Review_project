//User.java
/*package org.example;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class User implements Savable, Comparable<User> {
    private String username;

    private ArrayList<Book> booksRead; // List of books the user has read
    private ArrayList<ArrayList<Review>> reviews; // List of reviews for each book
    private List<Book> readingList = new ArrayList<>();

    public User(String username, String password) {
        this.username = username.trim();

        this.booksRead = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }


    public ArrayList<Book> getBooksRead() {
        return booksRead;
    }

    public List<Book> getReadingList() {
               return readingList;

    }

    public Collection<Review> getReviews() {
        ArrayList<Review> allReviews = new ArrayList<>();
        for (ArrayList<Review> bookReviews : reviews) {
            allReviews.addAll(bookReviews);
        }
        return allReviews;
    }

    public String getUsername() {
        return username;

    }
}*/
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Savable, Comparable<User> {
    private int userId;
    private String username;
    private String password;
    private List<Book> readingList;

    public User(String username, String password) {
        this.username = username.trim();
        this.password = password;
        this.readingList = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public List<Book> getReadingList() {
        return readingList;
    }

    public void loadReadingListFromDatabase() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT b.book_id, b.title, b.author, b.average_rating " +
                    "FROM readinglists rl " +
                    "JOIN readinglist_books rlb ON rl.list_id = rlb.list_id " +
                    "JOIN books b ON rlb.book_id = b.book_id " +
                    "WHERE rl.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double averageRating = rs.getDouble("average_rating");

                Book book = new Book(title, author, averageRating);
                book.setBookId(bookId);
                readingList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveReadingListToDatabase() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String listQuery = "INSERT INTO readinglists (user_id, list_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE list_id = LAST_INSERT_ID(list_id)";
            PreparedStatement listStmt = conn.prepareStatement(listQuery, Statement.RETURN_GENERATED_KEYS);
            listStmt.setInt(1, userId);
            listStmt.setString(2, username + "'s Reading List");
            listStmt.executeUpdate();

            ResultSet keys = listStmt.getGeneratedKeys();
            int listId = keys.next() ? keys.getInt(1) : -1;

            if (listId != -1) {
                String deleteBooksQuery = "DELETE FROM readinglist_books WHERE list_id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteBooksQuery);
                deleteStmt.setInt(1, listId);
                deleteStmt.executeUpdate();

                String insertBookQuery = "INSERT INTO readinglist_books (list_id, book_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertBookQuery);

                for (Book book : readingList) {
                    insertStmt.setInt(1, listId);
                    insertStmt.setInt(2, book.getBookId());
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(User other) {
        return this.readingList.size() - other.readingList.size();
    }

    @Override
    public void save() {
        saveReadingListToDatabase();
    }
}
