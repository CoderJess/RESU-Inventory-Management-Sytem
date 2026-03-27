import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InventoryUsageAnalytics {

    private StockInventory stockInventory;
    private Map<String, Integer> usageData;  // Track usage for each item
    private CustomerDatabase customerDatabase;

    public InventoryUsageAnalytics(StockInventory stockInventory, CustomerDatabase customerDatabase) {
        this.stockInventory = stockInventory;
        this.usageData = new HashMap<>();
        this.customerDatabase = customerDatabase;
    }

    // Method to generate the usage report
    public String generateReport(String timeRange) {
        StringBuilder report = new StringBuilder();
        report.append("Inventory Usage Report (").append(timeRange).append(")\n\n");
        report.append("Item Name\t\tUsage Quantity\n");

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        boolean itemsIncluded = false;  // Flag to check if any items match the time range

        // Loop through each stock item in the inventory
        for (Stock stock : stockInventory.getInventory()) {
            int usageCount = stock.getUsageCount();

            if (usageCount > 0) {
                boolean includeInReport = false;
                Date orderDate = getLastOrderDate(stock);  // Get the last order date for the stock

                if (orderDate != null) {
                    switch (timeRange.toLowerCase()) {
                        case "weekly":
                            includeInReport = isInCurrentWeek(orderDate, calendar);
                            break;
                        case "monthly":
                            includeInReport = isInCurrentMonth(orderDate, calendar);
                            break;
                        case "yearly":
                            includeInReport = isInCurrentYear(orderDate, calendar);
                            break;
                        default:
                            return "Invalid time range";
                    }

                    if (includeInReport) {
                        report.append(stock.getItemName()).append("\t\t").append(usageCount).append("\n");
                        itemsIncluded = true;
                    }
                }
            }
        }

        if (!itemsIncluded) {
            report.append("\nNo items found in the selected time range.\n");
        }

        // Add the top 5 most frequently used items to the report
        report.append("\nTop 5 Most Frequently Used Items:\n");
        usageData.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())  // Sort by usage descending
                .limit(5)
                .forEach(entry -> report.append(entry.getKey()).append("\t\t").append(entry.getValue()).append("\n"));

        return report.toString();
    }

    // Helper Methods for Time Range Filtering
    private boolean isInCurrentWeek(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentWeekToday = today.get(Calendar.WEEK_OF_YEAR);
        int currentYearToday = today.get(Calendar.YEAR);

        return (currentYear == currentYearToday && currentWeek == currentWeekToday);
    }

    private boolean isInCurrentMonth(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentMonthToday = today.get(Calendar.MONTH);
        int currentYearToday = today.get(Calendar.YEAR);

        return (currentYear == currentYearToday && currentMonth == currentMonthToday);
    }

    private boolean isInCurrentYear(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentYearToday = today.get(Calendar.YEAR);

        return currentYear == currentYearToday;
    }

    // Method to get the most recent order date of a stock item from the customer purchase history
    private Date getLastOrderDate(Stock stock) {
        Date lastOrderDate = null;

        for (Customer customer : customerDatabase.getCustomers()) {
            List<Order> purchaseHistory = customer.getPurchaseHistory();
            for (Order order : purchaseHistory) {
                if (order.getItem().equals(stock.getItemName())) {
                    if (lastOrderDate == null || order.getDate().after(lastOrderDate)) {
                        lastOrderDate = order.getDate();
                    }
                }
            }
        }
        return lastOrderDate;
    }

    // Method to export the report to a TXT file with the time frame
    public boolean exportReport(String reportContent, String timeRange) {
        try {
            // Create a timestamp for the export file
            String timeStamp = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
            String fileName = "Inventory_Usage_Report_" + timeStamp + ".txt";

            // Create the file and write the report content
            FileWriter writer = new FileWriter(fileName);
            writer.write("Exported on: " + timeStamp + "\n");
            writer.write("Time Range: " + timeRange + "\n\n");
            writer.write(reportContent);
            writer.close();
            System.out.println("Report exported to: " + fileName);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting report: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void showReportInDashboard(String reportContent, Map<String, Integer> usageData) {
        // Create a JFrame for the dashboard
        JFrame dashboardFrame = new JFrame("Inventory Usage Report");

        // Create a panel for displaying both the report and the histogram
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create a JTextArea for the report content
        JTextArea reportArea = new JTextArea(reportContent);
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);  // Allow line wrapping in report
        reportArea.setWrapStyleWord(true);  // Ensure words wrap properly
        reportArea.setCaretPosition(0);  // Start at the top of the report
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
        reportScrollPane.setPreferredSize(new Dimension(600, 200)); // Adjust height of report area
        mainPanel.add(reportScrollPane, BorderLayout.NORTH);

        // Check if usageData is available and not empty
        if (usageData == null || usageData.isEmpty()) {
            reportArea.setText("No usage data available.");
            dashboardFrame.setSize(800, 300);  // Adjust window size
            dashboardFrame.setVisible(true);
            return; // Exit early if no data to display
        }

        // Create the HistogramPanel and add data to it
        HistogramPanel histogramPanel = new HistogramPanel();

        // Predefined set of colors (you can expand this array if needed)
        Color[] colorPalette = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN,
            Color.MAGENTA, Color.YELLOW, Color.PINK, Color.DARK_GRAY, Color.LIGHT_GRAY
        };

        // Loop through the usage data and add it to the histogram
        int colorIndex = 0;  // Index for color palette
        for (Map.Entry<String, Integer> entry : usageData.entrySet()) {
            String itemName = entry.getKey();
            int usage = entry.getValue();

            // Cycle through the colors from the palette
            Color barColor = colorPalette[colorIndex % colorPalette.length];  // Loop through colors

            histogramPanel.addHistogramColumn(itemName, usage, barColor);

            // Increment the color index
            colorIndex++;
        }

        // Layout the histogram after adding the data
        histogramPanel.layoutHistogram();

        // Add the histogram panel below the report
        mainPanel.add(histogramPanel, BorderLayout.CENTER);

        // Set up the dashboard frame
        dashboardFrame.add(mainPanel);
        dashboardFrame.setSize(800, 600);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.setVisible(true);
    }
}


