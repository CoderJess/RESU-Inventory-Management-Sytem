import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;

public class InventoryChange {
    private String staffId;
    private String reasonForChange;
    private LocalDateTime dateTimeOfChange;
    private int previousQuantity;
    private int newQuantity;
    private double salesAdjustment;

    // Constructor to initialize inventory change details
    public InventoryChange(String staffId, String reasonForChange, int previousQuantity, int newQuantity, double salesAdjustment) {
        this.staffId = staffId;
        this.reasonForChange = reasonForChange;
        this.dateTimeOfChange = LocalDateTime.now();
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.salesAdjustment = salesAdjustment;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getReasonForChange() {
        return reasonForChange;
    }

    public LocalDateTime getDateTimeOfChange() {
        return dateTimeOfChange;
    }

    public int getPreviousQuantity() {
        return previousQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public double getSalesAdjustment() {
        return salesAdjustment;
    }

    @Override
    public String toString() {
        return String.format("| %s | %s | %s | Previous: %d | New: %d | Sales Adjustment: %.2f |", staffId, reasonForChange, dateTimeOfChange, previousQuantity, newQuantity, salesAdjustment);
    }

}
//AI used to iron out any kinks and fix the small mistakes

