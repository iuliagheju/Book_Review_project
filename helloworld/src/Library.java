import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Library implements Savable, Displayable {
    private ArrayList<Book> books;
    private ArrayList<User> users; // Added users list for sorting

    public Library() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
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
                books.add(new Book(title, author, rating) {
                    @Override
                    public void display() {

                    }
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

    public void sortBooksByRating() {
        Collections.sort(books);
        for (Book book : books) {
            System.out.println(book);
        }
    }

    @Override
    public void display() {
        for (Book book : books) {
            book.display();
        }
    }

    @Override
    public void save() {
        System.out.println("Saving library data...");
    }
}
