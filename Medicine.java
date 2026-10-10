import java.time.LocalDate;

public class Medicine {
    private String id, name;
    private double price;
    private int quantity;
    private LocalDate expiry;

    public Medicine(String id, String name, double price,
                    int quantity, LocalDate expiry) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.expiry = expiry;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiry() { return expiry; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setExpiry(LocalDate expiry) { this.expiry = expiry; }

    public String toString() {
        return id + " | " + name + " | ₹" + price +
            " | Stock: " + quantity + " | Expiry: " + expiry;
    }
}
