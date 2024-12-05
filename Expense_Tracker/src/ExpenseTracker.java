import java.sql.*;
import java.util.Scanner;

public class ExpenseTracker {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Expense Tracker Menu ===");
            System.out.println("1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. Calculate Total Balance");
            System.out.println("4. Delete Transaction");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    calculateBalance();
                    break;
                case 4:
                    deleteTransaction();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addTransaction() {
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter type (Income/Expense): ");
        String type = scanner.nextLine();

        try (Connection connection = DBConnection.getConnection()) {
            String query = "INSERT INTO Transactions (description, amount, type) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, description);
            statement.setDouble(2, amount);
            statement.setString(3, type);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Transaction added successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewTransactions() {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM Transactions";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("\nID | Description | Amount | Type | Date");
            System.out.println("----------------------------------------");
            while (resultSet.next()) {
                System.out.printf("%d | %s | %.2f | %s | %s\n",
                        resultSet.getInt("id"),
                        resultSet.getString("description"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("type"),
                        resultSet.getDate("date"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void calculateBalance() {
        try (Connection connection = DBConnection.getConnection()) {
            String queryIncome = "SELECT SUM(amount) AS totalIncome FROM Transactions WHERE type = 'Income'";
            String queryExpense = "SELECT SUM(amount) AS totalExpense FROM Transactions WHERE type = 'Expense'";

            Statement statement = connection.createStatement();
            ResultSet incomeResult = statement.executeQuery(queryIncome);
            incomeResult.next();
            double totalIncome = incomeResult.getDouble("totalIncome");

            ResultSet expenseResult = statement.executeQuery(queryExpense);
            expenseResult.next();
            double totalExpense = expenseResult.getDouble("totalExpense");

            double balance = totalIncome - totalExpense;

            System.out.println("\n=== Balance Summary ===");
            System.out.printf("Total Income: %.2f\n", totalIncome);
            System.out.printf("Total Expense: %.2f\n", totalExpense);
            System.out.printf("Net Balance: %.2f\n", balance);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void deleteTransaction() {
        System.out.print("Enter the Transaction ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try (Connection connection = DBConnection.getConnection()) {
            String query = "DELETE FROM Transactions WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Transaction deleted successfully!");
            } else {
                System.out.println("Transaction not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}