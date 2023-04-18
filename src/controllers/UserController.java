package controllers;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.User;
import utils.PasswordHasher;

public class UserController {
    private List<User> userList;

    public UserController() {
        userList = new ArrayList<>();

        // Read the user data from file and add it to the user list
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                String username = parts[3];
                String password = parts[4];
                User user = new User(id, name, email, username, password);
                userList.add(user);
            }
        } catch (IOException e) {
            System.out.println("Failed to read user data from file: " + e.getMessage());
        }
    }

    // Add a user to the users.txt file
    public boolean addUser(User user) {
        // Read the existing users from the file and get the last user's id
        int lastUserId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int userId = Integer.parseInt(fields[0]);
                if (userId > lastUserId) {
                    lastUserId = userId;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read user data from file: " + e.getMessage());
            return false;
        }

        // Set the new user's id to the last user's id + 1
        user.setId(lastUserId + 1);

        // Check if the user already exists based on the username
        if (userList.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            System.out.println("User with username " + user.getUsername() + " already exists.");
            return false;
        }

        // Hash the password before saving it
        user.setPassword(PasswordHasher.hashPassword(user.getPassword()));

        // Add the user to the list and write it to the file
        userList.add(user);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getId() + "," + user.getName() + "," + user.getEmail() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getUserRole() + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write user data to file: " + e.getMessage());
            return false;
        }

        return true;
    }

    // Remove a user from the users.txt file
    public void removeUser(int id) {
        for (User user : userList) {
            if (user.getId() == id) {
                userList.remove(user);
                break;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int userId = Integer.parseInt(parts[0]);
                if (userId != id) {
                    sb.append(line).append("\n");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            System.out.println("Failed to remove user from file: " + e.getMessage());
        }
    }

    // Get a user by id
    public User getUserById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int userId = Integer.parseInt(parts[0]);
                if (userId == id) {
                    String name = parts[1];
                    String email = parts[2];
                    String username = parts[3];
                    String password = parts[4];
                    return new User(id, name, email, username, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all users
    /**
     * Retrieves all users from the users file
     * @return A list of all users in the users file
     */
        public List<User> getAllUsers() {
            List<User> users = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    User user = new User(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4], parts[5]);
                    users.add(user);
                }
                return users;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    /**
     * Updates a user's information in the users file
     * @param user The updated user information
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[3].equals(user.getUsername())) {
                    users.add(user);
                } else {
                    users.add(new User(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (FileWriter fileWriter = new FileWriter("users.txt")) {
            for (User u : users) {
                String userString = u.getId() + "," + u.getName() + "," + u.getEmail() + "," + u.getUsername() + "," + u.getPassword() + "," + u.getUserRole() + "\n";
                fileWriter.write(userString);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getUserRole(String username) {
        String userRole = null;
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] user = line.split(",");
                if (user[3].equals(username)) {
                    userRole = user[5];
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return userRole;
    }
}
