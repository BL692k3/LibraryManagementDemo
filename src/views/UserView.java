package views;

import controllers.UserController;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import models.User;
import utils.PasswordHasher;

public class UserView extends JPanel {
    private JTable table;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton addButton;
    private UserController userController;

    public UserView() {
        // Create the non-editable table model
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create the table and set the model
        String[] columnNames = { "ID", "Name", "Email", "Username", "Password", "UserRole" };
        tableModel.setColumnIdentifiers(columnNames);
        table = new JTable(tableModel);

        // Create an instance of the UserController class
        userController = new UserController();

        // Add a selection listener to the table
        table.getSelectionModel().addListSelectionListener(e -> {
            // Enable the delete and update buttons if a row is selected
            boolean rowSelected = table.getSelectedRow() != -1;
            deleteButton.setEnabled(rowSelected);
            updateButton.setEnabled(rowSelected);
        });
        
        // Add an add button
        addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            // Display the add user dialog
            AddUserDialog dialog = new AddUserDialog();
            dialog.setVisible(true);
        });

        // Add the add button to the view

        // Add a delete button
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            // Get the selected row index and user ID
            int rowIndex = table.getSelectedRow();
            int userId = (int) table.getValueAt(rowIndex, 0);

            // Remove the user from the database
            userController.removeUser(userId);

            // Refresh the table
            displayAllUsers(userController.getAllUsers());
        });

        // Add an update button
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> {
            // Get the selected row index and user ID
            int rowIndex = table.getSelectedRow();
            int userId = (int) table.getValueAt(rowIndex, 0);

            // Get the selected user
            User selectedUser = userController.getUserById(userId);

            // Display the update user dialog
            UpdateUserDialog dialog = new UpdateUserDialog(selectedUser);
            dialog.setVisible(true);
        });

        // Add the table to a scroll pane and set as the content of this view
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        // Add the delete and update buttons to the view
        add(addButton);
        add(updateButton);
        add(deleteButton);
        

        // Set the panel size
        setPreferredSize(new Dimension(600, 400));

        // Display all users
        displayAllUsers(userController.getAllUsers());
    }

    // Display all users
    public void displayAllUsers(List<User> userList) {
        // Clear the table
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);

        // Add the users to the table
        for (User user : userList) {
            Object[] row = {user.getId(), user.getName(), user.getEmail(), user.getUsername(), user.getPassword(), user.getUserRole()};
            tableModel.addRow(row);
        }
    }

    // Dialog for updating user information
    public class UpdateUserDialog extends JDialog {
        private JTextField nameField;
        private JTextField emailField;
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton saveButton;
        private JButton cancelButton;

        public UpdateUserDialog(User user) {
            // Set the dialog title
            setTitle("Update User Information");

            // Set the dialog size
            setSize(new Dimension(400, 200));

            // Center the dialog on the screen
            setLocationRelativeTo(null);

            // Set the layout to a grid
            setLayout(new GridLayout(5, 2));

            // Create the name label and field
            JLabel nameLabel = new JLabel("Name:");
            nameField = new JTextField(user.getName());
            add(nameLabel);
            add(nameField);

            // Create the email label and field
            JLabel emailLabel = new JLabel("Email:");
            emailField = new JTextField(user.getEmail());
            add(emailLabel);
            add(emailField);

            // Create the username label and field
            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField(user.getUsername());
            add(usernameLabel);
            add(usernameField);

            // Create the password label and field
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();
            add(passwordLabel);
            add(passwordField);

            // Create the save button
            saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                // Update the user's information in the User object
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setUsername(usernameField.getText());
                user.setPassword(PasswordHasher.hashPassword(new String(passwordField.getPassword())));

                // Update the user's information in the database
                boolean success = userController.updateUser(user);

                if (success) {
                    // Refresh the table
                    displayAllUsers(userController.getAllUsers());

                    // Close the dialog
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user information. Please try again.");
                }
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
    // Dialog for adding new users
    public class AddUserDialog extends JDialog {
        private JTextField nameField;
        private JTextField emailField;
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton saveButton;
        private JButton cancelButton;

        private final String nameRegex = "^[a-zA-Z ]+$";
        private final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        private final String usernameRegex = "^[a-zA-Z]+$";
        private final int PASSWORD_MIN_LENGTH = 6;
        private final int PASSWORD_MAX_LENGTH = 12;

        public AddUserDialog() {
            // Set the dialog title
            setTitle("Add User");

            // Set the dialog size
            setSize(new Dimension(400, 200));

            // Center the dialog on the screen
            setLocationRelativeTo(null);

            // Set the layout to a grid
            setLayout(new GridLayout(5, 2));

            // Create the name label and field
            JLabel nameLabel = new JLabel("Name:");
            nameField = new JTextField();
            add(nameLabel);
            add(nameField);

            // Create the email label and field
            JLabel emailLabel = new JLabel("Email:");
            emailField = new JTextField();
            add(emailLabel);
            add(emailField);

            // Create the username label and field
            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField();
            add(usernameLabel);
            add(usernameField);

            // Create the password label and field
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();
            add(passwordLabel);
            add(passwordField);

            // Create the save button
            saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                // Validate name
                if (!Pattern.matches(nameRegex, name)) {
                    JOptionPane.showMessageDialog(this, "Invalid name. Only letters and spaces are allowed.");
                    return;
                }

                // Validate email
                if (!Pattern.matches(emailRegex, email)) {
                    JOptionPane.showMessageDialog(this, "Invalid email address format.");
                    return;
                }

                // Validate username
                if (!Pattern.matches(usernameRegex, username)) {
                    JOptionPane.showMessageDialog(this, "Invalid username. Only letters are allowed.");
                    return;
                }

                // Validate password
                if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
                    JOptionPane.showMessageDialog(this, "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters long.");
                    return;
                }

                // Create the new user
                User newUser = new User(0, name, email, username, password, "MEM");

                // Add the user to the database
                if (userController.addUser(newUser)) {
                    // Refresh the table
                    displayAllUsers(userController.getAllUsers());

                    // Close the dialog
                    dispose();
                }
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