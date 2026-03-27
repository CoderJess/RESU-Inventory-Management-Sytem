import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class StockInventory {
    private List<Stock> inventory;
    private List<InventoryChange> inventoryChanges;
    private ArrayList<Stock> outOfStockItems;  // For tracking out-of-stock items

    public StockInventory() {
        this.inventory = new ArrayList<>();
        this.inventoryChanges = new ArrayList<>();
        this.outOfStockItems = new ArrayList<>();
    }

    public void addStock(String itemName, String staffId, int quantity, double price) {
        if (quantity < 0) {
            JOptionPane.showMessageDialog(null, "Cannot add negative stock!", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return; // Exit if the quantity is invalid
        }
    
        Stock stock = findStockByName(itemName);
        if (stock != null) {
            // Item exists, update its quantity
            stock.updateQuantity(stock.getQuantity() + quantity);
            markOutOfStock(stock); // Check if it's out of stock
        } else {
            // Item doesn't exist, create a new Stock and add it to the inventory
            Stock newStock = new Stock(itemName, staffId, quantity, price);
            inventory.add(newStock);
            checkStockLevel(newStock);  // Check stock level for the new stock
            markOutOfStock(newStock); // Check if it's out of stock
        }
    }

    public void updateStock(String itemName, int newQuantity, String staffId, String reasonForChange, double salesAdjustment) {
        for (Stock stock : inventory) {
            if (stock.getItemName().equalsIgnoreCase(itemName)) {
                int previousQuantity = stock.getQuantity();
                stock.updateQuantity(newQuantity);
    
                // Record the inventory change
                InventoryChange change = new InventoryChange(staffId, reasonForChange, previousQuantity, newQuantity, salesAdjustment);
                inventoryChanges.add(change);
    
                // After updating stock, check if it's out of stock
                markOutOfStock(stock);
    
                System.out.println("Updated quantity for: " + itemName);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Item not found: " + itemName, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Add stock to inventory
    /*public void addStock(String itemName, String staffId, int quantity, double price) {
        if (quantity < 0) {
            JOptionPane.showMessageDialog(null, "Cannot add negative stock!", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return; // Exit if the quantity is invalid
        }

        Stock stock = findStockByName(itemName);
        if (stock != null) {
            // Item exists, update its quantity
            stock.updateQuantity(stock.getQuantity() + quantity);
            markOutOfStock(stock);
            //checkStockLevel(stock);  // Check stock level after adding more  
        } else {
            // Item doesn't exist, create a new Stock and add it to the inventory
            Stock newStock = new Stock(itemName, staffId, quantity, price);
            inventory.add(newStock);
            checkStockLevel(newStock);  // Check stock level for the new stock
        }
    }*/

    // Update stock quantity and track change
    /*public void updateStock(String itemName, int newQuantity, String staffId, String reasonForChange, double salesAdjustment) {
        for (Stock stock : inventory) {
            if (stock.getItemName().equalsIgnoreCase(itemName)) {
                int previousQuantity = stock.getQuantity();
                stock.updateQuantity(newQuantity);

                // Record the inventory change
                InventoryChange change = new InventoryChange(staffId, reasonForChange, previousQuantity, newQuantity, salesAdjustment);
                inventoryChanges.add(change);

                System.out.println("Updated quantity for: " + itemName);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Item not found: " + itemName, "Error", JOptionPane.ERROR_MESSAGE);
    }*/

    // Remove stock item from inventory and track the change
    public void removeStock(String itemName, String staffId, String reasonForChange) {
        for (Stock stock : inventory) {
            if (stock.getItemName().equalsIgnoreCase(itemName)) {
                // Record the inventory change before removing
                InventoryChange change = new InventoryChange(staffId, reasonForChange, stock.getQuantity(), 0, 0.0);
                inventoryChanges.add(change);

                // Remove the stock item from the inventory
                inventory.remove(stock);
                System.out.println("Stock removed: " + itemName);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Item not found: " + itemName, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Check stock level and notify if stock is below 10
    private void checkStockLevel(Stock stock) {
        if (stock.getQuantity() < 10) {
            JOptionPane.showMessageDialog(null, "Warning: " + stock.getItemName() + " stock is below 10!", "Low Stock Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void markOutOfStock(Stock stock) {
        if (stock != null && stock.getQuantity() == 0 && !outOfStockItems.contains(stock)) {
            outOfStockItems.add(stock);
            JOptionPane.showMessageDialog(null, stock.getItemName() + " is now out of stock! Please restock as soon as possible.", 
                                          "Out of Stock", JOptionPane.ERROR_MESSAGE);
        }
    }

     /*public void markOutOfStock(Stock stockName) {
        //Stock stock = findStockByName(stockName);
       
        if (stockName != null && stockName.getQuantity() == 0 && !outOfStockItems.contains(stockName)) {
            outOfStockItems.add(stockName);
            JOptionPane.showMessageDialog(null, stockName + " is now out of stock! Please restock as soon as possible.", 
                                          "Out of Stock", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    // Mark a stock as out of stock
    /*public void markOutOfStock(String stockName) {
        Stock stock = findStockByName(stockName);
        if (stock != null && !outOfStockItems.contains(stockName)) {
            outOfStockItems.add(stockName);
            JOptionPane.showMessageDialog(null, stockName + " is now out of stock! Time to restock.", "Out of Stock", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    // Display the current stock inventory
    public void displayInventory() {
        System.out.println("\n--- Current Inventory ---");
        if (inventory.isEmpty()) {
            System.out.println("No stocks in the inventory.");
        } else {
            for (Stock stock : inventory) {
                stock.displayStock();  // Assuming Stock has a method to display itself
            }
        }
    }

    // Export the inventory report to a file
    public void exportReport(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Inventory Report");
            writer.println("================");
            for (Stock stock : inventory) {
                writer.printf("Stock: %s | Quantity: %d | Price: $%.2f%n",
                        stock.getItemName(), stock.getQuantity(), stock.getPrice());
            }
            JOptionPane.showMessageDialog(null, "Report exported to " + fileName, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting report: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Calculate the total value of the inventory
    public double calculateInventoryValue() {
        double totalValue = 0.0;
        for (Stock stock : inventory) {
            totalValue += stock.getQuantity() * stock.getPrice();  // Multiply quantity by price
        }
        return totalValue;
    }

    // Load stock items from a file
    public void loadFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (line.startsWith("Item Name")) continue;

                String[] data = line.split(",");
                String itemName = data[0].trim();
                int quantity = Integer.parseInt(data[1].trim());
                double price = Double.parseDouble(data[2].trim());

                addStock(itemName, "staffId", quantity, price);  // Assuming "staffId" is static here
            }
            System.out.println("Inventory loaded from file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error loading inventory from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid data format in file: " + e.getMessage());
        }
    }

    // Generate random stock items for testing
    public void generateRandomStockItems(int numItems) {
        Random rand = new Random();
        for (int i = 0; i < numItems; i++) {
            String itemName = "Item" + (i + 1);
            int quantity = rand.nextInt(100) + 1; // Random quantity between 1 and 100
            double price = rand.nextDouble() * 100; // Random price between 0 and 100
            addStock(itemName, "staffId", quantity, price);  // Assuming "staffId" is static here
        }
        System.out.println("Generated " + numItems + " random stock items.");
    }

    // Get a list of out-of-stock items
    public ArrayList<Stock> getOutOfStockItems() {
        return outOfStockItems;
    }

    // Find a stock item by name
    public Stock findStockByName(String stockName) {
        for (Stock stock : inventory) {
            if (stock.getItemName().equalsIgnoreCase(stockName)) {
                return stock;
            }
        }
        return null;
    }

    // Get the current inventory
    public List<Stock> getInventory() {
        return inventory;
    }

    

    // Generate a report of inventory changes
    public void generateInventoryChangeReport() {
        System.out.println("\n--- Inventory Change Report ---");
        System.out.println("| Staff ID | Reason for Change | Date and Time | Previous Quantity | New Quantity | Sales Adjustment |");
        System.out.println("|----------|-------------------|---------------|-------------------|---------------|------------------|");

        for (InventoryChange change : inventoryChanges) {
            System.out.println(change);
        }
    }
}

