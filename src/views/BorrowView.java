package views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controllers.BorrowController;
import models.Borrow;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import models.User;
import utils.SessionManager;

public class BorrowView extends JPanel{
    private JTable table;
    private DefaultTableModel model;

    public BorrowView(BorrowController borrowController) {
        User currentUser = (User) SessionManager.getInstance().get("currentUser"); 
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        model = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Book Name", "User Name", "Borrow Date", "Return Date", "Overdue"}){@Override
            public boolean isCellEditable(int row, int column) {
                return false; // make all cells non-editable
            }
        };
        table.setModel(model);
        scrollPane.setViewportView(table);

        displayAllBorrows(borrowController.getBorrowList());
    }

    void displayAllBorrows(List<Borrow> borrowList) {
        BorrowController borrowController = new BorrowController();
        model.setRowCount(0); // clear the table before adding new rows
        for (Borrow borrow : borrowList) {
            int borrowId = borrow.getId();
            String bookName = borrowController.getBookNameByBorrowId(borrowId);
            String userName = borrowController.getUserNameByBorrowId(borrowId);
            Date borrowDate = borrow.getBorrowDate();
            Date returnDate = borrow.getReturnDate();
            boolean overdue = new Date().after(returnDate);

            model.addRow(new Object[]{borrowId, bookName, userName, borrowDate, returnDate, overdue});
        }
    }
}