import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages the in-memory product collection.
 */
public class Inventory {
    private final List<Product> products;

    public Inventory() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product findByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> searchByName(String name) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    public List<Product> searchByCategory(String category) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                results.add(p);
            }
        }
        return results;
    }

    public boolean editProduct(String name, int quantity, double price, String category) {
        Product product = findByName(name);
        if (product != null) {
            if (quantity >= 0) product.setQuantity(quantity);
            if (price >= 0) product.setPrice(price);
            if (category != null && !category.isEmpty()) product.setCategory(category);
            return true;
        }
        return false;
    }

    public boolean deleteProduct(String name) {
        Product product = findByName(name);
        if (product != null) {
            products.remove(product);
            return true;
        }
        return false;
    }

    public int getProductCount() {
        return products.size();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public void clearAll() {
        products.clear();
    }

    public void displayProducts(List<Product> productList) {
        if (productList.isEmpty()) {
            System.out.println("\nNo products to display.");
            return;
        }

        printTableHeader();
        for (Product product : productList) {
            System.out.println(product);
        }
        printTableFooter(productList.size());
    }

    public void displayAll() {
        if (products.isEmpty()) {
            System.out.println("\nInventory is empty.");
            return;
        }

        displayProducts(products);
    }

    private void printTableHeader() {
        System.out.println("\n================================================================================");
        System.out.println("|        NAME           | QUANTITY  |    PRICE    |     CATEGORY    |");
        System.out.println("================================================================================");
    }

    private void printTableFooter(int count) {
        System.out.println("================================================================================");
        System.out.println("Total Products: " + count);
    }
}
