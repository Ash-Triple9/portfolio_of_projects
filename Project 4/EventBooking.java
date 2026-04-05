import java.sql.*;
import java.util.*;

/**
 * Class Name: EventBooking
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages the booking and attendance records for events. It includes 
 * methods for registering members, checking event capacity before insertion, updating 
 * attendance status (check-in/no-show), and handling cancellations with a conditional 
 * delete policy based on time remaining before the event.
 * Public Variables:
 * bookingID: Unique identifier for the booking record.
 * memberID: ID of the member who made the booking.
 * eventID: ID of the event being booked.
 * bookingDate: Date the booking was created.
 * attendanceStatus: Current state of the booking ('registered', 'attended', 'no-show', 'canceled').
 * paymentStatus: Payment status of the booking ('paid', 'unpaid').
 * Constructors:
 * EventBooking(int, int, int, String, String, String): Initializes a new EventBooking object.
 * Implemented Methods:
 * insertBooking(Connection, EventBooking): Inserts a new booking after checking capacity.
 * updateAttendance(Connection, int, String): Updates the attendance status.
 * cancelOrDeleteBooking(Connection, int): Handles cancellation logic (delete if early, soft-cancel if late).
 * createBookingUI(Connection, Scanner): Collects user input for a new event registration.
 * manageBookingUI(Connection, Scanner): Presents menu for managing check-in or cancellation.
 */
public class EventBooking {
    // Unique identifier for the booking.
    public int bookingID;
    // Customer ID who made the booking.
    public int memberID;
    // Event ID being booked.
    public int eventID;
    // Date the booking was created.
    public String bookingDate;
    // Current attendance state.
    public String attendanceStatus; 
    // Current payment state.
    public String paymentStatus; 

