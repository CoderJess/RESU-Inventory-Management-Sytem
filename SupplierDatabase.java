import java.util.ArrayList;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SupplierDatabase {
    private ArrayList<Supplier> suppliers;
    private final String fileName = "Suppliers.txt";

    public SupplierDatabase() {
        this.suppliers = new ArrayList<>();
    }

    // Add a new supplier to the database
    public void addSupplier(String name, String contactInfo, String address, String suppliedItem, double expenditureAmount, String transactionDate) {
        if (name == null || name.isEmpty() || contactInfo == null || contactInfo.isEmpty() || address == null || address.isEmpty() 
            || suppliedItem == null || suppliedItem.isEmpty() || transactionDate == null || transactionDate.isEmpty()){
            System.out.println("Invalid supplier data.");
            return;
        }
        //Check for duplicate
        for (Supplier supplier : suppliers) {
            if (supplier.getName().equalsIgnoreCase(name) && supplier.getContactInfo().equalsIgnoreCase(contactInfo)) {
                System.out.println("Supplier is already added.");
                return; 
            }
        }
        Supplier newSupplier = new Supplier(name, contactInfo, address, suppliedItem, transactionDate);
        newSupplier.addExpenditure(expenditureAmount);
        suppliers.add(newSupplier);
        //System.out.println("Supplier added: " + name);  // Debugging line
        saveToFile();
    }

    // Search for suppliers by name (returns a list of matching suppliers)
    public ArrayList<Supplier> searchSupplier(String name) {
        ArrayList<Supplier> foundSuppliers = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            if (supplier.getName().equalsIgnoreCase(name)) {
                foundSuppliers.add(supplier);
            }
        }
        return foundSuppliers;
    }

    public void addExpenditure(String name, String contactInfo, double amount) {
        ArrayList<Supplier> matchingSuppliers = searchSupplier(name);
    
        if (!matchingSuppliers.isEmpty()) {
            for (Supplier supplier : matchingSuppliers) {
                supplier.addExpenditure(amount);
                System.out.println("Expenditure added to supplier: " + supplier.getName());
            }
            saveToFile();  // Save after adding expenditure
        } else {
            System.out.println("Supplier not found.");
        }
    }

     // Update a supplier's details
     public void updateSupplier(String name, String contactInfo, String updatedContactInfo, String updatedAddress, 
                                String updatedSuppliedItem, String updatedTransactionDate, double updatedExpenditure) {
        for (Supplier supplier : suppliers) {
            if (supplier.getName().equalsIgnoreCase(name) && supplier.getContactInfo().equalsIgnoreCase(contactInfo)) {
                supplier.setContactInfo(updatedContactInfo);
                supplier.setSuppliedItem(updatedSuppliedItem);
                supplier.setAddress(updatedAddress);
                supplier.addExpenditure(updatedExpenditure);
                supplier.setTransactionDate(updatedTransactionDate);
                saveToFile();  // Save after editing
                System.out.println("Supplier updated: " + name);
                return;
            }
        }
        System.out.println("Supplier not found.");
    }

    // Display all suppliers in the database
    public void displayAllSuppliers() {
        System.out.println("\n--- Supplier Database ---");
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers in the database.");
        } else {
            for (Supplier supplier : suppliers) {
                supplier.displaySupplier();
            }
        }
    }

    public ArrayList<Supplier> getSuppliers() {
        return suppliers;
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Suppliers List");
            writer.println("================");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            for (Supplier supplier : suppliers) {
                // Format the expenditure separately
                String formattedExpenditure = currencyFormat.format(supplier.getTotalExpenditures());
                writer.printf("Supplier: %s | Contact: %s | Address: %s | Supplies: %s | Total Expenditure: %s | Transaction Date: %s%n",
                    supplier.getName(), supplier.getContactInfo(), supplier.getAddress(), supplier.getSuppliedItem(), formattedExpenditure, supplier.getTransactionDate());
            }
            System.out.println("Supplier data saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }
    
    // Load suppliers from file
    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Suppliers List")) {
                    continue;  // Skip header
                }
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    continue;
                }
    
                String name = parts[0].trim();
                String contactInfo = parts[1].trim();
                String address = parts[2].trim();
                String suppliedItem = parts[3].trim();
                String expenditureString = parts[4].trim();
                String transactionDate = parts[5].trim();
    
                double totalExpenditure = 0.0;
                try {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                    totalExpenditure = currencyFormat.parse(expenditureString).doubleValue();
                } catch (ParseException e) {
                    System.err.println("Error parsing expenditure from file: " + e.getMessage());
                }
    
                Supplier supplier = new Supplier(name, contactInfo, address, suppliedItem, transactionDate);
                supplier.addExpenditure(totalExpenditure);
                suppliers.add(supplier);
            }
            System.out.println("Supplier data loaded from file.");
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
    }
}