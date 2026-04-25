import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/food_delivery";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Change if your password is different

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to MySQL database successfully!");
            } else {
                System.out.println("Failed to connect to MySQL database.");
            }
        } catch (SQLException e) {
            System.out.println("MySQL Connection Error: " + e.getMessage());
        }
    }
}
