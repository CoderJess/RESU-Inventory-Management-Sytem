import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Supplier {
    private String name;
    private String contactInfo;
    private String suppliedItem;
    private String address;
    private String transactionDate; 
    private double totalExpenditures;


    // Constructor to initialize the Supplier object
    public Supplier(String name, String contactInfo, String address, String suppliedItem, String transactionDate) {
        this.name = name;
        this.contactInfo = formatContactInfo(contactInfo);
        this.suppliedItem = suppliedItem;
        this.address = address;
        this.transactionDate = transactionDate;
        this.totalExpenditures = 0.0;
    }

    // Getter methods for each attribute
    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getSuppliedItem() {
        return suppliedItem;
    }

    public void setContactInfo(String contactInfo){
        this.contactInfo = formatContactInfo(contactInfo);
    }

    public void setSuppliedItem(String suppliedItem){
        this.suppliedItem = suppliedItem;
    }

    public double getTotalExpenditures(){
        return totalExpenditures;
    }

    // Add single Expenditure to the total Expenditures
    public void addExpenditure(double amount){
        this.totalExpenditures = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTransactionDate() {
        return transactionDate;
    }


    public static boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
        dateFormat.setLenient(false); // Strict parsing
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void setTransactionDate(String transactionDate) {
        if (isValidDate(transactionDate)) {
            this.transactionDate = transactionDate;
        } else {
            throw new IllegalArgumentException("Please enter a valid date yyyy/mm/dd.");
        }
    }


    // Formatting contactInfo
    private String formatContactInfo(String contactInfo) {
        if (contactInfo.matches("\\d{3}-\\d{3}-\\d{4}")) {
            return contactInfo; //Already formated
        } else if (contactInfo.matches("\\d{10}")) {
            
            //Correct the contact format 
            return contactInfo.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else {
            throw new IllegalArgumentException("Please enter a valid contact number xxx-xxx-xxxx.");
        }
    }


    // Method to display the supplier's information
    public void displaySupplier() {
        System.out.printf("Supplier: %s | Contact: %s | Address: %s | Supplies: %s% | Total Expenditure: %.2f% | Transaction Date:  %n"
        , name, contactInfo, address, suppliedItem, totalExpenditures,transactionDate);
    }
}