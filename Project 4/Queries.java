import java.sql.*;
import java.util.*;

/**
 * Class Name: Queries
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class contains static methods to run non-trivial, multi-table queries 
 * requested by the business logic, providing summarized or filtered views of the data 
 * such as adoption history, customer spending, event availability, and pet location.
 * Implemented Methods:
 * queryPetAdoptions(Connection, Scanner): Lists all adoption applications for a single pet.
 * queryCustomerHistory(Connection, Scanner): Summarizes a member's visits, spending, and tier usage.
 * queryUpcomingEvents(Connection): Lists future events that still have available spots.
 * queryPetsBySpecies(Connection, Scanner): Lists pets of a specific species that are currently available for reservation/adoption.
 * queriesUI(Connection, Scanner): Presents a menu interface for running the four queries.
 */
public class Queries {

    // --- 1. Pet Adoption History ---
    /**
     * Method: queryPetAdoptions
     * Purpose: Lists all adoption applications for a given pet ID, showing the applicant, 
     * the application date, current status, and the coordinator handling the application.
     * Pre-conditions: Active connection (conn). User provides a valid petID.
     * Post-conditions: Prints formatted output listing the application history.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     * Throws: Exception for input or SQL errors.
     */
    public static void queryPetAdoptions(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Pet ID to view history: ");
            int petID = Integer.parseInt(sc.nextLine());

            // SQL: Join Application -> Member (applicant) -> Employee (coordinator)
            // Order by date descending to see most recent applications first.
            String sql = "SELECT m.firstName || ' ' || m.lastName AS applicantName, " +
                         "       a.applicationDate, " +
                         "       a.status, " +
                         "       e.firstName || ' ' || e.lastName AS coordinatorName " +
                         "FROM application a " +
                         "JOIN member m ON a.memberID = m.memberID " +
                         "JOIN employee e ON a.employeeID = e.employeeID " +
                         "WHERE a.petID = ? " +
                         "ORDER BY a.applicationDate DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, petID);
                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n--- Adoption Application History for Pet " + petID + " ---");
                    System.out.printf("%-25s %-22s %-15s %-25s\n", "Applicant", "Date", "Status", "Coordinator");
                    System.out.println("-----------------------------------------------------------------------------------------");
                    
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.printf("%-25s %-22s %-15s %-25s\n",
                                rs.getString("applicantName"),
                                rs.getTimestamp("applicationDate"),
                                rs.getString("status"),
                                rs.getString("coordinatorName"));
                    }

