package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controllers.BookController;
import controllers.BorrowController;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import models.Book;
import models.User;
import utils.SessionManager;

public class BookView extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton borrowButton; 
    private BorrowController borrowController = new BorrowController();
    public BookView() {
 
        BookController bookController = new BookController();
        User currentUser = (User) SessionManager.getInstance().get("currentUser");

        // Create the non-editable table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnNames = {"ID", "Title", "Author", "Description", "Quantity"};
        tableModel.setColumnIdentifiers(columnNames);
        bookTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);

        // Add the CRUD buttons for only for users with "ADM" role
        if (currentUser.getUserRole() != null && currentUser.getUserRole().equals("ADM")) {
        // Create the panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Create the add button and add an action listener
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookController bookController = new BookController();
                // Open a dialog to add a new book
                BookDialog bookDialog = new BookDialog(bookController);
                bookDialog.setVisible(true);
            }
        });
        buttonPanel.add(addButton);

        // Create the delete button and add an action listener
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookController bookController = new BookController();
                int selectedRow = bookTable.getSelectedRow();
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                bookController.removeBook(id);
                deleteButton.setEnabled(false);
                updateButton.setEnabled(false);
                displayAllBooks(bookController.getAllBooks());
            }
        });
        buttonPanel.add(deleteButton);

        // Create the update button and add an action listener
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookController bookController = new BookController();
                int selectedRow = bookTable.getSelectedRow();
                int id = (int) tableModel.getValueAt(selectedRow, 0);

                // Get the book by ID and open a dialog to update it
                Book book = bookController.getBookById(id);
                BookDialog bookDialog = new BookDialog(bookController, book);
                bookDialog.setVisible(true);

                deleteButton.setEnabled(false);
                updateButton.setEnabled(false);
            }
        });
        buttonPanel.add(updateButton);

        // Create the borrow button and add an action listener
        borrowButton = new JButton("Borrow");
        borrowButton.setEnabled(false);
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookController bookController = new BookController();
                int selectedRow = bookTable.getSelectedRow();
                int bookId = (int) tableModel.getValueAt(selectedRow, 0);
                int userId = currentUser.getId();
                borrowController.addBorrow(userId, bookId);
                displayAllBooks(bookController.getAllBooks());
            }
        });
        buttonPanel.add(borrowButton);

        // Add the scroll pane and button panel to the view
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add a selection listener to the table to enable/disable the delete, update, and borrow buttons
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            boolean enabled = bookTable.getSelectedRow() != -1;
            deleteButton.setEnabled(enabled);
            updateButton.setEnabled(enabled);
            borrowButton.setEnabled(enabled);
        });
        } 
        add(scrollPane, BorderLayout.CENTER);

        displayAllBooks(bookController.getAllBooks()); // display all books when the view is created
    }

    public void displayAllBooks(List<Book> bookList) {
        tableModel.setRowCount(0);
        for (Book book : bookList) {
            Object[] rowData = {book.getId(), book.getTitle(), book.getAuthor(), book.getDescription(), book.getQuantity()};
            tableModel.addRow(rowData);
        }
    }
    private class BookDialog extends JDialog {
        private JTextField titleField;
        private JTextField authorField;
        private JTextField descriptionField;
        private JTextField quantityField;
        private JButton saveButton;
        private JButton cancelButton;

        public BookDialog(BookController bookController) {
            // Set the dialog title
            setTitle("Add Book");

            // Set the dialog size
            setSize(new Dimension(400, 200));

            // Center the dialog on the screen
            setLocationRelativeTo(null);

            // Set the layout to a grid
            setLayout(new GridLayout(5, 2));

            // Create the title label and field
            JLabel titleLabel = new JLabel("Title:");
            titleField = new JTextField();
            add(titleLabel);
            add(titleField);

            // Create the author label and field
            JLabel authorLabel = new JLabel("Author:");
            authorField = new JTextField();
            add(authorLabel);
            add(authorField);

            // Create the description label and field
            JLabel descriptionLabel = new JLabel("Description:");
            descriptionField = new JTextField();
            add(descriptionLabel);
            add(descriptionField);

            // Create the quantity label and field
            JLabel quantityLabel = new JLabel("Quantity:");
            quantityField = new JTextField();
            add(quantityLabel);
            add(quantityField);

            // Create the save button
            saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                // Validate the quantity input
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    if (quantity <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Create a new book with the entered information
                Book book = new Book(0, titleField.getText(), authorField.getText(), descriptionField.getText(), Integer.parseInt(quantityField.getText()));

                // Add the book to the database
                bookController.addBook(book);

                // Refresh the table
                displayAllBooks(bookController.getAllBooks());

                // Close the dialog
                dispose();
            });

            // Create the cancel button
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                // Close the dialog
                dispose();
            });

            // Add the buttons to the dialog
            add(saveButton);
            add(cancelButton);
        }

        public BookDialog(BookController bookController, Book book) {
            // Set the dialog title
            setTitle("Update Book Information");

            // Set the dialog size
            setSize(new Dimension(400, 200));

            // Center the dialog on the screen
            setLocationRelativeTo(null);

            // Set the layout to a grid
            setLayout(new GridLayout(5, 2));

            // Create the title label and field
            JLabel titleLabel = new JLabel("Title:");
            titleField = new JTextField(book.getTitle());
            add(titleLabel);
            add(titleField);

            // Create the author label and field
            JLabel authorLabel = new JLabel("Author:");
            authorField = new JTextField(book.getAuthor());
            add(authorLabel);
            add(authorField);

            // Create the description label and field
            JLabel descriptionLabel = new JLabel("Description:");
            descriptionField = new JTextField(book.getDescription());
            add(descriptionLabel);
            add(descriptionField);

            // Create the quantity label and field
            JLabel quantityLabel = new JLabel("Quantity:");
            quantityField = new JTextField(Integer.toString(book.getQuantity()));
            add(quantityLabel);
            add(quantityField);

            // Create the save button
            saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                // Update the book's information in the database
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setDescription(descriptionField.getText());
                book.setQuantity(Integer.parseInt(quantityField.getText()));
                bookController.updateBookFile(book);

                // Refresh the table
                displayAllBooks(bookController.getAllBooks());

                // Close the dialog
                dispose();
            });

            // Create the cancel button
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                // Close the dialog
                dispose();
            });

            // Add the buttons to the dialog
            add(saveButton);
            add(cancelButton);
        }
    }
}
