package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import models.Book;
import views.BookView;

public class BookController {
    private List<Book> bookList;

    public BookController() {
        bookList = new ArrayList<>();
        // Read the book data from file and add it to the book list
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String author = parts[2];
                String description = parts[3];
                int quantity = Integer.parseInt(parts[4]);
                Book book = new Book(id, title, author, description, quantity);
                bookList.add(book);
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }
    
    // Add a book to the list and text file
    public void addBook(Book book) {
        BookView bookView = new BookView();
        if (bookList.isEmpty()) { // check if the list is empty
            bookList.add(book);
        } else {
            Book lastBook = bookList.get(bookList.size() - 1); // get the last book in the list
            int newId = lastBook.getId() + 1; // increment the ID by 1
            book.setId(newId); // set the new ID for the book
            bookList.add(book); // add the book to the list
        }
        bookView.displayAllBooks(bookList); // display the updated list in the view
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("books.txt", true)))) {
            out.println(book.getId() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getDescription() + "," + book.getQuantity());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Remove a book from the list and text file
    public void removeBook(int id) {
        BookView bookView = new BookView();
        for (Book book : bookList) {
            if (book.getId() == id) {
                bookList.remove(book);
                bookView.displayAllBooks(bookList);
                updateBookFile(book);
                break;
            }
        }
    }

    // Get a book by ID
    public Book getBookById(int id) {
        for (Book book : bookList) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    // Get all books in the list
    public List<Book> getAllBooks() {
        bookList.clear(); // Clear the current bookList
        // Read the book data from file and add it to the book list
        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String author = parts[2];
                String description = parts[3];
                int quantity = Integer.parseInt(parts[4]);
                Book book = new Book(id, title, author, description, quantity);
                bookList.add(book);
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
        return bookList;
    }

    // Update a book in the list and text file
    public void updateBookFile(Book book) {
        List<Book> updatedBookList = new ArrayList<>();
        for (Book b : bookList) {
            if (b.getId() == book.getId()) {
                updatedBookList.add(book);
            } else {
                updatedBookList.add(b);
            }
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("books.txt")))) {
            for (Book b : updatedBookList) {
                out.println(b.getId() + "," + b.getTitle() + "," + b.getAuthor() + "," + b.getDescription() + "," + b.getQuantity());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}