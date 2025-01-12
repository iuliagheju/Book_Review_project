//Library.java
/*package org.example;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.List;

public class Library implements Savable, Displayable {
    private ArrayList<Book> books;
    private List<Category> categories;
    private ArrayList<User> users; // Added users list for sorting
    private Double averageRating;

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public void loadBooks(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filePath));
            for (Object obj : jsonArray) {
                JSONObject bookObject = (JSONObject) obj;
                String title = (String) bookObject.get("title");
                String author = (String) bookObject.get("author");
                int review = (Integer) bookObject.get("review");
                int rating = ((Long) bookObject.get("rating")).intValue();

                books.add(new Book(title, author, averageRating) {

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void listBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void sortUsersByReadingListSize() {
        Collections.sort(users);
        for (User user : users) {
            System.out.println(user);
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null; // Return null if the category does not exist
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    public void addBook(Book book) {
        books.add(book);
    }
    public void addCategory(Category category) {
        categories.add(category);

    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public void displayAllCategories(OutputDevice outputDevice) {
        for (Category category : categories) {
            category.displayBooks(outputDevice);
        }
    }

    @Override
    public void save() {
        System.out.println("Saving library data...");
    }

    @Override
    public void display() {

    }
}*/

package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Library implements Savable, Displayable {
    private List<Book> books;
    private List<Category> categories;
    private List<User> users;

    public Library() {
        this.books = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    // Load all books from the database
    public void loadBooksFromDatabase() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT b.book_id, b.title, b.author, b.average_rating, c.name AS category " +
                    "FROM books b LEFT JOIN book_categories bc ON b.book_id = bc.book_id " +
                    "LEFT JOIN categories c ON bc.category_id = c.category_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                double averageRating = rs.getDouble("average_rating");
                String category = rs.getString("category");

                Book book = new Book(title, author, averageRating);
                book.setBookId(bookId);
                book.setCategory(category);
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save a book to the database
    public void saveBookToDatabase(Book book) {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO books (title, author, average_rating) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setDouble(3, book.getAverageRating());

            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                int bookId = keys.getInt(1);
                book.setBookId(bookId);
            }

            if (book.getCategory() != null) {
                addBookCategory(conn, book.getBookId(), book.getCategory());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Associate a book with a category
    public void addBookCategory(Connection conn, int bookId, String categoryName) throws SQLException {
        String categoryQuery = "SELECT category_id FROM categories WHERE name = ?";
        PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
        categoryStmt.setString(1, categoryName);
        ResultSet categoryRs = categoryStmt.executeQuery();

        int categoryId;
        if (categoryRs.next()) {
            categoryId = categoryRs.getInt("category_id");
        } else {
            String insertCategory = "INSERT INTO categories (name) VALUES (?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertCategory, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, categoryName);
            insertStmt.executeUpdate();
            ResultSet keys = insertStmt.getGeneratedKeys();
            keys.next();
            categoryId = keys.getInt(1);
        }

        String bookCategoryQuery = "INSERT INTO book_categories (book_id, category_id) VALUES (?, ?)";
        PreparedStatement bookCategoryStmt = conn.prepareStatement(bookCategoryQuery);
        bookCategoryStmt.setInt(1, bookId);
        bookCategoryStmt.setInt(2, categoryId);
        bookCategoryStmt.executeUpdate();
    }

    // Add a user to the library
    public void addUserToLibrary(User user) {
        if (!users.contains(user)) { // Avoid duplicates
            users.add(user);
        }
    }

    // Add a category to the library
    public void addCategoryToLibrary(Category category) {
        categories.add(category);
    }

    // Add a book to the library
    public void addBookToLibrary(Book book) {
        books.add(book);
    }

    // Get the list of books
    public List<Book> getBooks() {
        return books;
    }

    // Get the list of categories
    public List<Category> getCategories() {
        return categories;
    }

    // Get the list of users
    public List<User> getUsers() {
        return users;
    }

    public User getUserByUsername(String username, String password) {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT user_id, username FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                //String password = rs.getString("password");

                // Create a User object from the database result
                User user = new User(username, password) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }

                    @Override
                    public void save() {
                    }
                };
                user.setUserId(userId);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if no user is found
        return null;
    }


    public List<Category> getCategory() {
        return categories;
    }



    @Override
    public void save() {
        for (Book book : books) {
            saveBookToDatabase(book);
        }
    }

    @Override
    public void display() {
        for (Book book : books) {
            System.out.println(book.getTitle() + " by " + book.getAuthor() + " - Rating: " + book.getAverageRating());
        }
    }
}
