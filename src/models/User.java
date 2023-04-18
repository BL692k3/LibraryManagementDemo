package models;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import utils.PasswordHasher;
/**
 *
 * @author Admin
 */
public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String userRole;

    public User(int id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRole = "MEM"; // Set default value
    }

    public User(int id, String name, String email, String username, String password, String userRole) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }
    
    public User(String username, String password, String userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
}