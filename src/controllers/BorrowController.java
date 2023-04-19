package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import models.Book;
import models.Borrow;
import models.User;
import views.BorrowView;

public class BorrowController {
    private List<Borrow> borrowList;
    private BookController bookController;
    private UserController userController;

    public BorrowController() {
        borrowList = new ArrayList<>();
        this.bookController = new BookController();
        this.userController = new UserController();
        // Read the borrow data from file and add it to the borrow list
        try (BufferedReader reader = new BufferedReader(new FileReader("borrows.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int userId = Integer.parseInt(parts[1]);
                int bookId = Integer.parseInt(parts[2]);
                Date borrowDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[3]);
                Date returnDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[4]);
                Borrow borrow = new Borrow(id, userId, bookId, borrowDate, returnDate);
                borrowList.add(borrow);
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
    }

    // Add a borrow to the list and text file
    public boolean addBorrow(int userId, int bookId) {
        UserController userController = new UserController();
        BookController bookController = new BookController();
        // Check if the user can borrow the book
        User user = userController.getUserById(userId);
        Book book = bookController.getBookById(bookId);
        if (user == null || book == null) {
            displayErrorMessage("Invalid user or book ID.");
            return false;
        }
        if (book.getQuantity() == 0) {
            displayErrorMessage("The book is out of stock.");
            return false;
        }
        for (Borrow borrow : borrowList) {
            if (borrow.getUserId() == userId && borrow.getBookId() == bookId) {
                displayErrorMessage("The user has already borrowed this book.");
                return false;
            }
        }        
        // Create the borrow and save it to the list and text file
        int newId = borrowList.isEmpty() ? 1 : borrowList.get(borrowList.size() - 1).getId() + 1;
        Date borrowDate = Calendar.getInstance().getTime();
        Calendar returnDate = Calendar.getInstance();
        returnDate.add(Calendar.DATE, 14); // Borrow period is 14 days

        Borrow borrow = new Borrow(newId, userId, bookId, borrowDate, returnDate.getTime());
        borrowList.add(borrow);

        // Update the book quantity and save it to the text file
        book.setQuantity(book.getQuantity() - 1);
        bookController.updateBookFile(book);

        // Save the borrow to the text file
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("borrows.txt", true)))) {
            writer.println(newId + "," + userId + "," + bookId + "," + new SimpleDateFormat("yyyy-MM-dd").format(borrowDate) + "," + new SimpleDateFormat("yyyy-MM-dd").format(returnDate.getTime()));
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        displaySuccessMessage("The book has been borrowed successfully.");
        return false;
    }

    // Return a book and update the list and text file
    public void returnBook(int borrowId) {
        Borrow borrow = getBorrowById(borrowId);
        if (borrow == null) {
            displayErrorMessage("Invalid borrow ID.");
            return;
        }

        // Update the borrow return date
        borrow.setReturnDate(Calendar.getInstance().getTime());

        // Update the book quantity and save it to the text file
        Book book = bookController.getBookById(borrow.getBookId());
        if (book != null) {
            book.setQuantity(book.getQuantity() + 1);
            bookController.updateBookFile(book);
        }

        // Save the borrow to the text file
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("borrows.txt")))) {
            for (Borrow b : borrowList) {
                writer.println(b.getId() + "," + b.getUserId() + "," + b.getBookId() + "," + new SimpleDateFormat("yyyy-MM-dd").format(b.getBorrowDate()) + "," + new SimpleDateFormat("yyyy-MM-dd").format(b.getReturnDate()));
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        displaySuccessMessage("The book has been returned successfully.");
    }

    // Get a borrow by ID
    public Borrow getBorrowById(int borrowId) {
        for (Borrow borrow : borrowList) {
            if (borrow.getId() == borrowId) {
                return borrow;
            }
        }
        return null;
    }

    // Get all borrows for a user
    public List<Borrow> getBorrowsByUserId(int userId) {
        List<Borrow> userBorrows = new ArrayList<>();
        for (Borrow borrow : borrowList) {
            if (borrow.getUserId() == userId) {
                userBorrows.add(borrow);
            }
        }
        return userBorrows;
    }

    // Get all borrows for a book
    public List<Borrow> getBorrowsByBookId(int bookId) {
        List<Borrow> bookBorrows = new ArrayList<>();
        for (Borrow borrow : borrowList) {
            if (borrow.getBookId() == bookId) {
                bookBorrows.add(borrow);
            }
        }
        return bookBorrows;
    }
    
    //get the name of the book that was borrowed for a given borrowId
    public String getBookNameByBorrowId(int borrowId) {
        for (Borrow borrow : borrowList) {
            if (borrow.getId() == borrowId) {
                int bookId = borrow.getBookId();
                Book book = bookController.getBookById(bookId);
                if (book != null) {
                    return book.getTitle();
                }
            }
        }
        return "";
    }
    
    //get the username of the user who borrowed a book with the given borrowId
    public String getUserNameByBorrowId(int borrowId) {
        for (Borrow borrow : borrowList) {
            if (borrow.getId() == borrowId) {
                int userId = borrow.getUserId();
                User user = userController.getUserById(userId);
                if (user != null) {
                    return user.getUsername();
                }
            }
        }
        return "";
    }
    
    //Returns a borrow list when called
    public List<Borrow> getBorrowList() {
        return borrowList;
    }
    // Display an error message in a dialog box
    public void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Display a success message in a dialog box
    public void displaySuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}