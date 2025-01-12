//Category.java
/*package org.example;
import java.util.ArrayList;
import java.util.List;



public abstract class Category implements Displayable {
    private String name;
    private List<Book> books;
    private OutputDevice outputDevice;

    public Category(String name, OutputDevice outputDevice) {
        this.name = name;
        this.outputDevice = outputDevice;
        this.books = new ArrayList<>();
    }

    public Category(String categoryName) {
        this.name = categoryName;
        this.books = new ArrayList<>();

    }

    public void addBook(Book book) {
        books.add(book);
    }
    public String getName() {
        return name;
    }

    // Getter for books in this category
    public List<Book> getBooks() {
        return books;
    }

    // Method to display all books in this category
    public void displayBooks(OutputDevice outputDevice) {
        outputDevice.writeMessage("\n--- " + name + " Category ---");

        if (books.isEmpty()) {
            outputDevice.writeMessage("No books available in this category.");
        } else {
            for (Book book : books) {
                outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor() +
                        " - Average Rating: " + book.getAverageRating());
            }
        }
    }
}
*/
package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Category implements Displayable {
    private int categoryId;
    private String name;
    private List<Book> books;

    public Category(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void loadBooksForCategory() {
        books.clear();
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT b.book_id, b.title, b.author, b.average_rating " +
                    "FROM books b " +
                    "JOIN book_categories bc ON b.book_id = bc.book_id " +
                    "WHERE bc.category_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double averageRating = rs.getDouble("average_rating");

                Book book = new Book(title, author, averageRating);
                book.setBookId(bookId);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCategory() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO categories (name) VALUES (?) ON DUPLICATE KEY UPDATE category_id = LAST_INSERT_ID(category_id)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                this.categoryId = keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBookToCategory(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO book_categories (book_id, category_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBookFromCategory(Book book) {
        books.remove(book);

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "DELETE FROM book_categories WHERE book_id = ? AND category_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        System.out.println("\n--- " + name + " Category ---");

        if (books.isEmpty()) {
            System.out.println("No books available in this category.");
        } else {
            for (Book book : books) {
                System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor() +
                        " - Average Rating: " + book.getAverageRating());
            }
        }
    }
}

