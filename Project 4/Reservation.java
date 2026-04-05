import java.sql.*;
import java.util.*;

/**
 * Class Name: Reservation
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages the booking of facility rooms, implementing key business logic 
 * including capacity checking (to prevent double booking) and historical tracking of the member's 
 * tier at the time of booking. It also manages check-in/check-out and conditional deletion (cannot 
 * delete if associated with an order).
 * Public Variables:
 * reservationID: Unique identifier for the reservation.
 * memberID: The member who made the booking.
 * roomID: The facility room reserved.
 * reservationStart: Planned start time of the visit.
 * reservationEnd: Planned end time of the visit.
 * checkIn: Actual check-in time (can be null).
 * checkOut: Actual check-out time (can be null).
 * tierAtTimeOfBooking: Snapshot of the member's tier name for pricing/history.
 * Constructors:
 * Reservation(...): Initializes a new Reservation object.
 * Implemented Methods:
 * insertReservation(Connection, Reservation): Inserts a new reservation, checks room capacity for overlap, and captures the member's tier.
 * updateCheckInOut(Connection, int, String, String): Records the actual check-in or check-out time.
 * deleteReservation(Connection, int): Deletes a reservation only if no food/beverage orders have been placed for the visit.
 * createReservationUI(Connection, Scanner): Collects user input for booking a new reservation.
 * manageReservationUI(Connection, Scanner): Provides a submenu for check-in, check-out, or cancellation.
 */
public class Reservation {
    // Unique reservation identifier.
    public int reservationID;
    // ID of the member who made the booking.
    public int memberID;
    // ID of the room reserved.
    public int roomID;
    // Planned start time.
    public String reservationStart;
    // Planned end time.
    public String reservationEnd;
    // Actual check-in time (can be null).
    public String checkIn;
    // Actual check-out time (can be null).
    public String checkOut;
    // Member tier snapshot for history.
    public String tierAtTimeOfBooking;

    /**
     * Method: Reservation Constructor
     * Purpose: Initializes a new Reservation object in memory.
     * Pre-conditions: Valid input for mandatory fields. Nulls acceptable for checkIn/Out and tierAtTimeOfBooking.
     * Post-conditions: A new Reservation object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int reservationID (In): The ID for the new reservation.
     * int memberID (In): The ID of the member.
     * int roomID (In): The ID of the reserved room.
     * String reservationStart (In): Planned start time.
     * String reservationEnd (In): Planned end time.
     * String checkIn (In): Actual check-in time.
     * String checkOut (In): Actual check-out time.
     * String tierAtTimeOfBooking (In): Member tier name.
     */
    public Reservation(int reservationID, int memberID, int roomID, String reservationStart, 
                       String reservationEnd, String checkIn, String checkOut, String tierAtTimeOfBooking) {
        this.reservationID = reservationID;
        this.memberID = memberID;
        this.roomID = roomID;
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.tierAtTimeOfBooking = tierAtTimeOfBooking;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: insertReservation
     * Purpose: Inserts a new reservation after performing two key checks: 
     * 1. Capacity Check: Ensures the room is not overbooked during the requested time slot.
     * 2. Tier Fetch: Retrieves and stores the member's current tier name for historical tracking.
     * Pre-conditions: Active connection (conn) and a valid Reservation object (r).
     * Post-conditions: A new row is added to the RESERVATION table, or insertion is blocked due to capacity.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Reservation r (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertReservation(Connection conn, Reservation r) throws SQLException {
        // 1. Check Room Capacity Logic
        int maxCap = 0;
        String capSql = "SELECT maxCapacity FROM room WHERE roomID = ?";
        try (PreparedStatement ps = conn.prepareStatement(capSql)) {
            ps.setInt(1, r.roomID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) maxCap = rs.getInt(1);
            else { System.out.println("Invalid Room ID."); return; }
        }

        // Count overlapping reservations using overlap logic: (StartA < EndB) and (EndA > StartB)
        // This counts existing reservations that overlap with the new requested time (r.reservationStart/End).
        String countSql = "SELECT count(*) FROM reservation WHERE roomID = ? " +
                          "AND reservationStart < TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') " +
                          "AND reservationEnd > TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS')";
        
        int currentCount = 0;
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, r.roomID);
            ps.setString(2, r.reservationEnd);
            ps.setString(3, r.reservationStart);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) currentCount = rs.getInt(1);
        }

        if (currentCount >= maxCap) {
            System.out.println("INSERTION BLOCKED: Room " + r.roomID + " is at full capacity (" + maxCap + ") for this time slot.");
            return;
        }

