import java.sql.*;
import java.util.*;

/**
 * Class Name: Member
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages the registration, updates, and deletion of customer members. 
 * The deletion method implements complex business logic, checking for any outstanding 
 * commitments (reservations, pending applications, unpaid orders) before removing the 
 * member and cleaning up all associated records due to foreign key constraints.
 * Public Variables:
 * memberID: Unique identifier for the member.
 * tierID: Membership level identifier.
 * firstName: Member's first name.
 * lastName: Member's last name.
 * phone: Member's primary phone number.
 * email: Member's email address.
 * dateOfBirth: Member's date of birth (used for age verification/records).
 * emergencyContactName: Name of the emergency contact.
 * emergencyContactPhone: Phone number of the emergency contact.
 * Constructors:
 * Member(int, int, String, String, String, String, String, String, String): Initializes a new Member object.
 * Implemented Methods:
 * insertMember(Connection, Member): Inserts a new member record.
 * updateMember(Connection, int, String, String): Updates a member's contact details or membership tier.
 * deleteMember(Connection, int): Performs pre-deletion checks (reservations, orders, applications) and cleans up child records before deletion.
 * checkCount(Connection, String, int): Helper method to execute COUNT queries.
 * insertMemberUI(Connection, Scanner): Collects user input for new member registration.
 * updateMemberUI(Connection, Scanner): Collects user input for member updates.
 * deleteMemberUI(Connection, Scanner): Collects user input for member deletion.
 */
public class Member {
    // Unique identifier for the member.
    public int memberID;
    // Membership tier level.
    public int tierID;
    // Member's first name.
    public String firstName;
    // Member's last name.
    public String lastName;
    // Member's primary phone number.
    public String phone;
    // Member's email address.
    public String email;
    // Date of Birth (YYYY-MM-DD).
    public String dateOfBirth; 
    // Emergency contact name.
    public String emergencyContactName;
    // Emergency contact phone.
    public String emergencyContactPhone;

    /**
     * Method: Member Constructor
     * Purpose: Initializes a new Member object in memory.
     * Pre-conditions: Valid input for all member details.
     * Post-conditions: A new Member object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int memberID (In): The ID for the new member.
     * int tierID (In): The membership tier ID.
     * String firstName (In): Member's first name.
     * String lastName (In): Member's last name.
     * String phone (In): Member's phone number.
     * String email (In): Member's email address.
     * String dateOfBirth (In): Member's date of birth.
     * String emergencyContactName (In): Emergency contact's name.
     * String emergencyContactPhone (In): Emergency contact's phone.
     */
    public Member(int memberID, int tierID, String firstName, String lastName, String phone,
                  String email, String dateOfBirth, String emergencyContactName, String emergencyContactPhone) {
        this.memberID = memberID;
        this.tierID = tierID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: insertMember
     * Purpose: Inserts a new member record into the database.
     * Pre-conditions: Active database connection (conn) and a fully populated Member object (m).
     * Post-conditions: A new row is added to the MEMBER table.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Member m (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertMember(Connection conn, Member m) throws SQLException {
        // SQL query to insert member data, converting DOB string to a TIMESTAMP.
        String sql = "INSERT INTO member (memberID, tierID, firstName, lastName, phone, email, dateOfBirth, emergencyContactName, emergencyContactPhone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.memberID);
            ps.setInt(2, m.tierID);
            ps.setString(3, m.firstName);
            ps.setString(4, m.lastName);
            ps.setString(5, m.phone);
            ps.setString(6, m.email);
            ps.setString(7, m.dateOfBirth);
            ps.setString(8, m.emergencyContactName);
            ps.setString(9, m.emergencyContactPhone);
            
            ps.executeUpdate();
            System.out.println("Member added successfully.");
        }
    }

    /**
     * Method: updateMember
     * Purpose: Updates a single specified field (contact details or tier ID) for a member.
     * Pre-conditions: Active connection (conn), a valid memberID, and a whitelisted column name.
     * Post-conditions: The specified column for the member is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int memberID (In): The ID of the member to modify.
     * String colName (In): The column to update (e.g., 'email', 'tierID').
     * String newValue (In): The new value for the column.
     * Throws: SQLException for database errors.
     */
    public static void updateMember(Connection conn, int memberID, String colName, String newValue) throws SQLException {
        // Whitelist columns to prevent SQL injection vulnerabilities.
        if (!colName.equalsIgnoreCase("phone") && !colName.equalsIgnoreCase("email") && 
            !colName.equalsIgnoreCase("tierID") && !colName.equalsIgnoreCase("emergencyContactName")) {
            System.out.println("Invalid column for update. You can only update: phone, email, tierID, emergencyContactName");
            return;
        }

        // Construct the query using the validated column name.
        String sql = "UPDATE member SET " + colName + " = ? WHERE memberID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Handle integer vs. string types for setting the parameter.
            if (colName.equalsIgnoreCase("tierID")) {
                ps.setInt(1, Integer.parseInt(newValue));
            } else {
                ps.setString(1, newValue);
            }
            ps.setInt(2, memberID);
            
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Member updated successfully.");
            else System.out.println("Member ID not found.");
        }
    }

