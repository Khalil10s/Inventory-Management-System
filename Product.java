/**
 * Represents a single inventory item.
 */
public class Product {
    private String name;
    private int quantity;
    private double price;
    private String category;

    public Product(String name, int quantity, double price, String category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("| %-20s | %-8d | $%-10.2f | %-15s |", name, quantity, price, category);
    }

    public String toFileString() {
        return name + "," + quantity + "," + price + "," + category;
    }

    public static Product fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            return null;
        }

        try {
            String name = parts[0].trim();
            int quantity = Integer.parseInt(parts[1].trim());
            double price = Double.parseDouble(parts[2].trim());
            String category = parts[3].trim();

            return new Product(name, quantity, price, category);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
