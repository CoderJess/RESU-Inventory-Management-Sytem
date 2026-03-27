import java.util.Date;

public class Payment {
     private double amount;
    private String method;
    private Date date;

    public Payment(double amount, String method, Date date) {
        this.amount = amount;
        this.method = method;
        this.date = date;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("Amount: %.2f | Method: %s | Date: %s", amount, method, date);
    }
}