    /**
     * Method: EventBooking Constructor
     * Purpose: Initializes a new EventBooking object in memory.
     * Pre-conditions: Valid input for all booking details.
     * Post-conditions: A new EventBooking object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int bookingID (In): The ID for the new record.
     * int memberID (In): The member reserving the spot.
     * int eventID (In): The event being booked.
     * String bookingDate (In): The date of the booking.
     * String attendanceStatus (In): The initial attendance status (usually 'registered').
     * String paymentStatus (In): The payment status.
     */
    public EventBooking(int bookingID, int memberID, int eventID, String bookingDate, 
                         String attendanceStatus, String paymentStatus) {
        this.bookingID = bookingID;
        this.memberID = memberID;
        this.eventID = eventID;
        this.bookingDate = bookingDate;
        this.attendanceStatus = attendanceStatus;
        this.paymentStatus = paymentStatus;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: insertBooking
     * Purpose: Inserts a new booking record into the database ONLY if the event has not reached its max capacity.
     * Pre-conditions: Active database connection (conn) and a fully populated EventBooking object (eb).
     * Post-conditions: A new row is added to the EVENTBOOKING table, or an error message is printed.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * EventBooking eb (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertBooking(Connection conn, EventBooking eb) throws SQLException {
        // 1. Capacity Check Logic: Find the maximum capacity for this specific event.
        int maxCap = 0;
        String capSql = "SELECT maxCapacity FROM event WHERE eventID = ?";
        try (PreparedStatement ps = conn.prepareStatement(capSql)) {
            ps.setInt(1, eb.eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) maxCap = rs.getInt(1);
            else { System.out.println("Invalid Event ID."); return; }
        }

        // Get current number of active bookings (exclude 'canceled' ones to count actual attendees/reservations).
        String countSql = "SELECT count(*) FROM eventBooking WHERE eventID = ? AND attendanceStatus != 'canceled'";
        int currentCount = 0;
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, eb.eventID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) currentCount = rs.getInt(1);
        }

        // Check if there is space remaining.
        if (currentCount >= maxCap) {
            System.out.println("REGISTRATION FAILED: Event is at full capacity (" + maxCap + ").");
            return;
        }

        // 2. Insert Record, as capacity check passed.
        String sql = "INSERT INTO eventBooking (bookingID, memberID, eventID, bookingDate, attendanceStatus, paymentStatus) " +
                     "VALUES (?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eb.bookingID);
            ps.setInt(2, eb.memberID);
            ps.setInt(3, eb.eventID);
            ps.setString(4, eb.bookingDate);
            ps.setString(5, eb.attendanceStatus); // Initial status is 'registered'.
            ps.setString(6, eb.paymentStatus);
            
            ps.executeUpdate();
            System.out.println("Event registration successful.");
        }
    }

    /**
     * Method: updateAttendance
     * Purpose: Updates the attendance status, primarily used for check-in ('attended') or marking as 'no-show'.
     * Pre-conditions: Active connection (conn), a valid booking ID (bookingID), and a valid new status.
     * Post-conditions: The attendanceStatus column for the specified row is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int bookingID (In): The ID of the booking to modify.
     * String newStatus (In): The status to set ('registered', 'attended', 'no-show', or 'canceled').
     * Throws: SQLException for database errors.
     */
    public static void updateAttendance(Connection conn, int bookingID, String newStatus) throws SQLException {
        // Simple input validation against expected status values.
        if (!newStatus.equalsIgnoreCase("registered") && !newStatus.equalsIgnoreCase("attended") && 
            !newStatus.equalsIgnoreCase("no-show") && !newStatus.equalsIgnoreCase("canceled")) {
            System.out.println("Invalid status. Must be: registered, attended, no-show, canceled");
            return;
        }

        String sql = "UPDATE eventBooking SET attendanceStatus = ? WHERE bookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Attendance status updated to: " + newStatus);
            else System.out.println("Booking ID not found.");
        }
    }

    /**
     * Method: cancelOrDeleteBooking
     * Purpose: Handles the cancellation process. If the event is more than 24 hours away (well in advance), 
     * the booking is hard deleted (implying a full refund). Otherwise, it is marked as 'canceled' to maintain a record.
     * Pre-conditions: Active connection (conn) and a valid booking ID (bookingID).
     * Post-conditions: Either the record is deleted, or the attendance status is set to 'canceled'.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int bookingID (In): The ID of the booking to cancel or delete.
     * Throws: SQLException for database errors.
     */
    public static void cancelOrDeleteBooking(Connection conn, int bookingID) throws SQLException {
        // 1. Get Event Date associated with this booking.
        String checkSql = "SELECT e.eventDate FROM event e JOIN eventBooking b ON e.eventID = b.eventID WHERE b.bookingID = ?";
        Timestamp eventDate = null;
        
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, bookingID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) eventDate = rs.getTimestamp(1);
            else { System.out.println("Booking ID not found."); return; }
        }

        // 2. Check Time Difference (Event Date vs Current Time + 1 day).
        boolean isAdvance = false;
        // This SQL checks if the EventDate is more than 24 hours (1 day) from the current time (SYSDATE).
        String timeDiffSql = "SELECT CASE WHEN ? > (SYSDATE + 1) THEN 1 ELSE 0 END FROM DUAL";
        try (PreparedStatement ps = conn.prepareStatement(timeDiffSql)) {
            ps.setTimestamp(1, eventDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) isAdvance = rs.getInt(1) == 1;
        }

        if (isAdvance) {
            // Case A: Cancellation well in advance -> Hard Delete.
            String delSql = "DELETE FROM eventBooking WHERE bookingID = ?";
            try (PreparedStatement ps = conn.prepareStatement(delSql)) {
                ps.setInt(1, bookingID);
                ps.executeUpdate();
                System.out.println("Booking deleted and refunded (Canceled >24h in advance).");
            }
        } else {
            // Case B: Late cancellation -> Soft Cancel (Mark as 'canceled' to keep the record).
            System.out.println("Too late for full deletion (Event is <24h away). Marking as 'canceled' to maintain history.");
            updateAttendance(conn, bookingID, "canceled");
        }
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: createBookingUI
     * Purpose: Collects all required data from the user via the console and attempts to insert the new booking.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new event booking record, checking capacity first.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void createBookingUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- Register for Event ---");
            System.out.print("Booking ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("Member ID: "); 
            int memID = Integer.parseInt(sc.nextLine());
            System.out.print("Event ID: "); 
            int eventID = Integer.parseInt(sc.nextLine());
            System.out.print("Booking Date (YYYY-MM-DD): "); 
            String date = sc.nextLine();
            
            System.out.print("Payment Status (paid/unpaid): "); 
            String pay = sc.nextLine();

            // Create the Java object, defaulting to 'registered' attendance status.
            EventBooking eb = new EventBooking(id, memID, eventID, date, "registered", pay);
            insertBooking(conn, eb);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: manageBookingUI
     * Purpose: Provides a submenu to handle check-in/status updates or event cancellation.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls either updateAttendance or cancelOrDeleteBooking based on user choice.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void manageBookingUI(Connection conn, Scanner sc) {
        System.out.println("\n1. Check-In (Update Status)");
        System.out.println("2. Cancel Registration");
        System.out.print("Choice: ");
        String choice = sc.nextLine();

        try {
            if (choice.equals("1")) {
                // Option 1: Update attendance status (check-in/no-show).
                System.out.print("Booking ID: "); 
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("New Status (attended/no-show): "); 
                String status = sc.nextLine();
                updateAttendance(conn, id, status);
            } else if (choice.equals("2")) {
                // Option 2: Cancel the booking (conditional delete/soft cancel).
                System.out.print("Booking ID to Cancel: "); 
                int id = Integer.parseInt(sc.nextLine());
                cancelOrDeleteBooking(conn, id);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}