/*import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// Assuming these are classes for Stock, Customer, Order, and StockInventory
class InventoryUsageAnalytics {

    private StockInventory stockInventory;
    private Map<String, Integer> usageData;  // Track usage for each item
    private CustomerDatabase customerDatabase;

    public InventoryUsageAnalytics(StockInventory stockInventory, CustomerDatabase customerDatabase) {
        this.stockInventory = stockInventory;
        this.usageData = new HashMap<>();
        this.customerDatabase = customerDatabase;
    }

    // Method to track usage
    public void trackUsage(String itemName, int quantityUsed) {
        usageData.put(itemName, usageData.getOrDefault(itemName, 0) + quantityUsed);
    }

    // Method to generate the usage report
    public String generateReport(String timeRange) {
        StringBuilder report = new StringBuilder();
        report.append("Inventory Usage Report (").append(timeRange).append(")\n\n");
        report.append("Item Name\t\tUsage Quantity\n");

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        boolean itemsIncluded = false;  // Flag to check if any items match the time range

        // Loop through each stock item in the inventory
        for (Stock stock : stockInventory.getInventory()) {
            int usageCount = stock.getUsageCount();

            if (usageCount > 0) {
                boolean includeInReport = false;
                Date orderDate = getLastOrderDate(stock);  // Get the last order date for the stock

                if (orderDate != null) {
                    switch (timeRange.toLowerCase()) {
                        case "weekly":
                            includeInReport = isInCurrentWeek(orderDate, calendar);
                            break;
                        case "monthly":
                            includeInReport = isInCurrentMonth(orderDate, calendar);
                            break;
                        case "yearly":
                            includeInReport = isInCurrentYear(orderDate, calendar);
                            break;
                        default:
                            System.out.println("Invalid time range");
                            return "Invalid time range";
                    }

                    if (includeInReport) {
                        report.append(stock.getItemName()).append("\t\t").append(usageCount).append("\n");
                        itemsIncluded = true;
                    }
                }
            }
        }

        if (!itemsIncluded) {
            report.append("\nNo items found in the selected time range.\n");
        }

        // Add the top 5 most frequently used items to the report
        report.append("\nTop 5 Most Frequently Used Items:\n");
        usageData.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())  // Sort by usage descending
                .limit(5)
                .forEach(entry -> report.append(entry.getKey()).append("\t\t").append(entry.getValue()).append("\n"));

        return report.toString();
    }

    // Helper Methods for Time Range Filtering
    private boolean isInCurrentWeek(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentWeekToday = today.get(Calendar.WEEK_OF_YEAR);
        int currentYearToday = today.get(Calendar.YEAR);

        return (currentYear == currentYearToday && currentWeek == currentWeekToday);
    }

    private boolean isInCurrentMonth(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentMonthToday = today.get(Calendar.MONTH);
        int currentYearToday = today.get(Calendar.YEAR);

        return (currentYear == currentYearToday && currentMonth == currentMonthToday);
    }

    private boolean isInCurrentYear(Date orderDate, Calendar calendar) {
        calendar.setTime(orderDate);
        int currentYear = calendar.get(Calendar.YEAR);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int currentYearToday = today.get(Calendar.YEAR);

        return currentYear == currentYearToday;
    }

    // Method to get the most recent order date of a stock item from the customer purchase history
    private Date getLastOrderDate(Stock stock) {
        Date lastOrderDate = null;

        for (Customer customer : customerDatabase.getCustomers()) {
            List<Order> purchaseHistory = customer.getPurchaseHistory();
            for (Order order : purchaseHistory) {
                if (order.getItem().equals(stock.getItemName())) {
                    if (lastOrderDate == null || order.getDate().after(lastOrderDate)) {
                        lastOrderDate = order.getDate();
                    }
                }
            }
        }
        return lastOrderDate;
    }

    // Method to export the report to a TXT file with the time frame
    public boolean exportReport(String reportContent, String timeRange) {
        try {
            // Create a timestamp for the export file
            String timeStamp = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
            String fileName = "Inventory_Usage_Report_" + timeStamp + ".txt";

            // Create the file and write the report content
            FileWriter writer = new FileWriter(fileName);
            writer.write("Exported on: " + timeStamp + "\n");
            writer.write("Time Range: " + timeRange + "\n\n");
            writer.write(reportContent);
            writer.close();
            System.out.println("Report exported to: " + fileName);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting report: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void showReportInDashboard(String reportContent, Map<String, Integer> usageData) {
        // Create a JFrame for the dashboard
        JFrame dashboardFrame = new JFrame("Inventory Usage Report");

        // Create a panel for displaying both the report and the histogram
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create a JTextArea for the report content
        JTextArea reportArea = new JTextArea(reportContent);
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);  // Allow line wrapping in report
        reportArea.setWrapStyleWord(true);  // Ensure words wrap properly
        reportArea.setCaretPosition(0);  // Start at the top of the report
        JScrollPane reportScrollPane = new JScrollPane(reportArea);
        reportScrollPane.setPreferredSize(new Dimension(600, 200)); // Adjust height of report area
        mainPanel.add(reportScrollPane, BorderLayout.NORTH);

        // Check if usageData is available and not empty
        if (usageData == null || usageData.isEmpty()) {
            reportArea.setText("No usage data available.");
            dashboardFrame.setSize(800, 300);  // Adjust window size
            dashboardFrame.setVisible(true);
            return; // Exit early if no data to display
        }

        // Create the HistogramPanel and add data to it
        HistogramPanel histogramPanel = new HistogramPanel();

        // Predefined set of colors (you can expand this array if needed)
        Color[] colorPalette = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN,
            Color.MAGENTA, Color.YELLOW, Color.PINK, Color.DARK_GRAY, Color.LIGHT_GRAY
        };

        // Loop through the usage data and add it to the histogram
        int colorIndex = 0;  // Index for color palette
        for (Map.Entry<String, Integer> entry : usageData.entrySet()) {
            String itemName = entry.getKey();
            int usage = entry.getValue();

            // Cycle through the colors from the palette
            Color barColor = colorPalette[colorIndex % colorPalette.length];  // Loop through colors

            histogramPanel.addHistogramColumn(itemName, usage, barColor);

            // Increment the color index
            colorIndex++;
        }

        // Layout the histogram after adding the data
        histogramPanel.layoutHistogram();

        // Add the histogram panel below the report
        mainPanel.add(histogramPanel, BorderLayout.CENTER);

        // Set up the dashboard frame
        dashboardFrame.add(mainPanel);
        dashboardFrame.setSize(800, 600);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.setVisible(true);
    }
}*/
