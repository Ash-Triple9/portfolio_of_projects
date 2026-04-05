import java.sql.*;
import java.util.*;

/**
 * Class Name: Application
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class represents a single Pet Adoption Application. It contains methods 
 * to perform CRUD operations (Insert, Update Status, Conditional Delete/Withdraw) 
 * on the APPLICATION table in the database, as well as handling console input 
 * for these operations.
 * Public Variables:
 * applicationID: Unique identifier for the application record.
 * memberID: ID of the customer who submitted the application.
 * petID: ID of the pet the member is applying for.
 * employeeID: ID of the adoption coordinator reviewing the application.
 * applicationDate: Date the application was submitted (YYYY-MM-DD).
 * status: Current state of the application (e.g., 'pending', 'approved', 'withdrawn').
 * Constructors:
 * Application(int, int, int, int, String, String): Initializes a new Application object.
 * Implemented Methods:
 * insertApplication(Connection, Application): Inserts a new application into the DB.
 * updateStatus(Connection, int, String): Changes the status of an existing application.
 * withdrawOrDeleteApplication(Connection, int): Handles deletion based on current status.
 * createApplicationUI(Connection, Scanner): Collects user input for a new application.
 * manageApplicationUI(Connection, Scanner): Presents menu for updating or removing an application.
 */
public class Application {
    // Unique identifier for the application.
    public int applicationID;
    // Customer ID who submitted the application.
    public int memberID;
    // Pet ID being applied for.
    public int petID;
    // Coordinator Employee ID assigned to the case.
    public int employeeID; 
    // Date the application was submitted.
    public String applicationDate;
    // Current status (e.g., pending, approved, withdrawn).
    public String status; 

    /**
     * Method: Application Constructor
     * Purpose: Initializes a new Application object in memory.
     * Pre-conditions: Valid input for all application details.
     * Post-conditions: A new Application object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int applicationID (In): The ID for the new record.
     * int memberID (In): The member submitting the application.
     * int petID (In): The pet being applied for.
     * int employeeID (In): The coordinator handling the application.
     * String applicationDate (In): The submission date.
     * String status (In): The initial status, usually "pending".
     */
    public Application(int applicationID, int memberID, int petID, int employeeID, 
                        String applicationDate, String status) {
        this.applicationID = applicationID;
        this.memberID = memberID;
        this.petID = petID;
        this.employeeID = employeeID;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: insertApplication
     * Purpose: Inserts a new application record into the database using a Prepared Statement.
     * Pre-conditions: Active database connection (conn) and a fully populated Application object (app).
     * Post-conditions: A new row is added to the APPLICATION table.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Application app (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertApplication(Connection conn, Application app) throws SQLException {
        // SQL query to insert application data, converting the date string to a proper timestamp.
        String sql = "INSERT INTO application (applicationID, memberID, petID, employeeID, applicationDate, status) " +
                     "VALUES (?, ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Map the object fields to the SQL placeholders.
            ps.setInt(1, app.applicationID);
            ps.setInt(2, app.memberID);
            ps.setInt(3, app.petID);
            ps.setInt(4, app.employeeID);
            ps.setString(5, app.applicationDate);
            ps.setString(6, app.status); 
            
            ps.executeUpdate();
            System.out.println("Adoption application submitted successfully.");
        }
    }

    /**
     * Method: updateStatus
     * Purpose: Updates the status field for a specific application ID.
     * Pre-conditions: Active connection (conn), a valid application ID (appID), and the new status string.
     * Post-conditions: The status column for the specified row is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int appID (In): The ID of the application to modify.
     * String newStatus (In): The status to set (e.g., 'approved').
     * Throws: SQLException for database errors.
     */
    public static void updateStatus(Connection conn, int appID, String newStatus) throws SQLException {
        String sql = "UPDATE application SET status = ? WHERE applicationID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, appID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Application status updated to: " + newStatus);
            else System.out.println("Application ID not found.");
        }
    }

    /**
     * Method: withdrawOrDeleteApplication
     * Purpose: Implements the business logic for application removal. If the status is 'pending' 
     * (meaning no review started), it deletes the record. Otherwise, it updates the status to 'withdrawn'.
     * Pre-conditions: Active connection (conn) and a valid application ID (appID).
     * Post-conditions: Either the record is deleted, or the status is set to 'withdrawn'.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int appID (In): The ID of the application to withdraw or delete.
     * Throws: SQLException for database errors.
     */
    public static void withdrawOrDeleteApplication(Connection conn, int appID) throws SQLException {
        // Step 1: Find the current status of the application.
        String statusSql = "SELECT status FROM application WHERE applicationID = ?";
        String currentStatus = "";
        
        try (PreparedStatement ps = conn.prepareStatement(statusSql)) {
            ps.setInt(1, appID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) currentStatus = rs.getString("status");
            else {
                System.out.println("Application ID not found.");
                return;
            }
        }

        // Step 2: Decide whether to hard delete or mark as withdrawn.
        if (currentStatus.equalsIgnoreCase("pending")) {
            // Only delete if the application is still 'pending' (submitted in error).
            String delSql = "DELETE FROM application WHERE applicationID = ?";
            try (PreparedStatement ps = conn.prepareStatement(delSql)) {
                ps.setInt(1, appID);
                ps.executeUpdate();
                System.out.println("Application deleted (was pending).");
            }
        } else {
            // If review has started, update to 'withdrawn' to keep a history of the application.
            System.out.println("Cannot delete application (Status is '" + currentStatus + "'). Marking as 'withdrawn' instead.");
            updateStatus(conn, appID, "withdrawn");
        }
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: createApplicationUI
     * Purpose: Collects all required data from the user via the console and calls the insertion method.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new application record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void createApplicationUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- New Adoption Application ---");
            System.out.print("Application ID: "); 
            // Read and parse input for the application ID.
            int id = Integer.parseInt(sc.nextLine()); 
            System.out.print("Member ID: "); 
            int memID = Integer.parseInt(sc.nextLine());
            System.out.print("Pet ID: "); 
            int petID = Integer.parseInt(sc.nextLine());
            System.out.print("Coordinator (Employee) ID: "); 
            int empID = Integer.parseInt(sc.nextLine());
            System.out.print("Date (YYYY-MM-DD): "); 
            String date = sc.nextLine();

            // Create the Java object, defaulting to 'pending' status.
            Application app = new Application(id, memID, petID, empID, date, "pending");
            insertApplication(conn, app);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please ensure IDs are numbers.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: manageApplicationUI
     * Purpose: Provides a submenu to handle status updates or application withdrawal/deletion.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls either updateStatus or withdrawOrDeleteApplication based on user choice.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void manageApplicationUI(Connection conn, Scanner sc) {
        System.out.println("\n1. Update Status (Review/Approve/Reject)");
        System.out.println("2. Withdraw/Delete Application");
        System.out.print("Choice: ");
        String choice = sc.nextLine();

        try {
            if (choice.equals("1")) {
                // Handle updating the application status.
                System.out.print("Application ID: "); 
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("New Status (pending/under review/approved/rejected): "); 
                String status = sc.nextLine();
                updateStatus(conn, id, status); 
            } else if (choice.equals("2")) {
                // Handle withdrawal or deletion based on status.
                System.out.print("Application ID to Remove: "); 
                int id = Integer.parseInt(sc.nextLine());
                withdrawOrDeleteApplication(conn, id); 
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}