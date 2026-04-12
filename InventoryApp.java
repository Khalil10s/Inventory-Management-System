import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the inventory management application.
 */
public class InventoryApp {
    private static final Inventory inventory = new Inventory();
    private static final FileManager fileManager = new FileManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   INVENTORY MANAGEMENT SYSTEM");
        System.out.println("========================================");
        
        loadData(false);
        showSavedProductsOnStart();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    editProduct();
                    break;
                case 3:
                    deleteProduct();
                    break;
                case 4:
                    viewAllProducts();
                    break;
                case 5:
                    searchProducts();
                    break;
                case 6:
                    saveData();
                    break;
                case 7:
                    loadData();
                    break;
                case 0:
                    saveData();
                    System.out.println("Thank you for using Inventory Management System!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Product");
        System.out.println("2. Edit Product");
        System.out.println("3. Delete Product");
        System.out.println("4. View All Products");
        System.out.println("5. Search Products");
        System.out.println("6. Save to " + fileManager.getDefaultFilename());
        System.out.println("7. Load from " + fileManager.getDefaultFilename());
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void addProduct() {
        System.out.println("\n--- ADD NEW PRODUCT ---");
        
        System.out.print("Enter product name: ");
        String name = scanner.nextLine().trim();

        if (inventory.findByName(name) != null) {
            System.out.println("Product already exists! Use Edit option to update.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = getPositiveInt();

        System.out.print("Enter price: ");
        double price = getPositiveDouble();

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        Product product = new Product(name, quantity, price, category);
        inventory.addProduct(product);
        System.out.println("Product added successfully!");
        saveData();
    }

    private static void editProduct() {
        System.out.println("\n--- EDIT PRODUCT ---");
        
        if (inventory.isEmpty()) {
            System.out.println("No products to edit.");
            return;
        }

        System.out.print("Enter product name to edit: ");
        String name = scanner.nextLine().trim();

        Product product = inventory.findByName(name);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("\nCurrent details: " + product);
        System.out.println("Press Enter to keep current value, or enter new value.\n");

        System.out.print("New quantity (current: " + product.getQuantity() + "): ");
        String qtyInput = scanner.nextLine().trim();
        int quantity = qtyInput.isEmpty() ? product.getQuantity() : parseIntOrDefault(qtyInput, product.getQuantity());

        System.out.print("New price (current: " + product.getPrice() + "): ");
        String priceInput = scanner.nextLine().trim();
        double price = priceInput.isEmpty() ? product.getPrice() : parseDoubleOrDefault(priceInput, product.getPrice());

        System.out.print("New category (current: " + product.getCategory() + "): ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            category = product.getCategory();
        }

        if (inventory.editProduct(name, quantity, price, category)) {
            System.out.println("Product updated successfully!");
            saveData();
        } else {
            System.out.println("Failed to update product.");
        }
    }

    private static void deleteProduct() {
        System.out.println("\n--- DELETE PRODUCT ---");
        
        if (inventory.isEmpty()) {
            System.out.println("No products to delete.");
            return;
        }

        System.out.print("Enter product name to delete: ");
        String name = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete '" + name + "'? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            if (inventory.deleteProduct(name)) {
                System.out.println("Product deleted successfully!");
                saveData();
            } else {
                System.out.println("Product not found.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void viewAllProducts() {
        System.out.println("\n--- ALL PRODUCTS ---");
        inventory.displayAll();
    }

    private static void searchProducts() {
        System.out.println("\n--- SEARCH PRODUCTS ---");
        System.out.println("1. Search by name");
        System.out.println("2. Search by category");
        System.out.print("Enter choice: ");

        int searchChoice = getUserChoice();
        System.out.print("Enter search term: ");
        String term = scanner.nextLine().trim();

        List<Product> results;
        if (searchChoice == 1) {
            results = inventory.searchByName(term);
        } else if (searchChoice == 2) {
            results = inventory.searchByCategory(term);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No products found matching: " + term);
        } else {
            System.out.println("\n--- SEARCH RESULTS (" + results.size() + " found) ---");
            inventory.displayProducts(results);
        }
    }

    private static void saveData() {
        fileManager.saveToFile(inventory.getAllProducts());
    }

    private static void loadData() {
        loadData(true);
    }

    private static void loadData(boolean showMessage) {
        List<Product> products = fileManager.loadFromFile();

        inventory.clearAll();
        for (Product p : products) {
            inventory.addProduct(p);
        }

        if (showMessage) {
            System.out.println("Inventory loaded from " + fileManager.getDefaultFilename() + ".");
        }
    }

    private static void showSavedProductsOnStart() {
        if (inventory.isEmpty()) {
            System.out.println("No saved products yet.");
            return;
        }

        System.out.println("\nSaved products found:");
        inventory.displayAll();
    }

    private static int getPositiveInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                System.out.print("Please enter a non-negative number: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getPositiveDouble() {
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                System.out.print("Please enter a non-negative number: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid price: ");
            }
        }
    }

    private static int parseIntOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static double parseDoubleOrDefault(String input, double defaultValue) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
