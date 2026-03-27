import java.io.*;
import java.util.*;

public class Stock {
    private String itemName;
    private String itemID;
    private int quantity;
    private double price;
    private int usageCount;  // Tracks how much of the item has been used

    public Stock(String itemName, String itemID, int quantity, double price) {
        this.itemName = itemName;
        this.itemID = itemID;
        this.quantity = quantity;
        this.price = price;
        this.usageCount = 0;  // Initially no usage
    }

    // Getters and setters
    public String getItemName() {
        return itemName;
    }

    public String getItemID() {
        return itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getUsageCount() {
        return usageCount;
    }

    // Update the quantity and usage count
    public void purchaseItem(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            this.usageCount += quantity;
        } else {
            System.out.println("Not enough stock to purchase the specified quantity.");
        }
    }

    // Method to update stock quantity
    public void updateQuantity(int quantity) {
        // You can add logic here to either add or subtract from the current stock
        if (quantity > 0) {
            this.quantity += quantity;
            System.out.println(quantity + " items added to stock.");
        } else if (quantity < 0 && this.quantity >= Math.abs(quantity)) {
            this.quantity += quantity;  // Subtracting quantity
            System.out.println(Math.abs(quantity) + " items removed from stock.");
        } else {
            System.out.println("Cannot update stock. Insufficient quantity to remove.");
        }
    }

    // Display stock details
    public void displayStock() {
        System.out.printf("Item: %s | ID: %s | Quantity: %d | Price: $%.2f | Usage: %d%n", 
                          itemName, itemID, quantity, price, usageCount);
    }
}