                    if (!found) System.out.println("No applications found for this pet.");
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- 2. Customer Visit History ---
    /**
     * Method: queryCustomerHistory
     * Purpose: Provides a detailed history of a member's facility visits, including the 
     * room reserved, the membership tier active at the time of booking, the number of 
     * food orders placed during that visit, and the total amount spent on orders.
     * Pre-conditions: Active connection (conn). User provides a valid memberID.
     * Post-conditions: Prints formatted table showing visit and spending history.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     * Throws: Exception for input or SQL errors.
     */
    public static void queryCustomerHistory(Connection conn, Scanner sc) {
        // 
        try {
            System.out.print("Enter Member ID: ");
            int memID = Integer.parseInt(sc.nextLine());

            // SQL: Join Reservation -> Room. Use correlated subqueries to calculate aggregate data 
            // (order count and total spending) specific to each reservation.
            String sql = "SELECT r.reservationID, " +
                         "       r.reservationStart, " +
                         "       rm.roomName, " +
                         "       r.tierAtTimeOfBooking, " +
                         "       (SELECT COUNT(*) FROM orders o WHERE o.reservationID = r.reservationID) as orderCount, " +
                         "       (SELECT NVL(SUM(amountOwing), 0) FROM orders o WHERE o.reservationID = r.reservationID) as totalSpent " +
                         "FROM reservation r " +
                         "JOIN room rm ON r.roomID = rm.roomID " +
                         "WHERE r.memberID = ? " +
                         "ORDER BY r.reservationStart DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memID);
                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n--- Visit History for Member " + memID + " ---");
                    System.out.printf("%-22s %-20s %-15s %-15s %-10s\n", "Date", "Room", "Tier Used", "Orders Placed", "Total Spent");
                    System.out.println("------------------------------------------------------------------------------------------");

                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        System.out.printf("%-22s %-20s %-15s %-15d $%-9.2f\n",
                                rs.getTimestamp("reservationStart"),
                                rs.getString("roomName"),
                                rs.getString("tierAtTimeOfBooking"),
                                rs.getInt("orderCount"),
                                rs.getDouble("totalSpent"));
                    }

                    if (!found) System.out.println("No visit history found for this member.");
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- 3. Upcoming Events with Capacity ---
    /**
     * Method: queryUpcomingEvents
     * Purpose: Lists all future events, their location, coordinator, and current 
     * attendance count versus maximum capacity, only showing events that have available spots.
     * Pre-conditions: Active connection (conn).
     * Post-conditions: Prints formatted table of available upcoming events.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Throws: Exception for SQL errors.
     */
    public static void queryUpcomingEvents(Connection conn) {
        try {
            // SQL: Join Event -> Room -> Employee. Use correlated subquery to count active event bookings.
            // Filter: Event date is in the future. Filtering for capacity (currentAttendees < maxCapacity) is done in Java.
            String sql = "SELECT e.eventName, " +
                         "       e.eventDate, " +
                         "       rm.roomName, " +
                         "       e.maxCapacity, " +
                         "       (SELECT COUNT(*) FROM eventBooking eb WHERE eb.eventID = e.eventID AND eb.attendanceStatus != 'canceled') as currentAttendees, " +
                         "       emp.firstName || ' ' || emp.lastName as coordinator " +
                         "FROM event e " +
                         "JOIN room rm ON e.roomID = rm.roomID " +
                         "JOIN employee emp ON e.employeeID = emp.employeeID " +
                         "WHERE e.eventDate > SYSDATE"; // Only future events

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("\n--- Upcoming Events with Availability ---");
                System.out.printf("%-20s %-22s %-20s %-10s %-20s\n", "Event", "Date", "Location", "Spots", "Coordinator");
                System.out.println("---------------------------------------------------------------------------------------------------");

                boolean found = false;
                while (rs.next()) {
                    int max = rs.getInt("maxCapacity");
                    int current = rs.getInt("currentAttendees");
                    
                    // Filter logic in Java: Only show if spots are available
                    if (current < max) {
                        found = true;
                        String spotsStr = current + "/" + max;
                        System.out.printf("%-20s %-22s %-20s %-10s %-20s\n",
                                rs.getString("eventName"),
                                rs.getTimestamp("eventDate"),
                                rs.getString("roomName"),
                                spotsStr,
                                rs.getString("coordinator"));
                    }
                }

                if (!found) System.out.println("No upcoming events with available capacity.");
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- 4. Custom Non-Trivial Query: Available Pets by Species and Room ---
    /**
     * Method: queryPetsBySpecies
     * Purpose: Lists all pets of a user-specified species that are currently marked 
     * as 'available'. Also displays their breed, temperament, and current room location.
     * Pre-conditions: Active connection (conn). User provides a species name.
     * Post-conditions: Prints formatted table of available pets matching the criteria.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     * Throws: Exception for input or SQL errors.
     */
    public static void queryPetsBySpecies(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter Species (e.g., Dog, Cat, Rabbit): ");
            String species = sc.nextLine().trim();

            // SQL: Join Pet -> Breed (to filter species) -> Room (to show location)
            // Filter: Species match AND status is 'available'.
            String sql = "SELECT p.petID, " +
                         "       p.name, " +
                         "       b.breedName, " +
                         "       r.roomName, " +
                         "       p.temperament " +
                         "FROM pet p " +
                         "JOIN breed b ON p.breedID = b.breedID " +
                         "LEFT JOIN room r ON p.roomID = r.roomID " + // Left join for pets not yet assigned a room
                         "WHERE LOWER(b.species) = LOWER(?) " +
                         "AND p.status = 'available' " +
                         "ORDER BY r.roomName, p.name";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, species);
                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n--- Available " + species + "s for Reservation ---");
                    System.out.printf("%-5s %-15s %-20s %-20s %-15s\n", "ID", "Name", "Breed", "Room", "Temperament");
                    System.out.println("--------------------------------------------------------------------------------");

                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String room = rs.getString("roomName");
                        // Handle potential null roomID due to LEFT JOIN
                        if (room == null) room = "Unassigned"; 
                        
                        System.out.printf("%-5d %-15s %-20s %-20s %-15s\n",
                                rs.getInt("petID"),
                                rs.getString("name"),
                                rs.getString("breedName"),
                                room,
                                rs.getString("temperament"));
                    }

                    if (!found) System.out.println("No available " + species + "s found.");
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- UI Menu ---
    /**
     * Method: queriesUI
     * Purpose: Provides the main menu for executing the predefined system queries.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls the selected query method.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void queriesUI(Connection conn, Scanner sc) {
        System.out.println("\n[System Queries]");
        System.out.println("1. View Adoption History for a Pet");
        System.out.println("2. View Customer Visit History");
        System.out.println("3. List Upcoming Available Events");
        System.out.println("4. Find Available Pets by Species");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine();
        
        switch (choice) {
            case "1": queryPetAdoptions(conn, sc); break;
            case "2": queryCustomerHistory(conn, sc); break;
            case "3": queryUpcomingEvents(conn); break;
            case "4": queryPetsBySpecies(conn, sc); break;
            default: System.out.println("Invalid choice.");
        }
    }
}