package views;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import controllers.UserController;
import models.User;
import utils.PasswordHasher;

public class RegistrationView extends JFrame implements ActionListener {

    private JLabel nameLabel, emailLabel, usernameLabel, passwordLabel;
    private JTextField nameField, emailField, usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private UserController userController;

    public RegistrationView() {
        // Set up the user controller
        userController = new UserController();

        // Set up the frame
        setTitle("Library Management System - Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set up the panel
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the name label and field
        nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        // Add the email label and field
        emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        panel.add(emailLabel);
        panel.add(emailField);

        // Add the username label and field
        usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        // Add the password label and field
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        // Add the register button
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(new JLabel());
        panel.add(registerButton);

        // Add the panel to the frame
        add(panel);

        // Show the frame
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            // Get the user input
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Validate the user input
            // ^[a-zA-Z ]+$: matches only letters and spaces
            // ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$: matches a valid email address format
            // ^[a-zA-Z]+$: matches only letters
            if (name.length() > 30 || !name.matches("^[a-zA-Z ]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid name. Name should not be longer than 30 letters and should only contain letters and spaces.");
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid email address.");
                return;
            }
            if (username.length() > 12 || !username.matches("^[a-zA-Z]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid username. Username should not be longer than 12 letters and should only contain letters.");
                return;
            }
            if (password.length() < 6 || password.length() > 12) {
                JOptionPane.showMessageDialog(this, "Invalid password. Password should be between 6 and 12 characters long.");
                return;
            }

            // Hash the password
            String hashedPassword = PasswordHasher.hashPassword(password);

            // Get the id for the new user
            int id = 1;
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    id = Integer.parseInt(parts[0]);
                }
                id++;
            } catch (IOException ex) {
                System.err.println("Error reading users file: " + ex.getMessage());
            }

            // Create a new user
            User newUser = new User(id, name, email, username, hashedPassword);

            // Add the user to the system...
            boolean success = userController.addUser(newUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose(); // Close the RegistrationView

                LoginView loginView = new LoginView(); // Open the LoginView
                loginView.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }
        }
    }
}