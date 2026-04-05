import java.sql.*;
import java.util.*;

/**
 * Class Name: HealthRecord
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages the medical and administrative health records for pets. It includes 
 * methods for creating new records, updating descriptive information, and implementing a strict 
 * policy against hard deletion by instead marking records as 'void' or 'corrected' with mandatory 
 * explanations for compliance and legal reasons.
 * Public Variables:
 * recordID: Unique identifier for the health record.
 * petID: ID of the pet the record belongs to.
 * employeeID: ID of the staff member (e.g., vet) who created the record.
 * recordDate: Date the procedure/checkup occurred.
 * recordType: Type of record (e.g., 'Vaccination', 'Surgery', 'Checkup').
 * description: Detailed notes about the procedure/findings.
 * notes: Explanation for status changes (void/corrected) or follow up details.
 * nextDueDate: Date of next required action (e.g., next vaccine).
 * status: Record state ('active', 'void', 'corrected').
 * Constructors:
 * HealthRecord(int, int, int, String, String, String, String, String, String): Initializes a new HealthRecord object.
 * Implemented Methods:
 * insertHealthRecord(Connection, HealthRecord): Inserts a new record, defaulting status to 'active'.
 * updateHealthRecord(Connection, int, String, String): Updates the main description and next due date.
 * voidHealthRecord(Connection, int, String): Changes the status to 'void' and requires an explanation note.
 * createRecordUI(Connection, Scanner): Collects user input for a new health record.
 * manageRecordUI(Connection, Scanner): Presents menu for updating or voiding a record.
 */
public class HealthRecord {
    // Unique identifier for the record.
    public int recordID;
    // Pet this record belongs to.
    public int petID;
    // Staff member (vet/tech) who created the record.
    public int employeeID;
    // Date the health event occurred.
    public String recordDate;
    // Type of event (e.g., Vaccination, Checkup).
    public String recordType;
    // Detailed notes on the procedure/finding.
    public String description;
    // Explanatory note for corrections or voiding.
    public String notes; 
    // Date of next required follow-up.
    public String nextDueDate;
    // Status ('active', 'void', 'corrected').
    public String status; 

    /**
     * Method: HealthRecord Constructor
     * Purpose: Initializes a new HealthRecord object in memory.
     * Pre-conditions: Valid input for all health record details.
     * Post-conditions: A new HealthRecord object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int recordID (In): The ID for the new record.
     * int petID (In): The pet identifier.
     * int employeeID (In): The staff member identifier.
     * String recordDate (In): The date of the procedure.
     * String recordType (In): The type of procedure/record.
     * String description (In): The detailed description of the event.
     * String notes (In): Any supplementary notes (often null initially).
     * String nextDueDate (In): Date of next follow-up.
     * String status (In): The initial status.
     */
    public HealthRecord(int recordID, int petID, int employeeID, String recordDate, String recordType,
                        String description, String notes, String nextDueDate, String status) {
        this.recordID = recordID;
        this.petID = petID;
        this.employeeID = employeeID;
        this.recordDate = recordDate;
        this.recordType = recordType;
        this.description = description;
        this.notes = notes;
        this.nextDueDate = nextDueDate;
        this.status = status;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: insertHealthRecord
     * Purpose: Inserts a new health record into the database. Status is always set to 'active' upon creation.
     * Pre-conditions: Active database connection (conn) and a fully populated HealthRecord object (hr).
     * Post-conditions: A new row is added to the HEALTHRECORD table.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * HealthRecord hr (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertHealthRecord(Connection conn, HealthRecord hr) throws SQLException {
        // SQL query to insert all fields, converting date strings to TIMESTAMPs.
        String sql = "INSERT INTO healthRecord (recordID, petID, employeeID, recordDate, recordType, description, notes, nextDueDate, status) " +
                     "VALUES (?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hr.recordID);
            ps.setInt(2, hr.petID);
            ps.setInt(3, hr.employeeID);
            ps.setString(4, hr.recordDate);
            ps.setString(5, hr.recordType);
            ps.setString(6, hr.description);
            ps.setString(7, hr.notes);
            
            // Handle optional 'nextDueDate' field; set as NULL in DB if input is blank.
            if (hr.nextDueDate == null || hr.nextDueDate.isBlank()) ps.setNull(8, Types.TIMESTAMP);
            else ps.setString(8, hr.nextDueDate);
            
            ps.setString(9, "active"); // Default status is always active on insert
            
            ps.executeUpdate();
            System.out.println("Health record added successfully.");
        }
    }

    /**
     * Method: updateHealthRecord
     * Purpose: Allows staff to update the main details of an active health record, such as descriptions or follow-up dates.
     * Pre-conditions: Active connection (conn), a valid record ID (recordID), new description, and an optional next due date.
     * Post-conditions: The description and/or nextDueDate fields for the specified row are updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int recordID (In): The ID of the record to modify.
     * String newDescription (In): The updated details.
     * String nextDue (In): The updated follow-up date (can be null/blank).
     * Throws: SQLException for database errors.
     */
    public static void updateHealthRecord(Connection conn, int recordID, String newDescription, String nextDue) throws SQLException {
        // Update the description and the nextDueDate field.
        String sql = "UPDATE healthRecord SET description = ?, nextDueDate = TO_TIMESTAMP(?, 'YYYY-MM-DD') WHERE recordID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newDescription);
            
            // Set nextDueDate as NULL if the input is empty.
            if (nextDue == null || nextDue.isBlank()) ps.setNull(2, Types.TIMESTAMP);
            else ps.setString(2, nextDue);
            
            ps.setInt(3, recordID);
            
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Health record updated.");
            else System.out.println("Record ID not found.");
        }
    }