        // 2. Fetch Current Tier Name (History Tracking)
        String tierName = "standard";
        // Join to get the tier name for historical tracking
        String tierSql = "SELECT t.name FROM member m JOIN membershipTier t ON m.tierID = t.tierID WHERE m.memberID = ?";
        try (PreparedStatement ps = conn.prepareStatement(tierSql)) {
            ps.setInt(1, r.memberID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) tierName = rs.getString(1);
            else { System.out.println("Invalid Member ID. Cannot determine tier."); return; }
        }

        // 3. Insert Record
        // checkIn and checkOut are initially set to NULL
        String insertSql = "INSERT INTO reservation VALUES (?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), " +
                           "TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), NULL, NULL, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, r.reservationID);
            ps.setInt(2, r.memberID);
            ps.setInt(3, r.roomID);
            ps.setString(4, r.reservationStart);
            ps.setString(5, r.reservationEnd);
            ps.setString(6, tierName); // Saving the snapshot
            ps.executeUpdate();
            System.out.println("Reservation confirmed for " + tierName + " member.");
        }
    }

    /**
     * Method: updateCheckInOut
     * Purpose: Records the actual check-in or check-out time for a reservation.
     * Pre-conditions: Active connection (conn) and a valid reservation ID (resID).
     * Post-conditions: The checkIn or checkOut timestamp is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int resID (In): The ID of the reservation to update.
     * String type (In): Must be "CheckIn" or "CheckOut" to select the column.
     * String timeStr (In): The timestamp string (YYYY-MM-DD HH24:MI:SS).
     * Throws: SQLException for database errors.
     */
    public static void updateCheckInOut(Connection conn, int resID, String type, String timeStr) throws SQLException {
        // Determine which column to update based on 'type' parameter
        String col = type.equalsIgnoreCase("checkin") ? "checkIn" : "checkOut";
        String sql = "UPDATE reservation SET " + col + " = TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') WHERE reservationID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, timeStr);
            ps.setInt(2, resID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Reservation status updated: " + type + " recorded.");
            else System.out.println("Reservation ID not found.");
        }
    }

    /**
     * Method: deleteReservation
     * Purpose: Deletes a reservation only if no food/beverage orders have been associated with it.
     * Pre-conditions: Active connection (conn) and a valid reservation ID (resID).
     * Post-conditions: The reservation is deleted, or deletion is blocked due to linked orders.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int resID (In): The ID of the reservation to delete.
     * Throws: SQLException for database errors.
     */
    public static void deleteReservation(Connection conn, int resID) throws SQLException {
        // 1. Check for Food Orders linked to this reservation
        String orderCheck = "SELECT count(*) FROM orders WHERE reservationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(orderCheck)) {
            ps.setInt(1, resID);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("CANNOT DELETE: Food/Beverage orders have been placed for this reservation.");
                return;
            }
        }

        // 2. Safe to delete
        String delSql = "DELETE FROM reservation WHERE reservationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(delSql)) {
            ps.setInt(1, resID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Reservation canceled/deleted successfully.");
            else System.out.println("Reservation ID not found.");
        }
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: createReservationUI
     * Purpose: Guides the user through collecting reservation details.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new reservation record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void createReservationUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- New Reservation ---");
            System.out.print("Reservation ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.print("Member ID: "); 
            int memID = Integer.parseInt(sc.nextLine());
            
            System.out.print("Room ID: "); 
            int roomID = Integer.parseInt(sc.nextLine());
            
            System.out.println("Format: YYYY-MM-DD HH:MM:SS (e.g., 2025-12-01 14:00:00)");
            System.out.print("Start Time: "); 
            String start = sc.nextLine();
            
            System.out.print("End Time: "); 
            String end = sc.nextLine();

            // Pass null for checkIn/Out and Tier (tier is fetched automatically in insertReservation)
            Reservation r = new Reservation(id, memID, roomID, start, end, null, null, null);
            insertReservation(conn, r);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: manageReservationUI
     * Purpose: Provides a menu for updating reservation status (check-in/out) or cancellation.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls the appropriate update or delete method.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void manageReservationUI(Connection conn, Scanner sc) {
        System.out.println("\n1. Record Check-In");
        System.out.println("2. Record Check-Out");
        System.out.println("3. Cancel/Delete Reservation");
        System.out.print("Choice: ");
        String choice = sc.nextLine();

        try {
            if (choice.equals("1") || choice.equals("2")) {
                // Handle Check-In / Check-Out
                System.out.print("Reservation ID: "); 
                int id = Integer.parseInt(sc.nextLine());
                
                System.out.print("Time (YYYY-MM-DD HH:MM:SS): "); 
                String time = sc.nextLine();
                
                String type = choice.equals("1") ? "checkin" : "checkout";
                updateCheckInOut(conn, id, type, time);
            
            } else if (choice.equals("3")) {
                // Handle Deletion / Cancellation
                System.out.print("Reservation ID to delete: "); 
                int id = Integer.parseInt(sc.nextLine());
                deleteReservation(conn, id);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}