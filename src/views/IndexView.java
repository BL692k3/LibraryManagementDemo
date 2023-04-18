package views;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import controllers.BookController;
import controllers.UserController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import models.Book;
import models.User;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import utils.SessionManager;

public class IndexView extends JFrame {
    private JTabbedPane tabbedPane;
    private UserView userView;
    private BookView bookView;

    private UserController userController;
    private BookController bookController;

    public IndexView() {
        super("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Check if the user is logged in
        User currentUser = (User) SessionManager.getInstance().get("currentUser");
        if (currentUser == null) {
            // If the user is not logged in, redirect to the LoginView
            JOptionPane.showMessageDialog(null, "Please log in first.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            LoginView loginView = new LoginView();
            return;
        }

        // Create the non-editable table model
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabbedPane = new JTabbedPane();
        userView = new UserView();
        bookView = new BookView(new BookController());
        
        // Create a panel for the logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Remove the "currentUser" from the SessionManager and redirect to the LoginView
                SessionManager.getInstance().remove("currentUser");
                dispose();
                LoginView loginView = new LoginView();
            }
        });
        logoutPanel.add(logoutButton);

        // Add the logout panel to the top of the content pane
        getContentPane().add(logoutPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        userView = new UserView();
        bookView = new BookView(new BookController());
        
        // Add the "Users" tab only for users with "ADM" role
        if (currentUser.getUserRole() != null && currentUser.getUserRole().equals("ADM")) {
            tabbedPane.addTab("Users", userView);
        } 

        // Add the "Books" tab for all logged-in users
        tabbedPane.addTab("Books", bookView);

        add(tabbedPane);
        setVisible(true);
    }

    public void displayAllUsers(List<User> userList) {
        userView.displayAllUsers(userList);
    }

    public void displayAllBooks(List<Book> bookList) {
        bookView.displayAllBooks(bookList);
    }
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if(e.getID() == WindowEvent.WINDOW_CLOSED){
            // When the user closes the application, remove the "currentUser" from the SessionManager
            SessionManager.getInstance().remove("currentUser");
        }
    }
}