    /**
     * Method: deleteMember
     * Purpose: Deletes a member only after checking for and cleaning up all related active and historical records.
     * Pre-conditions: Active connection (conn) and a valid memberID.
     * Post-conditions: The member record and all associated child records are deleted from the database.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int memberID (In): The ID of the member to delete.
     * Throws: SQLException for database errors.
     */
    public static void deleteMember(Connection conn, int memberID) throws SQLException {
        // 1. Check for Active Reservations: Future bookings or currently checked-in animals.
        String resCheck = "SELECT count(*) FROM reservation WHERE memberID = ? AND (reservationStart > SYSDATE OR (checkIn IS NOT NULL AND checkOut IS NULL))";
        if (checkCount(conn, resCheck, memberID) > 0) {
            System.out.println("CANNOT DELETE: Member has active or future reservations.");
            return;
        }

        // 2. Check for Pending Adoption Applications: Applications that are still being reviewed.
        String appCheck = "SELECT count(*) FROM application WHERE memberID = ? AND status IN ('pending', 'under review')";
        if (checkCount(conn, appCheck, memberID) > 0) {
            System.out.println("CANNOT DELETE: Member has pending adoption applications.");
            return;
        }

        // 3. Check for Unpaid Orders: Orders that are not marked 'complete' or still have a balance owing.
        String orderCheck = "SELECT count(*) FROM orders WHERE memberID = ? AND (status != 'complete' OR amountOwing > 0)";
        if (checkCount(conn, orderCheck, memberID) > 0) {
            System.out.println("CANNOT DELETE: Member has unpaid or incomplete food orders.");
            return;
        }

        // 4. If all checks pass, clean up all history records related to this member.
        System.out.println("Checks passed. Cleaning up history records to satisfy FK constraints...");
        
        // Manual deletion of child records in reverse order of foreign key dependency.
        conn.createStatement().executeUpdate("DELETE FROM eventBooking WHERE memberID = " + memberID);
        // OrderLine must be deleted before Orders.
        conn.createStatement().executeUpdate("DELETE FROM orderLine WHERE ordersID IN (SELECT ordersID FROM orders WHERE memberID = " + memberID + ")");
        conn.createStatement().executeUpdate("DELETE FROM orders WHERE memberID = " + memberID);
        conn.createStatement().executeUpdate("DELETE FROM reservation WHERE memberID = " + memberID);
        conn.createStatement().executeUpdate("DELETE FROM application WHERE memberID = " + memberID);

        // Finally, delete the parent member record.
        String delSql = "DELETE FROM member WHERE memberID = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setInt(1, memberID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Member and all associated history deleted successfully.");
            else System.out.println("Member ID not found.");
        }
    }

    /**
     * Method: checkCount
     * Purpose: Helper method to efficiently run a prepared statement that returns a single count value.
     * Pre-conditions: Active connection, a COUNT SQL statement, and the ID parameter.
     * Post-conditions: Executes the query and returns the resulting count.
     * Return: int - The count of rows returned by the query.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * String sql (In): The SQL query containing a single COUNT(*) and one '?' placeholder.
     * int id (In): The ID to substitute into the query.
     * Throws: SQLException for database errors.
     */
    private static int checkCount(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: insertMemberUI
     * Purpose: Guides the user through entering all details required to register a new member.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new member record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void insertMemberUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- Register New Member ---");
            System.out.print("Member ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            // Display tiers to help the user select the correct ID.
            System.out.println("Available Tiers: 1-Day Pass, 2-Weekly, 3-Monthly, 4-Annual, 5-Premium");
            System.out.print("Tier ID: "); 
            int tier = Integer.parseInt(sc.nextLine());
            
            System.out.print("First Name: "); 
            String fn = sc.nextLine();
            System.out.print("Last Name: "); 
            String ln = sc.nextLine();
            System.out.print("Phone: "); 
            String phone = sc.nextLine();
            System.out.print("Email: "); 
            String email = sc.nextLine();
            System.out.print("DOB (YYYY-MM-DD): "); 
            String dob = sc.nextLine();
            System.out.print("Emergency Contact Name: "); 
            String eName = sc.nextLine();
            System.out.print("Emergency Contact Phone: "); 
            String ePhone = sc.nextLine();

            Member m = new Member(id, tier, fn, ln, phone, email, dob, eName, ePhone);
            insertMember(conn, m);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: updateMemberUI
     * Purpose: Guides the user through selecting a member and a specific field to update.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to update the member's details.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void updateMemberUI(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Member ID to update: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.println("Fields you can update: phone, email, tierID, emergencyContactName");
            System.out.print("Which field to update? "); 
            String col = sc.nextLine();
            
            System.out.print("Enter new value: "); 
            String val = sc.nextLine();
            
            updateMember(conn, id, col, val);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Method: deleteMemberUI
     * Purpose: Guides the user through deleting a member, triggering the complex deletion logic.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to delete the member and associated records.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void deleteMemberUI(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Member ID to delete: "); 
            int id = Integer.parseInt(sc.nextLine());
            deleteMember(conn, id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}