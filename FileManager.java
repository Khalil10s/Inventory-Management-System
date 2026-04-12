import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence for inventory data.
 */
public class FileManager {
    private static final String DEFAULT_FILENAME = "inventory.txt";

    public boolean saveToFile(List<Product> products, String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Product product : products) {
                writer.println(product.toFileString());
            }
            System.out.println("Inventory saved successfully to: " + filename);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        }
    }

    public boolean saveToFile(List<Product> products) {
        return saveToFile(products, DEFAULT_FILENAME);
    }

    public List<Product> loadFromFile(String filename) {
        List<Product> products = new ArrayList<>();

        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }

        File file = new File(filename);
        if (!file.exists()) {
            if (!filename.equals(DEFAULT_FILENAME)) {
                System.out.println("File not found: " + filename);
            }
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Product product = Product.fromFileString(line);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
            System.out.println("Loaded " + products.size() + " products from: " + filename);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

        return products;
    }

    public List<Product> loadFromFile() {
        return loadFromFile(DEFAULT_FILENAME);
    }

    public boolean fileExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = DEFAULT_FILENAME;
        }

        File file = new File(filename);
        return file.exists();
    }

    public String getDefaultFilename() {
        return DEFAULT_FILENAME;
    }
}
