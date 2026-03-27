import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerDatabase {
    private ArrayList<Customer> customers;
    private final String fileName = "Customers.txt";
    private int orderNumberCounter;

    public CustomerDatabase() {
        this.customers = new ArrayList<>();
        this.orderNumberCounter = 1;
    }


    //Add a new customer
    public void addCustomer(String name, String contactInfo, String address) {
        if (name == null || name.isEmpty() || contactInfo == null || contactInfo.isEmpty() || address == null || address.isEmpty()) {
            System.out.println("Invalid customer data.");
            return;
        }

        // Check for duplicates (by name and contact info)
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name) && customer.getContactInfo().equalsIgnoreCase(contactInfo)) {
                System.out.println("Customer already exists.");
                return;
            }
        }

        Customer newCustomer = new Customer(name, contactInfo, address);
        customers.add(newCustomer);
        System.out.println("Customer added: " + name);
    }

    public void addOrderToCustomer(String customerName, String item, int quantity) {
        String orderNumber = generateOrderNumber();
        Customer customer = searchCustomerByName(customerName);
        
        if (customer != null) {
            Order newOrder = new Order(orderNumber, item, quantity, new Date()); 
            customer.addOrder(newOrder);
            System.out.println("Order added to customer: " + customerName + " with order number: " + orderNumber);
        } else {
            System.out.println("Customer not found.");
        }
    }

    
    private String generateOrderNumber() {
        return "ORD" + String.format("%04d", orderNumberCounter++); // e.g. "ORD0001"
    }

    // Update customer details
    public void updateCustomer(String name, String contactInfo, String updatedContactInfo, String updatedAddress) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name) && customer.getContactInfo().equalsIgnoreCase(contactInfo)) {
                customer.setContactInfo(updatedContactInfo);
                customer.setAddress(updatedAddress);
                saveToFile();  // Save after editing
                System.out.println("Customer updated: " + name);
                return;
            }
        }
        System.out.println("Customer not found.");
    }
    // Update this to return a single Customer, not a list
    public Customer searchCustomerByName(String name) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
        }
        System.out.println("Customer not found ");
        return null;  
    }

    // Search for customer by order number 
    public Customer searchCustomerByOrder(String orderNumber) {
        for (Customer customer : customers) {
            Order order = customer.searchOrderByOrderNumber(orderNumber);
            if (order != null) {
                return customer;
            }
        }
        System.out.println("Customer with order number " + orderNumber + " not found.");
        return null;  // No customer found with that order number
    }


        public void addPayment(String name, double amount, String method) {
            // Search for a single customer by name
            Customer matchingCustomer = searchCustomerByName(name);
            
            if (matchingCustomer != null) {
                matchingCustomer.addPayment(amount, method);
                System.out.println("Payment added for customer: " + matchingCustomer.getName());
                saveToFile();  // Save after adding payment
            } else {
                System.out.println("Customer not found.");
            }
        }

    public void displayAllCustomers() {
        System.out.println("\n--- Customer Database ---");
        if (customers.isEmpty()) {
            System.out.println("No customers in the database.");
        } else {
            for (Customer customer : customers) {
                customer.displayCustomer();
            }
        }
    }

    // Remove a customer by name
    public boolean removeCustomer(String name) {
        Customer customer = searchCustomerByName(name);
        if (customer != null) {
            customers.remove(customer);
            System.out.println("Customer removed: " + name);
            return true;
        }
        System.out.println("Customer not found.");
        return false;  // No customer found to remove
    }

     // Save customer data to file
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Customer List");
            writer.println("================");
            for (Customer customer : customers) {
                writer.printf("Customer: %s | Contact Info: %s | Address: %s%n", 
                        customer.getName(), customer.getContactInfo(), customer.getAddress());
                
                // Save purchase history
                writer.println("Purchase History:");
                for (Order order : customer.getPurchaseHistory()) {
                    writer.printf("Item: %s | Quantity: %d | Date: %s%n", order.getItem(), order.getQuantity(), order.getDate());
                }
                
                // Save payment history
                writer.println("Payment History:");
                for (Payment payment : customer.getPaymentHistory()) {
                    writer.println(payment);
                }
            }
            System.out.println("Customer data saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
    
            while ((line = reader.readLine()) != null) {
                if (line.equals("Customer List")) {
                    continue;  // Skip header
                }
    
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String name = parts[0].trim().replace("Customer: ", "");
                    String contactInfo = parts[1].trim().replace("Contact Info: ", "");
                    String address = parts[2].trim().replace("Address: ", "");
    
                    Customer customer = new Customer(name, contactInfo, address);
                    customers.add(customer);
    
                    // Load purchase history
                    while ((line = reader.readLine()) != null && line.contains("Item:")) {
                        String[] orderParts = line.split("\\|");
                        String item = orderParts[0].trim().replace("Item: ", "");
                        int quantity = Integer.parseInt(orderParts[1].trim().replace("Quantity: ", ""));
                        Date date = null;
    
                        try {
                            // Parse date using SimpleDateFormat
                            date = sdf.parse(orderParts[2].trim().replace("Date: ", ""));
                        } catch (ParseException e) {
                            System.out.println("Error parsing date for purchase: " + e.getMessage());
                        }
    
                        if (date != null) {
                            String orderNumber = "ORD" + String.format("%04d", orderNumberCounter++);
                            Order order = new Order(orderNumber, item, quantity, date);
                            customer.addOrder(order); 
                        }
                    }
    
                    // Load payment history
                    while ((line = reader.readLine()) != null && line.contains("Amount: $")) {
                        String[] paymentParts = line.split("\\|");
                        double amount = Double.parseDouble(paymentParts[0].trim().replace("Amount: ", ""));
                        String method = paymentParts[1].trim().replace("Method: ", "");
                        Date date = null;
    
                        try {
                            // Parse date using SimpleDateFormat
                            date = sdf.parse(paymentParts[2].trim().replace("Date: ", ""));
                        } catch (ParseException e) {
                            System.out.println("Error parsing date for payment: " + e.getMessage());
                        }
    
                        if (date != null) {
                            customer.addPayment(amount, method);
                        }
                    }
                }
            }
            System.out.println("Customer data loaded from file.");
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
    }

    public ArrayList <Customer> getCustomers() {
        return customers;
    }
}

