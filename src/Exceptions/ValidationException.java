package Exceptions;

import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class ValidationException extends Exception{
    public ValidationException(String message) {
        super(message);
    }
}
