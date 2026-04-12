import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Runs an end-to-end regression test against the console app in an isolated temp directory.
 */
public class InventoryAppRegressionTest {
    private static final List<String> APP_SOURCES = List.of(
        "Product.java",
        "Inventory.java",
        "FileManager.java",
        "InventoryApp.java"
    );

    public static void main(String[] args) throws Exception {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path tempDir = Files.createTempDirectory("inventory-app-test-");

        try {
            copySources(projectRoot, tempDir);
            compileApp(tempDir);
            runScenarioA(tempDir);
            runScenarioB(tempDir);
            System.out.println("ALL TESTS PASSED");
        } finally {
            deleteRecursively(tempDir);
        }
    }

    private static void copySources(Path projectRoot, Path tempDir) throws IOException {
        for (String source : APP_SOURCES) {
            Files.copy(projectRoot.resolve(source), tempDir.resolve(source), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void compileApp(Path workingDir) throws Exception {
        ProcessResult result = runProcess(buildJavaCommand("javac"), workingDir, null, APP_SOURCES);
        assertExitCode(result, 0, "Compilation failed");
    }

    private static void runScenarioA(Path workingDir) throws Exception {
        String input = String.join(System.lineSeparator(),
            "1",
            "Laptop",
            "10",
            "999.99",
            "Electronics",
            "4",
            "5",
            "1",
            "Lap",
            "2",
            "Laptop",
            "15",
            "899.99",
            "Computers",
            "6",
            "0",
            ""
        );

        ProcessResult result = runProcess(buildJavaCommand("java"), workingDir, input, List.of("InventoryApp"));
        assertExitCode(result, 0, "Scenario A failed");
        assertContains(result.output(), "No saved products yet.", "Expected empty-state message on first launch");
        assertContains(result.output(), "Product added successfully!", "Expected add-product confirmation");
        assertContains(result.output(), "--- SEARCH RESULTS (1 found) ---", "Expected search results for added product");
        assertContains(result.output(), "Product updated successfully!", "Expected edit-product confirmation");

        String inventoryContents = Files.readString(workingDir.resolve("inventory.txt"), StandardCharsets.UTF_8).trim();
        assertEquals("Laptop,15,899.99,Computers", inventoryContents, "Unexpected inventory contents after scenario A");
    }

    private static void runScenarioB(Path workingDir) throws Exception {
        String input = String.join(System.lineSeparator(),
            "5",
            "2",
            "Computers",
            "3",
            "Laptop",
            "y",
            "4",
            "0",
            ""
        );

        ProcessResult result = runProcess(buildJavaCommand("java"), workingDir, input, List.of("InventoryApp"));
        assertExitCode(result, 0, "Scenario B failed");
        assertContains(result.output(), "Loaded 1 products from: inventory.txt", "Expected saved data to load on restart");
        assertContains(result.output(), "Saved products found:", "Expected startup inventory preview");
        assertContains(result.output(), "Product deleted successfully!", "Expected delete-product confirmation");
        assertContains(result.output(), "Inventory is empty.", "Expected empty inventory after deletion");

        String inventoryContents = Files.readString(workingDir.resolve("inventory.txt"), StandardCharsets.UTF_8);
        assertEquals("", inventoryContents.trim(), "Expected empty inventory file after deleting the last product");
    }

    private static List<String> buildJavaCommand(String executableName) {
        String executable = Path.of(
            System.getProperty("java.home"),
            "bin",
            isWindows() ? executableName + ".exe" : executableName
        ).toString();
        return List.of(executable);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static ProcessResult runProcess(List<String> commandPrefix, Path workingDir, String input, List<String> args)
        throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command().addAll(commandPrefix);
        builder.command().addAll(args);
        builder.directory(workingDir.toFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();
        if (input != null) {
            process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        }
        process.getOutputStream().close();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        return new ProcessResult(exitCode, output);
    }

    private static void assertContains(String output, String expected, String message) {
        if (!output.contains(expected)) {
            throw new IllegalStateException(message + System.lineSeparator() + "Missing text: " + expected + System.lineSeparator() + output);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(message + System.lineSeparator() + "Expected: " + expected + System.lineSeparator() + "Actual: " + actual);
        }
    }

    private static void assertExitCode(ProcessResult result, int expectedExitCode, String message) {
        if (result.exitCode() != expectedExitCode) {
            throw new IllegalStateException(message + System.lineSeparator() + result.output());
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        try (var paths = Files.walk(path)) {
            paths.sorted((left, right) -> right.compareTo(left))
                .forEach(current -> {
                    try {
                        Files.deleteIfExists(current);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    private record ProcessResult(int exitCode, String output) {
    }
}