/*import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerDatabase {
    private ArrayList<Customer> customers;
    private final String fileName = "Customers.txt";
    private int orderNumberCounter;

    public CustomerDatabase() {
        this.customers = new ArrayList<>();
        this.orderNumberCounter = 1;
    }


    //Add a new customer
    public void addCustomer(String name, String contactInfo, String address) {
        if (name == null || name.isEmpty() || contactInfo == null || contactInfo.isEmpty() || address == null || address.isEmpty()) {
            System.out.println("Invalid customer data.");
            return;
        }

        // Check for duplicates (by name and contact info)
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name) && customer.getContactInfo().equalsIgnoreCase(contactInfo)) {
                System.out.println("Customer already exists.");
                return;
            }
        }

        Customer newCustomer = new Customer(name, contactInfo, address);
        customers.add(newCustomer);
        System.out.println("Customer added: " + name);
    }

    public void addOrderToCustomer(String customerName, String item, int quantity) {
        String orderNumber = generateOrderNumber();
        Customer customer = searchCustomerByName(customerName);
        
        if (customer != null) {
            Order newOrder = new Order(orderNumber, item, quantity, new Date()); 
            customer.addOrder(newOrder);
            System.out.println("Order added to customer: " + customerName + " with order number: " + orderNumber);
        } else {
            System.out.println("Customer not found.");
        }
    }

    
    private String generateOrderNumber() {
        return "ORD" + String.format("%04d", orderNumberCounter++); // e.g. "ORD0001"
    }

    // Update customer details
    public void updateCustomer(String name, String contactInfo, String updatedContactInfo, String updatedAddress) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name) && customer.getContactInfo().equalsIgnoreCase(contactInfo)) {
                customer.setContactInfo(updatedContactInfo);
                customer.setAddress(updatedAddress);
                saveToFile();  // Save after editing
                System.out.println("Customer updated: " + name);
                return;
            }
        }
        System.out.println("Customer not found.");
    }
    // Update this to return a single Customer, not a list
    public Customer searchCustomerByName(String name) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
        }
        System.out.println("Customer not found ");
        return null;  
    }

    // Search for customer by order number 
    public Customer searchCustomerByOrder(String orderNumber) {
        for (Customer customer : customers) {
            Order order = customer.searchOrderByOrderNumber(orderNumber);
            if (order != null) {
                return customer;
            }
        }
        System.out.println("Customer with order number " + orderNumber + " not found.");
        return null;  // No customer found with that order number
    }


        public void addPayment(String name, double amount, String method) {
            // Search for a single customer by name
            Customer matchingCustomer = searchCustomerByName(name);
            
            if (matchingCustomer != null) {
                matchingCustomer.addPayment(amount, method);
                System.out.println("Payment added for customer: " + matchingCustomer.getName());
                saveToFile();  // Save after adding payment
            } else {
                System.out.println("Customer not found.");
            }
        }

    public void displayAllCustomers() {
        System.out.println("\n--- Customer Database ---");
        if (customers.isEmpty()) {
            System.out.println("No customers in the database.");
        } else {
            for (Customer customer : customers) {
                customer.displayCustomer();
            }
        }
    }

    // Remove a customer by name
    public boolean removeCustomer(String name) {
        Customer customer = searchCustomerByName(name);
        if (customer != null) {
            customers.remove(customer);
            System.out.println("Customer removed: " + name);
            return true;
        }
        System.out.println("Customer not found.");
        return false;  // No customer found to remove
    }

     // Save customer data to file
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Customer List");
            writer.println("================");
            for (Customer customer : customers) {
                writer.printf("Customer: %s | Contact Info: %s | Address: %s%n", 
                        customer.getName(), customer.getContactInfo(), customer.getAddress());
                
                // Save purchase history
                writer.println("Purchase History:");
                for (Order order : customer.getPurchaseHistory()) {
                    writer.printf("Item: %s | Quantity: %d | Date: %s%n", order.getItem(), order.getQuantity(), order.getDate());
                }
                
                // Save payment history
                writer.println("Payment History:");
                for (Payment payment : customer.getPaymentHistory()) {
                    writer.println(payment);
                }
            }
            System.out.println("Customer data saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
    
            while ((line = reader.readLine()) != null) {
                if (line.equals("Customer List")) {
                    continue;  // Skip header
                }
    
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String name = parts[0].trim().replace("Customer: ", "");
                    String contactInfo = parts[1].trim().replace("Contact Info: ", "");
                    String address = parts[2].trim().replace("Address: ", "");
    
                    Customer customer = new Customer(name, contactInfo, address);
                    customers.add(customer);
    
                    // Load purchase history
                    while ((line = reader.readLine()) != null && line.contains("Item:")) {
                        String[] orderParts = line.split("\\|");
                        String item = orderParts[0].trim().replace("Item: ", "");
                        int quantity = Integer.parseInt(orderParts[1].trim().replace("Quantity: ", ""));
                        Date date = null;
    
                        try {
                            // Parse date using SimpleDateFormat
                            date = sdf.parse(orderParts[2].trim().replace("Date: ", ""));
                        } catch (ParseException e) {
                            System.out.println("Error parsing date for purchase: " + e.getMessage());
                        }
    
                        if (date != null) {
                            String orderNumber = "ORD" + String.format("%04d", orderNumberCounter++);
                            Order order = new Order(orderNumber, item, quantity, date);
                            customer.addOrder(order); 
                        }
                    }
    
                    // Load payment history
                    while ((line = reader.readLine()) != null && line.contains("Amount: $")) {
                        String[] paymentParts = line.split("\\|");
                        double amount = Double.parseDouble(paymentParts[0].trim().replace("Amount: ", ""));
                        String method = paymentParts[1].trim().replace("Method: ", "");
                        Date date = null;
    
                        try {
                            // Parse date using SimpleDateFormat
                            date = sdf.parse(paymentParts[2].trim().replace("Date: ", ""));
                        } catch (ParseException e) {
                            System.out.println("Error parsing date for payment: " + e.getMessage());
                        }
    
                        if (date != null) {
                            customer.addPayment(amount, method);
                        }
                    }
                }
            }
            System.out.println("Customer data loaded from file.");
        } catch (IOException e) {
            System.err.println("Error loading from file: " + e.getMessage());
        }
    }


    public ArrayList <Customer> getCustomers() {
        return customers;
    }
}*/