    /**
     * Method: voidHealthRecord
     * Purpose: Implements the policy that health records cannot be deleted. Instead, it marks the record 
     * status as 'void' and requires a mandatory explanatory note.
     * Pre-conditions: Active connection (conn), a valid record ID (recordID), and a non-empty explanation string.
     * Post-conditions: The status is set to 'void' and the explanation is stored in the 'notes' column.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int recordID (In): The ID of the record to void.
     * String explanation (In): Mandatory text explaining why the record is being voided.
     * Throws: SQLException for database errors.
     */
    public static void voidHealthRecord(Connection conn, int recordID, String explanation) throws SQLException {
        // Change the status to 'void' and update the notes field with the explanation.
        String sql = "UPDATE healthRecord SET status = 'void', notes = ? WHERE recordID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, explanation); // The mandatory explanatory note
            ps.setInt(2, recordID);
            
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Record marked as VOID. Explanation saved.");
            else System.out.println("Record ID not found.");
        }
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: createRecordUI
     * Purpose: Guides the user through entering all necessary information to create a new health record.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new health record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void createRecordUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- New Health Record ---");
            System.out.print("Record ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("Pet ID: "); 
            int petID = Integer.parseInt(sc.nextLine());
            System.out.print("Vet Staff ID: "); 
            int empID = Integer.parseInt(sc.nextLine());
            System.out.print("Date (YYYY-MM-DD): "); 
            String date = sc.nextLine();
            System.out.print("Type (Vaccination, Checkup, Grooming, etc): "); 
            String type = sc.nextLine();
            System.out.print("Description: "); 
            String desc = sc.nextLine();
            System.out.print("Next Due Date (YYYY-MM-DD) or blank: "); 
            String next = sc.nextLine();

            // Status is active by default, notes are null initially.
            HealthRecord hr = new HealthRecord(id, petID, empID, date, type, desc, null, next, "active");
            insertHealthRecord(conn, hr);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: manageRecordUI
     * Purpose: Provides a submenu to handle updating existing record details or voiding a record.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls either updateHealthRecord or voidHealthRecord based on user choice.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void manageRecordUI(Connection conn, Scanner sc) {
        System.out.println("\n1. Update Details (Description/Date)");
        System.out.println("2. Void a Record (Delete)");
        System.out.print("Choice: ");
        String choice = sc.nextLine();

        try {
            if (choice.equals("1")) {
                // Handle updating the record's primary fields.
                System.out.print("Record ID: "); 
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("New Description: "); 
                String desc = sc.nextLine();
                System.out.print("New Next Due Date (YYYY-MM-DD): "); 
                String date = sc.nextLine();
                updateHealthRecord(conn, id, desc, date);
            } else if (choice.equals("2")) {
                // Handle voiding the record, which requires an explanation.
                System.out.print("Record ID to Void: "); 
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Reason for voiding (Mandatory): "); 
                String reason = sc.nextLine();
                
                // Check if the mandatory reason was provided.
                if (reason.isBlank()) {
                    System.out.println("Error: Explanation note is required to void a legal record.");
                    return;
                }
                voidHealthRecord(conn, id, reason);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}