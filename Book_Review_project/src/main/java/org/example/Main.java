//Main.java
/*package org.example;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static Application app;
    private static Library library;  // New instance of Library to manage categories and books
    private static User currentUser;
    private static InputDevice inputDevice;
    private static OutputDevice outputDevice;

    public static void main(String[] args) {
        // Initialize InputDevice and OutputDevice for handling input/output
        InputDevice inputDevice = new InputDevice();
        outputDevice = new OutputDevice();
        app = new Application(inputDevice, outputDevice);
        library = new Library();  // Initialize the Library instance

        Properties config = loadConfigurations("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\java\\org\\example\\config.properties");
        app.loadBooksFromJson(config.getProperty("books.filepath"));
        app.loadUsersFromDatabase();
        loadBooksIntoLibrary(); // Load books into Library

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            // Display main menu
            outputDevice.writeMessage("\n--- Main Menu ---");
            outputDevice.writeMessage("1. Log in");
            outputDevice.writeMessage("2. Search for a book");
            outputDevice.writeMessage("3. View all books by category");
            outputDevice.writeMessage("4. Display users sorted by reading list size");
            outputDevice.writeMessage("5. Exit");
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice. Please enter a valid number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    login(scanner, outputDevice);
                    break;
                case 2:
                    searchBook(scanner, outputDevice, false); // Non-logged-in user
                    break;
                case 3:
                    viewAllBooksByCategory(outputDevice); // New method to view books by category
                    break;
                case 4:
                    displayUsersSortedByReadingListSize(outputDevice); // New method to display users sorted by reading list size
                    break;
                case 5:
                    outputDevice.writeMessage("Exiting...");
                    app.saveUsersToJson(config.getProperty("users.filepath"));
                    exit = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please choose a valid option.");
            }
        }

        scanner.close(); // Close scanner after handling the choice
    }

    private static void login(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("Enter username: ");
        String username = scanner.nextLine();

        try {
            // Validate username
            if (username.trim().isEmpty()) {
                throw new InvalidUserException("Username cannot be empty.");
            }

            // Check if user already exists
            User user = library.getUserByUsername(username);
            if (user == null) {
                // Create a new user if it doesn't exist
                String password = "";
                user = new User(username, password) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }

                    @Override
                    public void save() {}
                };
                library.addUser(user); // Add new user to library
            }

            currentUser = user; // Set the logged-in user
            outputDevice.writeMessage("Welcome, " + username + "!");
            userMenu(scanner, outputDevice); // Navigate to user-specific menu
        } catch (InvalidUserException e) {
            outputDevice.writeMessage("Error: " + e.getMessage());
        }
    }

    public static Properties loadConfigurations(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error loading configurations: " + e.getMessage());
        }
        return properties;
    }

    private static void loadBooksIntoLibrary() {
        for (Book book : app.getBooks()) {
            String categoryName;

            // Categorize books based on their titles or authors
            if (book.getAuthor().contains("J.K. Rowling") || book.getTitle().contains("Harry Potter")) {
                categoryName = "Fantasy";
            } else if (book.getAuthor().contains("Agatha Christie")) {
                categoryName = "Mystery";
            } else if (book.getAuthor().contains("George Orwell") || book.getAuthor().contains("F. Scott Fitzgerald") ||
                    book.getAuthor().contains("Oscar Wilde") || book.getAuthor().contains("Emily Bronte") ||
                    book.getAuthor().contains("Charlotte Bronte") || book.getAuthor().contains("Bram Stoker")) {
                categoryName = "Classic Literature";
            } else if (book.getAuthor().contains("Frank Herbert") || book.getAuthor().contains("Mary Shelley")) {
                categoryName = "Science Fiction";
            } else if (book.getAuthor().contains("Jane Austen") || book.getAuthor().contains("Margaret Atwood")) {
                categoryName = "Romance";
            } else {
                categoryName = "General"; // Default category
            }

            // Get or create the category
            Category category = library.getCategoryByName(categoryName);
            if (category == null) {
                category = new Category(categoryName) {
                    @Override
                    public void display() {
                        outputDevice.writeMessage("--- " + categoryName + " ---");
                        for (Book categoryBook : this.getBooks()) {
                            outputDevice.writeMessage("Book: " + categoryBook.getTitle() + " by " + categoryBook.getAuthor() +
                                    " - Average Rating: " + categoryBook.getAverageRating());
                        }
                    }
                };
                library.addCategory(category);
            }

            // Add the book to the category
            category.addBook(book);
        }

        outputDevice.writeMessage("Books loaded into categories successfully.");
    }


    private static void viewAllBooksByCategory(OutputDevice outputDevice) {
        outputDevice.writeMessage("\n--- Books by Category ---");

        for (Category category : library.getCategories()) {
            category.displayBooks(outputDevice);
        }
    }

    private static void displayUsersSortedByReadingListSize(OutputDevice outputDevice) {
        // Retrieve and sort users by reading list size
        List<User> users = library.getUsers();
        users.sort((u1, u2) -> Integer.compare(u2.getReadingList().size(), u1.getReadingList().size()));

        outputDevice.writeMessage("\n--- Users Sorted by Reading List Size ---");

        if (users.isEmpty()) {
            outputDevice.writeMessage("No users available.");
        } else {
            for (User user : users) {
                outputDevice.writeMessage(user.getUsername() + " - Reading List Size: " + user.getReadingList().size());
            }
        }
    }

    private static void addBookToReadingList(Scanner scanner, OutputDevice outputDevice) {
        outputDevice.writeMessage("\nEnter the title of the book to add to your reading list: ");
        String bookTitle = scanner.nextLine();

        Book book = app.searchBookByTitle(bookTitle); // Reuse the search functionality
        if (book != null) {
            currentUser.getReadingList().add(book); // Add the book to the user's reading list
            outputDevice.writeMessage("Book added to your reading list.");
        } else {
            outputDevice.writeMessage("Book not found.");
        }
    }

    private static void userMenu(Scanner scanner, OutputDevice outputDevice) {
        boolean logout = false;
        while (!logout) {
            outputDevice.writeMessage("\n--- User Menu ---");
            outputDevice.writeMessage("1. Search for a book");
            outputDevice.writeMessage("2. Add book to reading list");
            outputDevice.writeMessage("3. Log out");
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice. Please enter a valid number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    searchBook(scanner, outputDevice, true); // Logged-in user
                    break;
                case 2:
                    addBookToReadingList(scanner, outputDevice); // New method to add a book to the reading list
                    break;
                case 3:
                    outputDevice.writeMessage("Logging out...");
                    currentUser = null; // Clear the current user
                    logout = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please choose a valid option.");
            }
        }
    }

    private static void searchBook(Scanner scanner, OutputDevice outputDevice, boolean isLoggedIn) {
        outputDevice.writeMessage("\n--- Search Book ---");
        outputDevice.writeMessage("1. Search by Title");
        outputDevice.writeMessage("2. Search by Author");
        outputDevice.writeMessage("3. Search by Title and Author");
        outputDevice.writeMessage("4. Search by Category");
        outputDevice.writeMessage("Choose an option: ");

        int choice = -1;

        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline left after nextInt
        } else {
            outputDevice.writeMessage("Invalid choice.");
            scanner.nextLine(); // clear invalid input
            return;
        }

        switch (choice) {
            case 1: // Search by Title
                outputDevice.writeMessage("Enter book title: ");
                String titleQuery = scanner.nextLine();
                Book bookByTitle = app.searchBookByTitle(titleQuery);
                displaySearchResult(outputDevice, bookByTitle, isLoggedIn, scanner);
                break;

            case 2: // Search by Author
                outputDevice.writeMessage("Enter book author: ");
                String authorQuery = scanner.nextLine();
                Book bookByAuthor = app.searchBookByAuthor(authorQuery);
                displaySearchResult(outputDevice, bookByAuthor, isLoggedIn, scanner);
                break;

            case 3: // Search by Title and Author
                outputDevice.writeMessage("Enter book title: ");
                String titleAndAuthorQuery = scanner.nextLine();
                outputDevice.writeMessage("Enter book author: ");
                String authorForTitleQuery = scanner.nextLine();
                Book bookByTitleAndAuthor = app.searchBookByTitleAndAuthor(titleAndAuthorQuery, authorForTitleQuery);
                displaySearchResult(outputDevice, bookByTitleAndAuthor, isLoggedIn, scanner);
                break;

            case 4: // Search by Category
                outputDevice.writeMessage("Available Categories: ");
                for (Category category : library.getCategories()) {
                    outputDevice.writeMessage("- " + category.getName());
                }

                outputDevice.writeMessage("Enter category name: ");
                String categoryQuery = scanner.nextLine();

                Category category = library.getCategoryByName(categoryQuery);
                if (category != null) {
                    outputDevice.writeMessage("\n--- Books in Category: " + category.getName() + " ---");
                    for (Book categoryBook : category.getBooks()) {
                        outputDevice.writeMessage("Book: " + categoryBook.getTitle() + " by " + categoryBook.getAuthor() +
                                " - Average Rating: " + categoryBook.getAverageRating());
                    }
                } else {
                    outputDevice.writeMessage("No such category found.");
                }
                break;

            default:
                outputDevice.writeMessage("Invalid choice.");
        }
    }

    private static void displaySearchResult(OutputDevice outputDevice, Book book, boolean isLoggedIn, Scanner scanner) {
        if (book != null) {
            outputDevice.writeMessage("Book found: " + book.getTitle() + " by " + book.getAuthor());
            displayBookOptions(scanner, outputDevice, book, isLoggedIn);
        } else {
            outputDevice.writeMessage("Book not found.");
        }
    }

    private static void displayBookOptions(Scanner scanner, OutputDevice outputDevice, Book book, boolean isLoggedIn) {
        boolean goBack = false;
        while (!goBack) {
            outputDevice.writeMessage("\n--- Book Options ---");
            outputDevice.writeMessage("1. View all reviews and average rating");
            if (isLoggedIn) {
                outputDevice.writeMessage("2. Add a review and a rating");
                outputDevice.writeMessage("3. Go back to previous menu");
            } else {
                outputDevice.writeMessage("2. Go back to previous menu");
            }
            outputDevice.writeMessage("Choose an option: ");

            int choice = -1;

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline left after nextInt
            } else {
                outputDevice.writeMessage("Invalid choice.");
                scanner.nextLine(); // clear invalid input
                continue;
            }

            if (choice == 1) {
                viewBookReviews(outputDevice, book);
            } else if (isLoggedIn) {
                switch (choice) {
                    case 2:
                        addReview(scanner, outputDevice, book);
                        break;
                    case 3:
                        goBack = true; // Set goBack to true to exit the loop
                        break;
                    default:
                        outputDevice.writeMessage("Invalid choice. Please try again.");
                }
            } else {
                if (choice == 2) {
                    goBack = true; // Set goBack to true to exit the loop
                } else {
                    outputDevice.writeMessage("Invalid choice. Please try again.");
                }
            }
        }
    }

    public static int getValidatedRating(Scanner scanner, OutputDevice outputDevice) {
        int rating;
        do {
            outputDevice.writeMessage("Enter rating (1-5): ");
            while (!scanner.hasNextInt()) {
                outputDevice.writeMessage("Invalid input. Please enter a number between 1 and 5.");
                scanner.next(); // discard invalid input
            }
            rating = scanner.nextInt();
            scanner.nextLine(); // consume newline
        } while (rating < 1 || rating > 5);
        return rating;
    }


    private static void addReview(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your review: ");
        String reviewText = scanner.nextLine();

        outputDevice.writeMessage("Enter a rating for the review (1-5): ");
        int rating = getValidatedRating(scanner, outputDevice);
        scanner.nextLine(); // consume newline

        // Use the addReviewToBook method in Application to add and save the review
        Review review = new Review(reviewText, rating);
        book.addReview(review);  // Add review to the book
        outputDevice.writeMessage("Review added successfully.");

        // Save the updated book state to JSON
        app.saveBooksToJson("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json");
    }

    private static void addRating(Scanner scanner, OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("Enter your rating for the book (1-5): ");
        int rating = getValidatedRating(scanner, outputDevice);
        scanner.nextLine(); // consume newline
        book.addRating(rating);
        outputDevice.writeMessage("Rating added successfully.");
        // Save the updated book state to JSON
        app.saveBooksToJson("C:\\Users\\iulia\\IdeaProjects\\Book_Review_project\\src\\main\\resources\\Books.json");
    }

    private static void viewBookReviews(OutputDevice outputDevice, Book book) {
        outputDevice.writeMessage("\n--- Book Reviews ---");
        outputDevice.writeMessage("Book: " + book.getTitle() + " by " + book.getAuthor());
        outputDevice.writeMessage("Average Rating: " + book.getAverageRating());

        if (book.getReviews().isEmpty()) {
            outputDevice.writeMessage("No reviews available.");
        } else {
            for (Review review : book.getReviews()) {
                outputDevice.writeMessage("- " + review.getReviewText() + " (Rating: " + review.getRating() + ")");
            }
        }
    }
}

*/

