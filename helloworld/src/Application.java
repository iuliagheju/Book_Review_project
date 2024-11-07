import java.util.ArrayList;
import java.util.List;

public class Application implements Displayable {
    private List<Book> books;
    private InputDevice inputDevice;
    private OutputDevice outputDevice;
    private List<User> users;

    public Application() {
        this.users = new ArrayList<>();// Initialize the users list here
    }

    public Application(InputDevice input, OutputDevice output) {
        this.inputDevice = input;
        this.outputDevice = output;
    }

    @Override
    public void display() {
        outputDevice.writeMessage("Displaying application content.");
    }


    public void addReview(int index, User user, String reviewText) {
        if (index >= user.getReviews().size()) {
            outputDevice.writeMessage("Invalid index for review.");
        } else {
            Review newReview = new Review(reviewText, 5); // Assuming a default rating of 5
            user.addReview(index, newReview);
            Book book = user.getBook(index); // Assume getBook method requires index
            if (book != null) {
                outputDevice.writeMessage("Review added for book: " + book.getTitle());
            }
        }
    }

    public void displayUserReviews(User user) {
        outputDevice.writeMessage("Displaying reviews for user: " + user.getUsername());
        for (int i = 0; i < user.getReviews().size(); i++) {
            Book book = user.getBook(i);
            if (book != null) {
                outputDevice.writeMessage("Book: " + book.getTitle());
            }
            Review review = (Review) user.getReviews().toArray()[i]; // Assuming getReviews() returns a Collection of Review objects
            if (review != null) {
                outputDevice.writeMessage("- " + review.getReviewText());
            }
        }
    }

    public void rateBook(User user, int rating) {
        Book book = user.getBook(1);
        if (book != null && rating >= 1 && rating <= 5) {
            book.addRating(rating);
            outputDevice.writeMessage("You rated \"" + book.getTitle() + "\" " + rating + " stars.");
        } else {
            outputDevice.writeMessage("Invalid rating. Please provide a rating between 1 and 5.");
        }
    }

    public void displayBookRating(User user) {
        Book book = user.getBook(1);
        if (book != null) {
            double averageRating = book.getAverageRating();
            int numberOfRatings = book.getNumberOfRatings();

            if (numberOfRatings > 0) {
                outputDevice.writeMessage("The book \"" + book.getTitle() + "\" has an average rating of " + averageRating +
                        " stars (" + numberOfRatings + " ratings).");
            } else {
                outputDevice.writeMessage("No ratings available for the book \"" + book.getTitle() + "\".");
            }
        }
    }



    public void generateUserActivityReport() {
        users.forEach(user -> outputDevice.writeMessage(user.getUsername() + " - " + user.getReviews().size() + " reviews"));
    }

    public void addUser(User currentUser) {
        if (currentUser != null) {
            users.add(currentUser);
            System.out.println("User " + currentUser.getUsername() + " added successfully.");
        } else {
            System.out.println("Invalid user.");
        }
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            System.out.println("\n--- Available Books ---");
            for (Book book : books) {
                System.out.println(book);  // Assuming Book has a toString method
            }
        }
    }

    public Book searchBookByTitle(String query) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(query)) {
                return book;  // Return the first match
            }
        }
        System.out.println("No book found with title: " + query);
        return null;  // Return null if no match is found
    }


    public Book searchBookByAuthor(String query) {
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(query)) {
                return book;  // Return the first match
            }
        }
        System.out.println("No book found by author: " + query);
        return null;  // Return null if no match is found
    }


    public Book searchBookByTitleAndAuthor(String query, String authorQuery) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(query) && book.getAuthor().equalsIgnoreCase(authorQuery)) {
                return book;  // Return the first match
            }
        }
        System.out.println("No book found with title: " + query + " and author: " + authorQuery);
        return null;  // Return null if no match is found
    }

}
