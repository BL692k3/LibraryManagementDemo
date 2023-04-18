package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.regex.Pattern;

import controllers.BookController;
import controllers.UserController;
import models.Book;
import models.User;
import utils.PasswordHasher;
import utils.SessionManager;

public class IndexView extends JFrame {
    private JTabbedPane tabbedPane;
    private UserView userView;
    private BookView bookView;

    private UserController userController;

    // regex patterns for input validation
    private final Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$");
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final Pattern usernamePattern = Pattern.compile("^[a-zA-Z]+$");
    private final int PASSWORD_MIN_LENGTH = 6;
    private final int PASSWORD_MAX_LENGTH = 12;

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

        // Create a panel for the logout and edit profile buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show a dialog for the user to edit their profile
                EditProfileDialog dialog = new EditProfileDialog(currentUser);
                dialog.setVisible(true);
            }
        });
        buttonPanel.add(editProfileButton);
        
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
        buttonPanel.add(logoutButton);

        // Add the button panel to the top of the content pane
        getContentPane().add(buttonPanel, BorderLayout.NORTH);

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
        if (e.getID() == WindowEvent.WINDOW_CLOSED) {
            // When the user closes the application, remove the "currentUser" from the SessionManager
            SessionManager.getInstance().remove("currentUser");
        }
    }

    /**
     * A dialog for the user to edit their profile.
     */
    private class EditProfileDialog extends JDialog {
        private User user;
        private JTextField nameTextField;
        private JTextField emailTextField;
        private JTextField usernameTextField;
        private JPasswordField currentPasswordField;
        private JPasswordField newPasswordField;
        private JPasswordField confirmPasswordField;

        public EditProfileDialog(User user) {
            super(IndexView.this, "Edit Profile", true);
            this.user = user;
            setSize(400, 300);
            setLocationRelativeTo(IndexView.this);

            // Create the input fields for the user to edit their profile
            JLabel nameLabel = new JLabel("Name:");
            nameTextField = new JTextField(user.getName(), 20);
            JLabel emailLabel = new JLabel("Email:");
            emailTextField = new JTextField(user.getEmail(), 20);
            JLabel usernameLabel = new JLabel("Username:");
            usernameTextField = new JTextField(user.getUsername(), 20);
            JLabel currentPasswordLabel = new JLabel("Current Password:");
            currentPasswordField = new JPasswordField(20);
            JLabel newPasswordLabel = new JLabel("New Password:");
            newPasswordField = new JPasswordField(20);
            JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
            confirmPasswordField = new JPasswordField(20);

            // Create a panel for the input fields
            JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
            panel.add(nameLabel);
            panel.add(nameTextField);
            panel.add(emailLabel);
            panel.add(emailTextField);
            panel.add(usernameLabel);
            panel.add(usernameTextField);
            panel.add(currentPasswordLabel);
            panel.add(currentPasswordField);
            panel.add(newPasswordLabel);
            panel.add(newPasswordField);
            panel.add(confirmPasswordLabel);
            panel.add(confirmPasswordField);

            // Create a "Save" button
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userController = new UserController();
                    // Get the input values
                    String name = nameTextField.getText();
                    String email = emailTextField.getText();
                    String username = usernameTextField.getText();
                    char[] currentPassword = currentPasswordField.getPassword();
                    char[] newPassword = newPasswordField.getPassword();
                    char[] confirmPassword = confirmPasswordField.getPassword();

                    // Validate the input values
                    if (!namePattern.matcher(name).matches()) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Invalid name.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!emailPattern.matcher(email).matches()) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Invalid email.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!usernamePattern.matcher(username).matches()) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Invalid username.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (currentPassword.length == 0) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Current password is required.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (newPassword.length > 0 && (newPassword.length < PASSWORD_MIN_LENGTH || newPassword.length > PASSWORD_MAX_LENGTH)) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters long.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (newPassword.length > 0 && !String.valueOf(newPassword).equals(String.valueOf(confirmPassword))) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!user.getPassword().equals(PasswordHasher.hashPassword(String.valueOf(currentPassword)))) {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Incorrect current password.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update the user's profile information
                    
                    user.setName(name);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(PasswordHasher.hashPassword(String.valueOf(newPassword)));
                    boolean success = userController.updateUser(user);
                    if (success) {
                        // Refresh the table
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Updated successfully!");
                        // Close the dialog
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(EditProfileDialog.this, "Failed to update user information. Please try again.");
                    }
                }
            });

            // Add the input fields and the "Save" button to the content pane
            getContentPane().add(panel, BorderLayout.CENTER);
            getContentPane().add(saveButton, BorderLayout.SOUTH);
        }
    }
}