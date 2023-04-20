package views;

import Exceptions.ValidationException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.BoxLayout;
import models.User;
import controllers.UserController;
import utils.PasswordHasher;
import utils.SessionManager;

public class LoginView extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    public LoginView() {
        super("Login");

        JPanel fieldsPanel = new JPanel();
        // Create a panel for the form fields
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        JLabel usernameLabel = new JLabel("Username:");
        fieldsPanel.add(usernameLabel, BorderLayout.NORTH);
        // Add the username label and field to the panel
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        fieldsPanel.add(usernameField, BorderLayout.CENTER);
        
        JLabel passwordLabel = new JLabel("Password:");
        fieldsPanel.add(passwordLabel, BorderLayout.SOUTH);
        // Add the password label and field to the panel
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        fieldsPanel.add(passwordField, BorderLayout.SOUTH);
        
        // Add the registerButton
        registerButton = new JButton("Create an account");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationView registerView = new RegistrationView();
                setVisible(false);
                registerView.setVisible(true);
                dispose();
            }
        });
        
        // Add the loginButton and registerButton
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(fieldsPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            if (validateCredentials(username, password)) {
            // If the credentials are valid, set the "currentUser" session variable
            UserController userController = new UserController();
            User user = userController.getUser(username);
            SessionManager.getInstance().put("currentUser", user);
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            IndexView indexView = new IndexView();
            setVisible(false);
            indexView.setVisible(true);
            dispose();
            } else {
                throw new ValidationException("The username doesn't exist or the password is wrong. Please try again.");
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage());
        }
    }

    private boolean validateCredentials(String username, String password) {
        boolean isValid = false;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(",");
                if (user[3].equals(username)) {
                    String hashedPassword = PasswordHasher.hashPassword(password);
                    if (hashedPassword.equals(user[4])) {
                        isValid = true;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return isValid;
    }

    public static void main(String[] args) {
        LoginView loginView = new LoginView();
    }
}