package org.example;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static Application app;
    private static Library library;
    private static User currentUser;
    private static InputDevice inputDevice;
    private static OutputDevice outputDevice;

    public static void main(String[] args) {
        // Initialize input and output devices
        inputDevice = new InputDevice();
        outputDevice = new OutputDevice();

        // Initialize the application and library
        app = new Application(inputDevice, outputDevice);
        library = new Library();

        // Load data from the database
        loadInitialData();

        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        // Start the main application loop
        mainMenu(scanner);

        // Close the scanner when the application exits
        scanner.close();
    }


    private static void loadInitialData() {
        outputDevice.writeMessage("Loading data from database...");
        app.loadBooksFromDatabase();
        app.loadUsersFromDatabase();
        app.loadCategoriesFromDatabase();

        // Debug log for loaded users
        System.out.println("Loaded users:");
        for (User user : library.getUsers()) {
            System.out.println(user.getUsername());
        }

        outputDevice.writeMessage("Data loaded successfully.");
    }

    private static void mainMenu(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            outputDevice.writeMessage("\n--- Main Menu ---");
            outputDevice.writeMessage("1. Login as User");
            outputDevice.writeMessage("2. Login as Admin");
            outputDevice.writeMessage("3. Guest Access");
            outputDevice.writeMessage("4. Exit");
            outputDevice.writeMessage("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1: // User Login
                    outputDevice.writeMessage("\n--- User Login ---");
                    userLogin(scanner); // Use the provided user login logic
                    break;

                case 2: // Admin Login
                    outputDevice.writeMessage("\n--- Admin Login ---");
                    adminLogin(scanner); // Use the provided admin login logic
                    break;

                case 3: // Guest Access
                    outputDevice.writeMessage("\n--- Guest Access ---");
                    guestMenu(scanner);
                    break;

                case 4: // Exit
                    outputDevice.writeMessage("Exiting the application...");
                    exit = true;
                    break;

                default:
                    outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        }
    }

    private static void adminMenu(Scanner scanner) {
        boolean logout = false;

        while (!logout) {
            outputDevice.writeMessage("\n--- Admin Menu ---");
            outputDevice.writeMessage("1. Add a new book");
            outputDevice.writeMessage("2. Add a new category");
            outputDevice.writeMessage("3. Log out");
            outputDevice.writeMessage("Choose an option:");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addBookToDatabase(scanner);
                    break;
                case 2:
                    addCategoryToDatabase(scanner);
                    break;
                case 3:
                    outputDevice.writeMessage("Logging out...");
                    logout = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        }
    }
    private static void guestMenu(Scanner scanner) {
        boolean logout = false;

        while (!logout) {
            outputDevice.writeMessage("\n--- Guest Menu ---");
            outputDevice.writeMessage("1. View all books");
            outputDevice.writeMessage("2. Search for a book by title and author");
            outputDevice.writeMessage("3. Log out");
            outputDevice.writeMessage("Choose an option:");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    searchBookByTitleAndAuthor(scanner);
                    break;
                case 3:
                    outputDevice.writeMessage("Logging out...");
                    logout = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        }
    }

    private static void addBookToDatabase(Scanner scanner) {
        outputDevice.writeMessage("Enter book title:");
        String title = scanner.nextLine();

        outputDevice.writeMessage("Enter book author:");
        String author = scanner.nextLine();

        outputDevice.writeMessage("Enter book category:");
        String category = scanner.nextLine();

        outputDevice.writeMessage("Enter average rating (optional, default 0):");
        double averageRating = scanner.hasNextDouble() ? scanner.nextDouble() : 0.0;
        scanner.nextLine(); // Consume newline

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO books (title, author, category, average_rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setDouble(4, averageRating);
            stmt.executeUpdate();
            outputDevice.writeMessage("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("Error adding book to the database.");
        }
    }

    private static void addCategoryToDatabase(Scanner scanner) {
        outputDevice.writeMessage("Enter category name:");
        String categoryName = scanner.nextLine();

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO categories (name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, categoryName);
            stmt.executeUpdate();
            outputDevice.writeMessage("Category added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("Error adding category to the database.");
        }
    }


    private static void loginMenu(Scanner scanner) {
        outputDevice.writeMessage("Login as:\n1. User\n2. Admin");
        outputDevice.writeMessage("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                userLogin(scanner);
                break;
            case 2:
                adminLogin(scanner);
                break;
            default:
                outputDevice.writeMessage("Invalid choice. Please try again.");
                loginMenu(scanner); // Restart the login menu
                break;
        }
    }

    private static void userLogin(Scanner scanner) {
        outputDevice.writeMessage("Enter your username: ");
        String username = scanner.nextLine();
        outputDevice.writeMessage("Enter your password: ");
        String password = scanner.nextLine();

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT user_id, username, password FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                currentUser = new User(username, password) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }

                    @Override
                    public void save() {
                    }
                };
                currentUser.setUserId(userId);
                outputDevice.writeMessage("Welcome back, " + username + "!");
                userMenu(scanner);
            } else {
                outputDevice.writeMessage("User not found. Creating a new user...");
                createNewUser(scanner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("An error occurred during user login. Please try again.");
        }
    }

    private static void adminLogin(Scanner scanner) {
        outputDevice.writeMessage("Enter admin username: ");
        String adminUsername = scanner.nextLine();
        outputDevice.writeMessage("Enter admin password: ");
        String adminPassword = scanner.nextLine();

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT admin_id, username, password FROM admins WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, adminUsername);
            stmt.setString(2, adminPassword);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                outputDevice.writeMessage("Welcome, Admin " + adminUsername + "!");
                adminMenu(scanner);
            } else {
                outputDevice.writeMessage("Invalid admin credentials. Please try again.");
                loginMenu(scanner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("An error occurred during admin login. Please try again.");
        }
    }

    private static void createNewUser(Scanner scanner) {
        outputDevice.writeMessage("Enter a new username: ");
        String newUsername = scanner.nextLine();
        outputDevice.writeMessage("Enter a password: ");
        String newPassword = scanner.nextLine();

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newUserId = generatedKeys.getInt(1);
                currentUser = new User(newUsername, newPassword) {
                    @Override
                    public int compareTo(User o) {
                        return 0;
                    }

                    @Override
                    public void save() {
                    }
                };
                currentUser.setUserId(newUserId);
                outputDevice.writeMessage("New user created successfully.");
                userMenu(scanner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("An error occurred while creating a new user. Please try again.");
        }
    }


    private static void searchBookByTitleAndAuthor(Scanner scanner) {
        outputDevice.writeMessage("Enter the title of the book:");
        String title = scanner.nextLine();

        outputDevice.writeMessage("Enter the author of the book:");
        String author = scanner.nextLine();

        // Search for the book in the database
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT * FROM books WHERE title = ? AND author = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, author);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String bookTitle = rs.getString("title");
                String bookAuthor = rs.getString("author");
                double averageRating = rs.getDouble("average_rating");

                outputDevice.writeMessage("\n--- Book Found ---");
                outputDevice.writeMessage(bookTitle + " by " + bookAuthor + " - Average Rating: " + averageRating);
            } else {
                outputDevice.writeMessage("No book found with the specified title and author.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userMenu(Scanner scanner) {
        boolean logout = false;
        while (!logout) {
            outputDevice.writeMessage("\n--- User Menu ---");
            outputDevice.writeMessage("1. View your reading list");
            outputDevice.writeMessage("2. Add a book to your reading list");
            outputDevice.writeMessage("3. Add a review");
            outputDevice.writeMessage("4. Add a rating");
            outputDevice.writeMessage("5. Log out");
            outputDevice.writeMessage("Choose an option: ");

            int choice = inputDevice.nextInt();

            switch (choice) {
                case 1:
                    viewReadingList();
                    break;
                case 2:
                    addBookToReadingList(scanner);
                    break;
                case 3: addReview(scanner);
                break;
                case 4: addRating(scanner);
                case 5:
                    outputDevice.writeMessage("Logging out...");
                    currentUser = null;
                    logout = true;
                    break;
                default:
                    outputDevice.writeMessage("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewAllBooks() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT * FROM books";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            outputDevice.writeMessage("\n--- Books in the Library ---");
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                double averageRating = rs.getDouble("average_rating");

                outputDevice.writeMessage(title + " by " + author + " - Average Rating: " + averageRating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private static void viewCategories() {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT name FROM categories"; // SQL query to retrieve all category names
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            outputDevice.writeMessage("\n--- Categories ---");

            // Iterate through the result set and display each category
            while (rs.next()) {
                String categoryName = rs.getString("name");
                outputDevice.writeMessage("Category: " + categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputDevice.writeMessage("Error retrieving categories from the database.");
        }
    }


    private static void viewReadingList() {
        if (currentUser != null) {
            outputDevice.writeMessage("\n--- Your Reading List ---");

            // Fetch the reading list from the database
            List<Book> readingList = getReadingListFromDatabase(currentUser);
            if (readingList.isEmpty()) {
                outputDevice.writeMessage("Your reading list is empty.");
            } else {
                for (Book book : readingList) {
                    outputDevice.writeMessage(book.getTitle() + " by " + book.getAuthor());
                }
            }
        } else {
            outputDevice.writeMessage("No user logged in.");
        }
    }

    private static List<Book> getReadingListFromDatabase(User user) {
        List<Book> readingList = new ArrayList<>(); // Use java.util.List and ArrayList

        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "SELECT b.book_id, b.title, b.author " +
                    "FROM books b " +
                    "JOIN readinglist_books rl ON b.book_id = rl.book_id " +
                    "JOIN readinglists r ON rl.list_id = r.list_id " +
                    "WHERE r.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");

                // Create a Book object and add it to the reading list
                Book book = new Book(title, author, 0.0);
                book.setBookId(bookId);
                readingList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readingList;
    }

    private static void addReview(Scanner scanner) {
        if (currentUser != null) {
            outputDevice.writeMessage("Enter the title of the book you want to review:");
            String title = scanner.nextLine();

            // Search for the book in the database
            Book book = app.searchBookByTitleFromDatabase(title);
            if (book != null) {
                outputDevice.writeMessage("Enter your review:");
                String reviewText = scanner.nextLine();

                outputDevice.writeMessage("Enter a rating for the book (1-5):");
                int rating = getValidatedRating(scanner);

                // Save the review to the database
                saveReviewToDatabase(currentUser, book, reviewText, rating);

                outputDevice.writeMessage("Review added successfully.");
            } else {
                outputDevice.writeMessage("Book not found.");
            }
        } else {
            outputDevice.writeMessage("No user logged in.");
        }
    }

    private static void saveReviewToDatabase(User user, Book book, String reviewText, int rating) {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            String query = "INSERT INTO reviews (book_id, user_id, review_text, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, user.getUserId());
            stmt.setString(3, reviewText);
            stmt.setInt(4, rating);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void addRating(Scanner scanner) {
        if (currentUser != null) {
            outputDevice.writeMessage("Enter the title of the book you want to rate:");
            String title = scanner.nextLine();

            // Search for the book in the database
            Book book = app.searchBookByTitleFromDatabase(title);
            if (book != null) {
                outputDevice.writeMessage("Enter your rating for the book (1-5):");
                int rating = getValidatedRating(scanner);

                // Update the book's average rating in the database
                updateBookAverageRating(book, rating);

                outputDevice.writeMessage("Rating added successfully.");
            } else {
                outputDevice.writeMessage("Book not found.");
            }
        } else {
            outputDevice.writeMessage("No user logged in.");
        }
    }

    private static int getValidatedRating(Scanner scanner) {
        int rating;

        while (true) {
            try {
                outputDevice.writeMessage("Enter a rating (1-5): ");
                rating = Integer.parseInt(scanner.nextLine());

                // Check if the rating is within the valid range
                if (rating >= 1 && rating <= 5) {
                    break;
                } else {
                    outputDevice.writeMessage("Invalid input. Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                outputDevice.writeMessage("Invalid input. Please enter a valid number.");
            }
        }

        return rating;
    }

    private static void updateBookAverageRating(Book book, int newRating) {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            // Retrieve the current average rating and number of ratings
            String selectQuery = "SELECT average_rating FROM books WHERE book_id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, book.getBookId());
            ResultSet rs = selectStmt.executeQuery();

            double averageRating = 0.0;
            if (rs.next()) {
                averageRating = rs.getDouble("average_rating");
            }

            // Update the average rating
            averageRating = (averageRating + newRating) / 2; // Simple average logic
            String updateQuery = "UPDATE books SET average_rating = ? WHERE book_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, averageRating);
            updateStmt.setInt(2, book.getBookId());
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void addBookToReadingList(Scanner scanner) {
        if (currentUser != null) {
            outputDevice.writeMessage("Enter the title of the book to add to your reading list: ");
            String title = scanner.nextLine();

            // Search for the book in the database
            Book book = app.searchBookByTitleFromDatabase(title);
            if (book != null) {
                // Add the book to the in-memory reading list
                currentUser.getReadingList().add(book);

                // Save the book to the database
                saveBookToReadingListInDatabase(currentUser, book);

                outputDevice.writeMessage("Book added to your reading list.");
            } else {
                outputDevice.writeMessage("Book not found.");
            }
        } else {
            outputDevice.writeMessage("No user logged in.");
        }
    }

    private static void saveBookToReadingListInDatabase(User user, Book book) {
        try (Connection conn = DatabaseConnectionUtil.getConnection()) {
            // Check if the user already has a reading list in the database
            String selectListQuery = "SELECT list_id FROM readinglists WHERE user_id = ?";
            PreparedStatement selectListStmt = conn.prepareStatement(selectListQuery);
            selectListStmt.setInt(1, user.getUserId()); // Use the passed user object directly
            ResultSet rs = selectListStmt.executeQuery();

            int listId;
            if (rs.next()) {
                // Retrieve the existing list ID
                listId = rs.getInt("list_id");
            } else {
                // Create a new reading list for the user
                String insertListQuery = "INSERT INTO readinglists (user_id, list_name) VALUES (?, ?)";
                PreparedStatement insertListStmt = conn.prepareStatement(insertListQuery, Statement.RETURN_GENERATED_KEYS);
                insertListStmt.setInt(1, user.getUserId());
                insertListStmt.setString(2, "Default Reading List");
                insertListStmt.executeUpdate();

                ResultSet generatedKeys = insertListStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    listId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to create a new reading list.");
                }
            }

            // Add the book to the reading list in the database
            String insertBookQuery = "INSERT INTO readinglist_books (list_id, book_id) VALUES (?, ?)";
            PreparedStatement insertBookStmt = conn.prepareStatement(insertBookQuery);
            insertBookStmt.setInt(1, listId);
            insertBookStmt.setInt(2, book.getBookId());
            insertBookStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
