package utils;

import java.sql.*;
import java.util.*;

/**
 * DBUtil: The Database Engine of the Automation Framework.
 * This class handles all "Back-End" validations by talking directly to SQLite.
 */
public class DBUtil {

    // ============================================================
    // 1. CONFIGURATION BLOCK (The "Address")
    // This section tells Java WHERE the database is and WHICH table to use.
    // ============================================================
    private static final String URL = "jdbc:sqlite:src/test/resources/automation_db.sqlite";
    private static final String TABLE_NAME = "WebUsers";

    // ============================================================
    // 2. ACTIVE MANUAL CRUD METHODS (The "Sync" Block)
    // These methods push data from your Excel Map into the Database.
    // CRUD = Create, Read, Update, Delete.
    // ============================================================

    /**
     * INSERT: This method simulates the website saving a new user to the database.
     * It maps Excel keys (e.g., "FirstName") to Database columns (e.g., "firstname").
     */
    public static void insertUserManual(Map<String, String> userData) {
        // The '?' are placeholders that prevent SQL Injection (Security)
        String sql = "INSERT INTO WebUsers (firstname, lastname, email, age, salary, department) VALUES (?, ?, ?, ?, ?, ?)";

        // 'try-with-resources' automatically closes the connection when finished
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // We manually set each value. 1 = the first '?', 2 = the second '?', etc.
            pstmt.setString(1, userData.get("FirstName"));
            pstmt.setString(2, userData.get("LastName"));
            pstmt.setString(3, userData.get("Email"));

            // Databases need numbers to be Integers, so we convert the Excel String here
            pstmt.setInt(4, Integer.parseInt(userData.get("Age")));
            pstmt.setInt(5, Integer.parseInt(userData.get("Salary")));

            pstmt.setString(6, userData.get("Department"));

            pstmt.executeUpdate(); // This actually "fires" the command to the DB
            System.out.println("🚀 DB MANUAL SYNC: Record inserted for " + userData.get("Email"));
        } catch (Exception e) {
            System.err.println("❌ Manual Insert Failed: " + e.getMessage());
        }
    }

    /**
     * UPDATE: This method changes an existing record.
     * It uses the ORIGINAL Email (index 7) as an "Anchor" to find the right row.
     */
    public static void updateUserManual(Map<String, String> userData) {
        String sql = "UPDATE WebUsers SET firstname=?, lastname=?, email=?, age=?, salary=?, department=? WHERE email=?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // --- THE "SET" SECTION (New data from 'Edit' columns in Excel) ---
            pstmt.setString(1, userData.get("EditFirstName"));
            pstmt.setString(2, userData.get("EditLastName"));
            pstmt.setString(3, userData.get("EditEmail"));
            pstmt.setInt(4, Integer.parseInt(userData.get("EditAge")));
            pstmt.setInt(5, Integer.parseInt(userData.get("EditSalary")));
            pstmt.setString(6, userData.get("EditDepartment"));

            // --- THE "WHERE" SECTION (Finding the specific record to change) ---
            pstmt.setString(7, userData.get("Email")); // Target the original email

            int rows = pstmt.executeUpdate();
            System.out.println("🔄 DB MANUAL SYNC: Updated " + rows + " row(s).");
        } catch (Exception e) {
            System.err.println("❌ Manual Update Failed: " + e.getMessage());
        }
    }

    // ============================================================
    // 3. UTILITY & SEARCH METHODS (The "Auditor" Block)
    // These methods pull data OUT of the database so we can verify it.
    // ============================================================

    /**
     * SELECT: Finds a user by their email address.
     * This is the DB equivalent of searching the Web Table in the browser.
     */
    public static Map<String, Object> getUserByEmail(String tableName,String email) {
        String sql = "SELECT * FROM " + tableName + " WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery(); // Use executeQuery for 'SELECT'

            if (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                // ResultSetMetaData is 'Magic' — it asks the DB for the column names
                // so we don't have to hardcode them here.
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    // We store the column name (Key) and the data (Value) in a Map
                    row.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
                }
                System.out.println("✅ DB FOUND: User → " + email);
                return row;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB getUserByEmail failed: " + e.getMessage(), e);
        }
        System.out.println("⚠️ DB NOT FOUND: No user with email → " + email);
        return null; // Returns null if the user doesn't exist in the DB
    }

    /**
     * DELETE: The "Cleanup" method.
     * Ensures that test data is removed so the next test run starts fresh.
     */
    public static void deleteUserByEmail(String email) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("🧹 DB CLEANUP: Removed existing record for " + email);
            }
        } catch (SQLException e) {
            System.err.println("❌ DB Cleanup Error: " + e.getMessage());
        }
    }

    // ============================================================
    // 4. ENGINE METHODS (The "Internal Gears")
    // Generic methods used for one-off commands or schema checks.
    // ============================================================

    /**
     * A generic method to run any SQL 'Action' (Create, Drop, etc.)
     */
    public static void executeUpdate(String sql) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("DB Update Error: " + e.getMessage());
        }
    }

    // ============================================================
    // 5. DB INITIALIZER (The "Builder" Block)
    // Run this once (uncomment the main method) to create your DB table.
    // ============================================================

    /*
    public static void main(String[] args) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS WebUsers & FormTest (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT," +
                "lastName TEXT," +
                "email TEXT," +
                "age INTEGER," +
                "salary INTEGER," +
                "department TEXT" +
                ");";
        DBUtil.executeUpdate(createTableSQL);
        System.out.println("✅ Database created and Table 'WebUsers' is ready!");
    }
    */
}