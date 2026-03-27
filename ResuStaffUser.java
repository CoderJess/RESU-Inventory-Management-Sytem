import javax.swing.*;

class ResuStaffUser {
    private String staffID;
    private String name;
    private String role; // E.g., "Inventory Manager", "Cashier", etc.
    private String password;
    
    public ResuStaffUser(String staffID, String name, String role, String password) {
        this.staffID = staffID;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    // Getter methods
    public String getStaffID() {
        return staffID;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    // Example method to manage inventory (based on role/permissions)
    public void manageInventory(StockInventory stockInventory) {
        if (role.equals("Inventory Manager")) {
            // Allow management of inventory
            JOptionPane.showMessageDialog(null, "Access granted: Managing Inventory");
        } else {
            JOptionPane.showMessageDialog(null, "Access denied: You do not have permission to manage inventory.");
        }
    }
}
