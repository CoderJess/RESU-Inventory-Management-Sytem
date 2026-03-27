import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;

// Main class to run the inventory management system
public class InventoryManagementSystemGUI {
    private JFrame stockFrame;
    private JTable stockTable;
    private Customer customer;
    private JFrame loginFrame;
    private StockInventory stockInventory;
    private CustomerDatabase customerDatabase;
    private SupplierDatabase supplierDatabase;

    public InventoryManagementSystemGUI() {
        stockInventory = new StockInventory();
        customerDatabase = new CustomerDatabase();
        supplierDatabase = new SupplierDatabase();

        // Show the splash screen before the login screen
        showSplashScreen();
    }

    private void showSplashScreen() {
        JWindow splashScreen = new JWindow();
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        JLabel titleLabel = new JLabel("RESU Inventory Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(0, 50, 400, 20);

        // Load the image (make sure the path is correct)
        ImageIcon splashImage = new ImageIcon("uwi.png"); // Change to your image path
        JLabel imageLabel = new JLabel(splashImage);
        imageLabel.setBounds(50, 80, splashImage.getIconWidth(), splashImage.getIconHeight());

        JLabel splashLabel = new JLabel("Loading...", JLabel.CENTER);
        splashLabel.setFont(new Font("Arial", Font.BOLD, 16));
        splashLabel.setForeground(Color.BLACK);
        splashLabel.setBounds(10, 200, 400, 120);

        layeredPane.add(imageLabel, Integer.valueOf(0));
        layeredPane.add(titleLabel, Integer.valueOf(1));
        layeredPane.add(splashLabel, Integer.valueOf(1));

        splashScreen.getContentPane().add(layeredPane);
        splashScreen.pack();
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            splashScreen.dispose();
            showLoginScreen();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showLoginScreen() {
        loginFrame = new JFrame("Login - RESU Inventory Management System");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 250);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Set button colors
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusable(false);

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel("")); // Spacer
        loginPanel.add(loginButton);

        JLabel welcomeLabel = new JLabel("Welcome to RESU Inventory Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.BLACK);

        loginFrame.add(welcomeLabel, BorderLayout.NORTH);
        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Check login credentials from file
            String realName = authenticateUser(username, password);
            if (realName != null) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful! Welcome, " + realName + ".");
                loginFrame.dispose();
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials. Please try again.");
            }
        });

        loginFrame.setVisible(true);
    }

    private String authenticateUser(String username, String password) {
        try {
            File file = new File("ResuStaffUsers.txt");  // File where usernames and passwords are stored
            if (!file.exists()) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                String storedUsername = credentials[0].trim();
                String storedPassword = credentials[3].trim();
                String storedName = credentials[1].trim();  // Assuming the second column is the name

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    reader.close();
                    return storedName;  // Return the real name of the user
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading user data: " + e.getMessage());
        }
        return null;  // If no match is found
    }

    private void showMainMenu() {
        JFrame mainMenuFrame = new JFrame("Main Menu - RESU Inventory Management System");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setSize(500, 350);
        mainMenuFrame.setLocationRelativeTo(null);
        mainMenuFrame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));  // Increased row count to 4
        JButton stockButton = new JButton("Manage Stock Inventory");
        JButton customerButton = new JButton("Manage Customer Database");
        JButton supplierButton = new JButton("Manage Supplier Database");
        JButton analyticsButton = new JButton("View Inventory Usage Analytics");

        // Set button colors
        stockButton.setBackground(new Color(0, 123, 255));
        customerButton.setBackground(new Color(0, 123, 255));
        supplierButton.setBackground(new Color(0, 123, 255));
        analyticsButton.setBackground(new Color(0, 123, 255));

        stockButton.setForeground(Color.WHITE);
        customerButton.setForeground(Color.WHITE);
        supplierButton.setForeground(Color.WHITE);
        analyticsButton.setForeground(Color.WHITE);

        buttonPanel.add(stockButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(supplierButton);
        buttonPanel.add(analyticsButton);  // Add the new button

        mainMenuFrame.add(new JLabel("Select an Option:", JLabel.CENTER), BorderLayout.NORTH);
        mainMenuFrame.add(buttonPanel, BorderLayout.CENTER);

        stockButton.addActionListener(e -> showStockInventoryWindow());
        customerButton.addActionListener(e -> showCustomerDatabaseWindow());
        supplierButton.addActionListener(e -> showSupplierDatabaseWindow());
        analyticsButton.addActionListener(e -> showAnalyticsWindow());

        mainMenuFrame.setVisible(true);
    }

    // Show Stock Inventory Window
    private void showStockInventoryWindow() {
        stockFrame = createFrame("Stock Inventory Management", 800, 600);
        stockTable = createTable(new Object[]{"Item Name", "Quantity", "Price"});
        JPanel inputPanel = createStockInputPanel(stockTable, stockFrame);
        populateTableFromFile(stockTable);

        stockFrame.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        stockFrame.add(inputPanel, BorderLayout.NORTH);
        stockFrame.setVisible(true);
    }

    private JFrame createFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background
        return frame;
    }

    private JTable createTable(Object[] columnNames) {
        return new JTable(new DefaultTableModel(columnNames, 0));
    }

    private JPanel createStockInputPanel(JTable stockTable, JFrame stockFrame) {
        JPanel containerJPanel = new JPanel(new GridLayout(2, 2,10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField itemNameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JButton addButton = new JButton("Add Stock");
        JButton updateButton = new JButton("Update Stock");
        JButton calculateButton = new JButton("Calculate Value");
        JButton searchButton =  new JButton("Search for Inventory Items");
        JButton outOfStockButton = new JButton("Show Out of Stock Items");
        JButton returnButton = new JButton("Return");

        // Set button styles
        addButton.setBackground(new Color(0, 123, 255));
        updateButton.setBackground(new Color(0, 123, 255));
        calculateButton.setBackground(new Color(0, 123, 255));
        searchButton.setBackground(new Color(0, 123, 255));
        outOfStockButton.setBackground(new Color(0, 123, 255));
        returnButton.setBackground(new Color(0, 123, 255));

        addButton.setForeground(Color.WHITE);
        updateButton.setForeground(Color.WHITE);
        calculateButton.setForeground(Color.WHITE);
        searchButton.setForeground(Color.WHITE);
        outOfStockButton.setForeground(Color.WHITE);
        returnButton.setForeground(Color.WHITE);

        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 0, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(outOfStockButton);
        buttonPanel.add(returnButton);

        containerJPanel.add(inputPanel);
        containerJPanel.add(buttonPanel);
        //containerJPanel.add(Box.createVerticalGlue());

        outOfStockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayOutOfStockItems();
            }
        });

        returnButton.addActionListener(e ->{
            stockFrame.setVisible(false);
        });

        addButton.addActionListener(e -> {
            String itemName = itemNameField.getText();
            String staffId = ""; // Assuming staffId is managed elsewhere
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
        
                // Add stock to inventory
                stockInventory.addStock(itemName, staffId, quantity, price);
        
                // Add stock data to the table
                DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                model.addRow(new Object[]{itemName, quantity, price});
        
                // Append stock data to a text file
                try (FileWriter writer = new FileWriter("stock_inventory.txt", true)) {
                    writer.write(itemName + "," + quantity + "," + price + "\n");
                } catch (IOException ioEx) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Error writing to file: " + ioEx.getMessage());
                }
        
                // Show success message
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Stock added successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Invalid quantity or price. Please enter valid numbers.");
            }
        });

        updateButton.addActionListener(e -> {
            String[] options = {"Add New Stock", "Delete Existing Stock"};
            int choice = JOptionPane.showOptionDialog(stockFrame,
                    "Would you like to add a new stock item or delete an existing one?",
                    "Update Stock",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
        
            if (choice == 0) {
                // User chose to add new stock
                addNewStock();
            } else if (choice == 1) {
                // User chose to delete existing stock
                deleteExistingStock();
            }
        });

        calculateButton.addActionListener(e -> {
            double totalValue = stockInventory.calculateInventoryValue();
            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Total Inventory Value: $" + totalValue);
        });

        searchButton.addActionListener(e -> {
            showSearchWindow();
        });

        return containerJPanel;
    }

    private void addNewStock() {
        JTextField itemNameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
    
        Object[] message = {
            "Item Name:", itemNameField,
            "Quantity:", quantityField,
            "Price:", priceField
        };
    
        int option = JOptionPane.showConfirmDialog(stockFrame, message, "Add New Stock", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            int quantity;
            double price;
            
            try {
                quantity = Integer.parseInt(quantityField.getText());
                price = Double.parseDouble(priceField.getText());
    
                if (itemExists(itemName)) {
                    JOptionPane.showMessageDialog(stockFrame, "Item already exists.");
                } else {
                    // Add the new stock to the inventory
                    stockInventory.addStock(itemName, "", quantity, price);
                    appendToFile(itemName, quantity, price);
                    
                    // Update the table using the instance variable
                    DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                    model.addRow(new Object[]{itemName, quantity, price});
                    JOptionPane.showMessageDialog(stockFrame, "Stock added successfully.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(stockFrame, "Invalid quantity or price. Please enter valid numbers.");
            }
        }
    }

    private void deleteExistingStock() {
        String itemName = JOptionPane.showInputDialog(stockFrame, "Enter Item Name to Delete:");
        if (itemName != null && !itemName.trim().isEmpty()) {
            if (removeFromFile(itemName)) {
                // Remove from JTable
                DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    // Cast to String before using equalsIgnoreCase
                    String tableItemName = (String) model.getValueAt(i, 0);
                    if (tableItemName.equalsIgnoreCase(itemName)) {
                        model.removeRow(i);
                        JOptionPane.showMessageDialog(stockFrame, "Item deleted successfully.");
                        return; // Exit after deletion
                    }
                }
                // If we reach here, the item was not found in the table
                JOptionPane.showMessageDialog(stockFrame, "Item does not exist in the inventory.");
            } else {
                JOptionPane.showMessageDialog(stockFrame, "Item does not exist."); // This should trigger if the item isn't found in the file
            }
        }
    }

    private boolean removeFromFile(String itemName) {
        File inputFile = new File("stock_inventory.txt");
        File tempFile = new File("temp_stock_inventory.txt");
    
        boolean found = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            String line;
    
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(itemName)) {
                    found = true; // Mark as found
                    continue; // Skip writing this line to delete it
                }
                writer.println(line); // Write lines that aren't deleted
            }
    
            // Replace original file with the updated temporary file
            if (inputFile.delete()) {
                tempFile.renameTo(inputFile);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(stockFrame, "Error modifying file: " + e.getMessage());
        }
    
        return found; // Return whether the item was found and deleted
    }
    
    private boolean itemExists(String itemName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("stock_inventory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(itemName)) {
                    return true; // Item exists
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(stockFrame, "Error reading from file: " + e.getMessage());
        }
        return false; // Item does not exist
    }
    
    private void appendToFile(String itemName, int quantity, double price) {
        try (FileWriter writer = new FileWriter("stock_inventory.txt", true)) {
            writer.write(itemName + "," + quantity + "," + price + "\n");
        } catch (IOException ioEx) {
            JOptionPane.showMessageDialog(stockFrame, "Error writing to file: " + ioEx.getMessage());
        }
    }

    private void populateTableFromFile(JTable stockTable) {
        try (BufferedReader reader = new BufferedReader(new FileReader("stock_inventory.txt"))) {
            String line;
            DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // Assuming CSV format
                if (data.length == 3) {
                    String itemName = data[0];
                    int quantity = Integer.parseInt(data[1]);
                    double price = Double.parseDouble(data[2]);
                    model.addRow(new Object[]{itemName, quantity, price});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error parsing data from file: " + e.getMessage());
        }
    }

    private void displayOutOfStockItems() {
        JFrame outOfStockFrame = createFrame("Out of Stock Items", 400, 400);
        JPanel outOfStockPanel = new JPanel();
        outOfStockPanel.setLayout(new BoxLayout(outOfStockPanel, BoxLayout.Y_AXIS));  // For vertical layout
    
        // Retrieve out-of-stock items
        ArrayList<Stock> outOfStockItems = stockInventory.getOutOfStockItems();
    
        // Check if there are any out-of-stock items
        if (outOfStockItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No items are out of stock.");
            outOfStockPanel.add(noItemsLabel);
        } else {
            // Iterate through the out-of-stock items and display them
            for (Stock outOfStockItem : outOfStockItems) {
                if (outOfStockItem.getQuantity() == 0) {  // Ensure it's out of stock (quantity = 0)
                    // Create a label for each out-of-stock item and add to the panel
                    JLabel itemLabel = new JLabel("Item: " + outOfStockItem.getItemName() + " | Quantity: " + outOfStockItem.getQuantity());
                    outOfStockPanel.add(itemLabel);
                }
            }
        }
    
        // Add the panel to the frame and make it visible
        outOfStockFrame.add(outOfStockPanel);
        outOfStockFrame.setVisible(true);
    }

    private void showSearchWindow(){
        // Search Frame
        JFrame searchFrame = createFrame("Search for Inventory Items", 500, 300);
        searchFrame.setLayout(new GridLayout(2, 2));

        // Text fields for input
        JTextField searchByItemName = new JTextField();

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Adding components to the input panel
        JLabel label = new JLabel("Enter the name of the item you want to search for:");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(label);
        inputPanel.add(searchByItemName);

        // Buttons for actions
        JButton performSearch = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        performSearch.setBackground(new Color(255, 123, 0));
        cancelButton.setBackground(new Color(255, 123, 0));
        
        performSearch.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String itemName = searchByItemName.getText();
                
                displaySearchResults(itemName);
                searchFrame.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchFrame.dispose(); // Close the search window
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(performSearch);
        buttonPanel.add(cancelButton);
    
        // Add input panel and button panel to the frame
        searchFrame.add(inputPanel);
        searchFrame.add(buttonPanel);
        
        // Set the frame to be visible
        searchFrame.setVisible(true);
    }

    private void displaySearchResults(String itemName) {

        // Create a new dialog to display results
        JDialog resultsDialog = new JDialog();
        resultsDialog.setTitle("Search Results");
        resultsDialog.setSize(400, 300);
        resultsDialog.setLocationRelativeTo(null); // Center the dialog
        
        // Create a panel for the results with a border
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(new TitledBorder("Item Details")); // Add title to the border
        
        // Add vertical glue to center components
        resultsPanel.add(Box.createVerticalGlue());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        try{
            String iName = stockInventory.findStockByName(itemName).getItemName();
            int iQuantity = stockInventory.findStockByName(itemName).getQuantity();
            double iPrice = stockInventory.findStockByName(itemName).getPrice();

            centerPanel.add(new JLabel("Item Name: " + iName,  SwingConstants.CENTER));
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space
            centerPanel.add(new JLabel("Quantity: " + iQuantity,  SwingConstants.CENTER));
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space
            centerPanel.add(new JLabel("Price: $" + String.format("%.2f", iPrice), SwingConstants.CENTER));
        }catch(Exception e){
            JOptionPane.showMessageDialog(centerPanel.getTopLevelAncestor(), "Item not found.");
        }

        resultsPanel.add(centerPanel);

        // Add vertical glue again to push the button down
        resultsPanel.add(Box.createVerticalGlue());

        // Create button panel for the close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align button to the right
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsDialog.dispose(); // Close the results dialog
            }
        });

        buttonPanel.add(closeButton);

        // Add components to the dialog
        resultsDialog.add(resultsPanel, BorderLayout.CENTER); // Add results panel to the center
        resultsDialog.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom

        // Show the dialog
        resultsDialog.setVisible(true);
    }

    // Show Customer Database Window
    private void showCustomerDatabaseWindow() {
        JFrame customerFrame = createFrame("Customer Database Management", 800, 600);
        JTable customerTable = createTable(new Object[]{"Name", "Contact Info", "Address"});
        JPanel inputPanel = createCustomerInputPanel(customerTable);

        customerFrame.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        customerFrame.add(inputPanel, BorderLayout.NORTH);
        customerFrame.setVisible(true);
    }

    private JPanel createCustomerInputPanel(JTable customerTable) {
        JPanel inputPanel = new JPanel(new GridLayout(9, 7, 10, 10));
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField orderNumberField = new JTextField();
        JButton addButton = new JButton("Add Customer");
        JButton searchByNameButton = new JButton("Search by Name");
        JButton searchByOrderButton = new JButton("Search by Order Number");
        JButton updateButton = new JButton("Update");
        JButton returnButton = new JButton("Return");
        JButton payButton = new JButton("Pay");
        JButton orderButton = new JButton("Add Order");

        addButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        searchByNameButton.setBackground(new Color(0, 123, 255));
        searchByNameButton.setForeground(Color.WHITE);
        searchByOrderButton.setBackground(new Color(0, 123, 255)); 
        searchByOrderButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(0, 123, 255));
        updateButton.setForeground(Color.WHITE);
        returnButton.setBackground(new Color(0, 123, 255));
        returnButton.setForeground(Color.WHITE);
        payButton.setBackground(new Color(0, 123, 255));
        payButton.setForeground(Color.WHITE);
        orderButton.setBackground(new Color(0, 123, 255));
        orderButton.setForeground(Color.WHITE);
    

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Contact Info:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Order Number:")); 
        inputPanel.add(orderNumberField);
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(addButton);
        //inputPanel.add(new JLabel("Search by name or order number:"));
        inputPanel.add(searchByNameButton);
        inputPanel.add(searchByOrderButton);
        inputPanel.add(updateButton);
        inputPanel.add(returnButton);
        inputPanel.add(payButton);
        inputPanel.add(orderButton);



        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String contactInfo = contactField.getText();
            String address = addressField.getText();

            if (name.isEmpty() || contactInfo.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "All fields must be filled out.");
            } else {
                customerDatabase.addCustomer(name, contactInfo, address);

                DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
                model.addRow(new Object[]{name, contactInfo, address, "View"});

                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer added successfully.");
            }
            });

            searchByNameButton.addActionListener(e -> {
                String searchQuery = nameField.getText();
                Customer customer = customerDatabase.searchCustomerByName(searchQuery);
        
                if (customer != null) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer found: " + customer.getName());
                } else {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer not found.");
                }
            });
        
            // Search by Order Number ActionListener (NEW)
            searchByOrderButton.addActionListener(e -> {
                String orderNumber = orderNumberField.getText();
                if (orderNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter an order number.");
                } else {
                    Customer customer = customerDatabase.searchCustomerByOrder(orderNumber);  // Assuming the method exists
        
                    if (customer != null) {
                        JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer found: " + customer.getName() + " with Order Number: " + orderNumber);
                    } else {
                        JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Order Number not found.");
                    }
                }
            });

        updateButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                
                String name = (String) customerTable.getValueAt(selectedRow, 0);
                String contactInfo = (String) customerTable.getValueAt(selectedRow, 1);
                String address = (String) customerTable.getValueAt(selectedRow, 2);
        
                
                String updatedContactInfo = JOptionPane.showInputDialog("Enter new contact info:", contactInfo);
                String updatedAddress = JOptionPane.showInputDialog("Enter new address:", address);
        
                
                if (updatedContactInfo != null && updatedAddress != null && !updatedContactInfo.isEmpty() && !updatedAddress.isEmpty()) {
                    customerDatabase.updateCustomer(name, contactInfo, updatedContactInfo, updatedAddress);
        
                    DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
                    model.setValueAt(updatedContactInfo, selectedRow, 1);
                    model.setValueAt(updatedAddress, selectedRow, 2);
        
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer updated.");
                } else {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please provide valid contact info and address.");
                }
            } else {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please select a customer to edit.");
            }
        });

        payButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                String name = (String) customerTable.getValueAt(selectedRow, 0);
    
                String amountStr = JOptionPane.showInputDialog("Enter payment amount:");
                String method = JOptionPane.showInputDialog("Enter payment method:");
    
                if (amountStr != null && method != null && !amountStr.isEmpty() && !method.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        Customer customer = customerDatabase.searchCustomerByName(name);
                        if (customer != null) {
                            customerDatabase.addPayment(name, amount, method);
                            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Payment added successfully.");
                        } else {
                            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer not found.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Invalid amount entered.");
                    }
                } else {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "All fields must be filled out.");
                }
            } else {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please select a customer to make payment.");
            }
        });

        orderButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                String name = (String) customerTable.getValueAt(selectedRow, 0);
    
                String item = JOptionPane.showInputDialog("Enter item:");
                String quantityStr = JOptionPane.showInputDialog("Enter quantity:");
    
                if (item != null && quantityStr != null && !item.isEmpty() && !quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        Customer customer = customerDatabase.searchCustomerByName(name);
                        if (customer != null) {
                            customerDatabase.addOrderToCustomer(name, item, quantity);
                            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Order added successfully.");
                        } else {
                            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Customer not found.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Invalid quantity entered.");
                    }
                } else {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "All fields must be filled out.");
                }
            } else {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please select a customer to add order.");
            }
            //customer.viewPurchaseHistory();
        });

    returnButton.addActionListener(e -> {
        JFrame topFrame = (JFrame) inputPanel.getTopLevelAncestor();
        topFrame.dispose();
    });

        return inputPanel;
    }

    private DefaultTableModel model; 
    private JTable supplierTable;   

    private void showSupplierDatabaseWindow() {
        // Create JFrame for Supplier Database
        JFrame supplierFrame = createFrame("Supplier Database Management", 800, 600);
        
        // Check if JTable is already created to avoid re-creating it each time
        if (supplierTable == null) {
            model = new DefaultTableModel(new Object[]{"Name", "Contact Information", "Address", "Supplied Item", "Expenditure", "Transaction Date"}, 0);
            supplierTable = new JTable(model);
        }
        
        JPanel inputPanel = createSupplierInputPanel(supplierTable);
        SupplierDatabase supplierDatabase = new SupplierDatabase();
        supplierDatabase.loadFromFile(); // Load suppliers from file
        
        // Load existing suppliers into the table
        for (Supplier supplier : supplierDatabase.getSuppliers()) {
            model.addRow(new Object[]{supplier.getName(), supplier.getContactInfo(), supplier.getAddress(), supplier.getSuppliedItem(), supplier.getTotalExpenditures(), supplier.getTransactionDate()});
        }
    
        supplierFrame.add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        supplierFrame.add(inputPanel, BorderLayout.NORTH);
        supplierFrame.setVisible(true);
    }

    private JPanel createSupplierInputPanel(JTable supplierTable) {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField suppliedItemField = new JTextField();
        JTextField expenditureField = new JTextField();
        JTextField transactionDateField = new JTextField();
        JButton addButton = new JButton("Add Supplier");
        JButton updateButton = new JButton("Update Supplier");
        JButton returnButton = new JButton("Return");

        addButton.setBackground(new Color(0, 123, 255));
        updateButton.setBackground(new Color(0, 123, 255));
        returnButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        updateButton.setForeground(Color.WHITE);
        returnButton.setForeground(Color.WHITE);


        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Contact Information:"));
        fieldsPanel.add(contactField);
        fieldsPanel.add(new JLabel("Address:"));
        fieldsPanel.add(addressField);
        fieldsPanel.add(new JLabel("Supplied Item:"));
        fieldsPanel.add(suppliedItemField);
        fieldsPanel.add(new JLabel("Expenditure:"));
        fieldsPanel.add(expenditureField);
        fieldsPanel.add(new JLabel("Transaction Date:"));
        fieldsPanel.add(transactionDateField);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(returnButton);

        inputPanel.add(fieldsPanel, BorderLayout.CENTER);
        inputPanel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String contactInfo = contactField.getText();
            String address = addressField.getText(); 
            String suppliedItem = suppliedItemField.getText();
            String transactionDate = transactionDateField.getText();
            double expenditureAmount;



            //Ensure there is an input
            if (name.isEmpty() || contactInfo.isEmpty() || address.isEmpty() || suppliedItem.isEmpty() || transactionDate.isEmpty()) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "All fields must be filled out.");
                return; 
            }


            // Validate contact format
            if (!contactInfo.matches("\\d{3}-\\d{3}-\\d{4}")) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid contact number xxx-xxx-xxxx.");
                return;
            }


              // Validate the expenditure input
            
            try {
                expenditureAmount = Double.parseDouble(expenditureField.getText());
                if (expenditureAmount <= 0) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid positive amount.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid expenditure amount.");
                return;
            }
            

            // Validate date format
            if (!Supplier.isValidDate(transactionDate)) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid date yyyy/mm/dd.");
                return;
            }


            //Prevent duplicating
            ArrayList<Supplier> existingSuppliers = supplierDatabase.searchSupplier(name);
            boolean isDuplicate = false;
            for (Supplier supplier : existingSuppliers) {
                if (supplier.getContactInfo().equalsIgnoreCase(contactInfo)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Supplier already exists.");
                return;
            }

            supplierDatabase.addSupplier(name, contactInfo, address, suppliedItem, expenditureAmount, transactionDate);

            // Format expenditure for display
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            String formattedExpenditure = currencyFormat.format(expenditureAmount);

            DefaultTableModel model = (DefaultTableModel) supplierTable.getModel();
            model.addRow(new Object[]{name, contactInfo, address, suppliedItem, formattedExpenditure, transactionDate});

            supplierDatabase.saveToFile();

            // Clear the text fields
            nameField.setText("");
            contactField.setText("");
            addressField.setText("");
            suppliedItemField.setText("");
            expenditureField.setText("");
            transactionDateField.setText("");
            JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Supplier added successfully.");
        });

         //Close Supplier Management window
        returnButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) inputPanel.getTopLevelAncestor();
            topFrame.setVisible(false); 
        });



        updateButton.addActionListener(e -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow >= 0) {
                String name = (String) supplierTable.getValueAt(selectedRow, 0);
                String currentContactInfo = (String) supplierTable.getValueAt(selectedRow, 1);
        
                // Prompt for updated details
                String updatedContactInfo = JOptionPane.showInputDialog("Enter new contact information:", currentContactInfo);
                //Validate contact Info
                if (updatedContactInfo != null && !updatedContactInfo.matches("\\d{3}-\\d{3}-\\d{4}")) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid contact number xxx-xxx-xxxx.");
                    return;
                }
        
                String updatedAddress = JOptionPane.showInputDialog("Enter new address:", supplierTable.getValueAt(selectedRow, 2));
                String updatedSuppliedItem = JOptionPane.showInputDialog("Enter updated supplied item:", supplierTable.getValueAt(selectedRow, 3));
        
                //Validate Date format
                String transactionDate = JOptionPane.showInputDialog("Enter new transaction date (yyyy/mm/dd):", supplierTable.getValueAt(selectedRow, 5));
                if (!Supplier.isValidDate(transactionDate)) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid date yyyy/mm/dd.");
                    return;
                }
        
                // Validate the  updated expenditure input
                String expenditureInput = JOptionPane.showInputDialog("Enter updated expenditure amount:", supplierTable.getValueAt(selectedRow, 4));
                double updatedExpenditure = 0.0;
                try {
                    updatedExpenditure = Double.parseDouble(expenditureInput);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please enter a valid expenditure amount.");
                    return;
                }

        
                // Update supplier details
                supplierDatabase.updateSupplier(name, currentContactInfo, updatedContactInfo, updatedAddress, updatedSuppliedItem, transactionDate, updatedExpenditure);
        
                // Update the table
                DefaultTableModel model = (DefaultTableModel) supplierTable.getModel();
                model.setValueAt(updatedContactInfo, selectedRow, 1);
                model.setValueAt(updatedAddress, selectedRow, 2);
                model.setValueAt(updatedSuppliedItem, selectedRow, 3);
                model.setValueAt(NumberFormat.getCurrencyInstance().format(updatedExpenditure), selectedRow, 4);
                model.setValueAt(transactionDate, selectedRow, 5);
        
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Supplier updated successfully.");
            } else {
                JOptionPane.showMessageDialog(inputPanel.getTopLevelAncestor(), "Please select a supplier to edit.");
            }
        });
        return inputPanel;
    }

    // Show Analytics Window
    public void showAnalyticsWindow() {
        InventoryUsageAnalytics usageAnalytics = new InventoryUsageAnalytics(stockInventory, customerDatabase);
        JFrame frame = new JFrame("Inventory Usage Analytics");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    
        // Panel to display the report or analytics output
        JTextArea reportArea = new JTextArea(10, 40);
        reportArea.setEditable(false);
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
    
        // Panel to hold buttons (moved to the bottom)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));  // 3 buttons in a single row with spacing
    
        // Buttons to trigger different actions
        JButton generateReportButton = new JButton("Generate Report");
        JButton exportReportButton = new JButton("Export Report");
        JButton showDashboardButton = new JButton("Show Dashboard");
    
        // Set background and text color for the buttons
        Color blueColor = new Color(0, 122, 255);  // You can use any blue shade you prefer
        Color whiteColor = Color.WHITE;
    
        generateReportButton.setBackground(blueColor);
        exportReportButton.setBackground(blueColor);
        showDashboardButton.setBackground(blueColor);
    
        // Add buttons to the panel
        buttonPanel.add(generateReportButton);
        buttonPanel.add(exportReportButton);
        buttonPanel.add(showDashboardButton);
    
        // Action for generating report
        generateReportButton.addActionListener(e -> {
            String report = usageAnalytics.generateReport("weekly");
            reportArea.setText(report);
        });
    
        // Action for exporting report
        exportReportButton.addActionListener(e -> {
            String reportContent = usageAnalytics.generateReport("monthly");
            boolean success = usageAnalytics.exportReport(reportContent, "monthly");
            if (success) {
                reportArea.setText("Report exported successfully.");
            } else {
                reportArea.setText("Failed to export report.");
            }
        });
    
        // Action for showing the report in the dashboard
        showDashboardButton.addActionListener(e -> {
            String reportContent = usageAnalytics.generateReport("yearly");
            Map<String, Integer> usageData = new HashMap<>();
              // Sample data for visualization
            customer.getPurchaseHistory();
            
            usageData.put("Item A", 10);
            usageData.put("Item B", 20);
            usageAnalytics.showReportInDashboard(reportContent, usageData);
        });
    
        // Add components to the frame
        frame.add(reportScrollPane, BorderLayout.CENTER);  // Place report area in the center
        frame.add(buttonPanel, BorderLayout.SOUTH);  // Move button panel to the bottom
    
        // Make the frame visible
        frame.setLocationRelativeTo(null);  // Center the frame on the screen
        frame.setVisible(true);
    }
    /*private void showAnalyticsWindow() {
        JFrame analyticsFrame = createFrame("Inventory Usage Analytics", 800, 600);
        JPanel panel = new JPanel();
        JButton generateReportButton = new JButton("Generate Report");
        JTextArea reportArea = new JTextArea(10, 30);

        panel.add(generateReportButton);
        panel.add(new JScrollPane(reportArea));

        generateReportButton.addActionListener(e -> {
            // Generate and display a sample report
            reportArea.setText("Sample Inventory Usage Report:\n\n");
            reportArea.append("Stock Item 1: 200 sold\n");
            reportArea.append("Stock Item 2: 150 sold\n");
            // You can replace this with actual data generation
        });

        analyticsFrame.add(panel, BorderLayout.CENTER);
        analyticsFrame.setVisible(true);
    }*/

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementSystemGUI());
    }
}
