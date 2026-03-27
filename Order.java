
import java.util.Date;

public class Order {
        private String orderNumber;
        private String item;
        private int quantity;
        private Date date;
    
        // Constructor
        public Order(String orderNumber, String item, int quantity, Date date) {
            this.orderNumber = orderNumber;
            this.item = item;
            this.quantity = quantity;
            this.date = date;
        }
    
        // Getters and Setters
        public String getOrderNumber() {
            return orderNumber;
        }
    
        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }
    
        public String getItem() {
            return item;
        }
    
        public void setItem(String item) {
            this.item = item;
        }
    
        public int getQuantity() {
            return quantity;
        }
    
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    
        public Date getDate() {
            return date;
        }
    
        public void setDate(Date date) {
            this.date = date;
        }
    
        @Override
        public String toString() {
            return "OrderNumber: " + orderNumber + " | Item: " + item + " | Quantity: " + quantity + " | Date: " + date;
        }
    }


