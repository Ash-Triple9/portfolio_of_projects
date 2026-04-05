/*
 * Main.java
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Course: CS460
 * Assignment: Prog4
 * Instructor: Lester McCann
 * TA: James Shen, Utkarsh Upadhyay
 * Due Date: 12/8/2025
 *
 * Description:
 * This program provides a text based user interface for managing pet records
 * in our Oracle aloe database. The program uses JDBC to connect to the database 
 * and execute SQL statements. User input is collected via the console.
 *
 * Operational Requirements:
 * - Java version 17+
 * - Oracle JDBC driver must be included in CLASSPATH, see below.
 *     export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 * - Compile using: javac *.java
 *   This compiles all java files in our source directory
 * - Run using: java Main casonvela a5449
 */
import java.sql.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Check if the correct number of command line arguments (username and password) were provided.
        if (args.length != 2) {
            System.out.println("Usage: java Main casonvela a5449");
            return;
        }

        // Extract the database username and password from the arguments.
        String user = args[0];
        String pass = args[1];

        // Use try with resources to automatically close the database connection and scanner.
        try (Connection conn = DBConnection.getConnection(user, pass);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to Oracle Database successfully!");

            // Main application loop to keep the program running until the user quits.
            while (true) {
                // Display the primary menu options to the user.
                printMainMenu();
                // Read the user's choice from the console.
                String choice = sc.nextLine().trim();

                // Process the user's menu choice.
                switch (choice) {
                    case "1":
                        // Go to the Member management submenu.
                        handleMemberMenu(conn, sc);
                        break;
                    case "2":
                        // Go to the Pet management submenu.
                        handlePetMenu(conn, sc);
                        break;
                    case "3":
                        // Go to the Reservation management submenu.
                        handleReservationMenu(conn, sc);
                        break;
                    case "4":
                        // Go to the Food & Beverage Order management submenu.
                        handleOrderMenu(conn, sc);
                        break;
                    case "5":
                        // Go to the Health Record management submenu.
                        handleHealthRecordMenu(conn, sc);
                        break;
                    case "6":
                        // Go to the Adoption Application management submenu.
                        handleApplicationMenu(conn, sc);
                        break;
                    case "7":
                        // Go to the Event Booking management submenu.
                        handleEventMenu(conn, sc);
                        break;
                    case "8":
                        // Execute the pre defined and custom database queries (Reports).
                        Queries.queriesUI(conn, sc);
                        break;
                    case "q":
                    case "Q":
                        // Exit the application.
                        System.out.println("Exiting Pet Cafe. Thanks for visiting!");
                        return;
                    default:
                        // Handle invalid input for the main menu.
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            // Catch and print any critical database connection or operation errors.
            System.out.println("CRITICAL DB ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Displays the main selection menu to the user.
    private static void printMainMenu() {
        System.out.println("\n=================================");
        System.out.println("   PET CAFE MANAGEMENT SYSTEM    ");
        System.out.println("=================================");
        System.out.println("1. Manage Members");
        System.out.println("2. Manage Pets");
        System.out.println("3. Manage Reservations");
        System.out.println("4. Manage Orders (Food & Drink)");
        System.out.println("5. Manage Health Records");
        System.out.println("6. Manage Adoptions");
        System.out.println("7. Manage Event Bookings");
        System.out.println("8. RUN REPORTS (QUERIES)");
        System.out.println("Q. Quit");
        System.out.print("Select an option: ");
    }

    // Handles the Member management menu and redirects to the appropriate functions in the Member class.
    private static void handleMemberMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Member Management ---");
        System.out.println("1. Register New Member");
        System.out.println("2. Update Member Details");
        System.out.println("3. Delete Member (Strict Check)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1": Member.insertMemberUI(conn, sc); break;
            case "2": Member.updateMemberUI(conn, sc); break;
            case "3": Member.deleteMemberUI(conn, sc); break;
            default: System.out.println("Invalid option.");
        }
    }

    // Handles the Pet management menu and redirects to the appropriate functions in the Pet class.
    private static void handlePetMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Pet Management ---");
        System.out.println("1. Add New Pet");
        System.out.println("2. Update Pet (Name, Status, Temperament)");
        System.out.println("3. Remove Pet (Must be Adopted/Deceased)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1": Pet.insertPetUI(conn, sc); break;
            case "2": Pet.updatePetUI(conn, sc); break;
            case "3": Pet.deletePetUI(conn, sc); break;
            default: System.out.println("Invalid option.");
        }
    }

    // Handles the Reservation management menu and redirects to the appropriate functions in the Reservation class.
    private static void handleReservationMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Reservation Management ---");
        System.out.println("1. Create New Reservation (Capacity Check)");
        System.out.println("2. Manage Reservation (Check-In/Out/Cancel)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) Reservation.createReservationUI(conn, sc);
        else if (choice.equals("2")) Reservation.manageReservationUI(conn, sc);
        else System.out.println("Invalid option.");
    }

    // Handles the Food & Beverage Order management menu and redirects to the appropriate functions in the Order class.
    private static void handleOrderMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Order Management ---");
        System.out.println("1. Create New Order (Add Items)");
        System.out.println("2. Manage Order (Update Status / Delete)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) Order.createOrderUI(conn, sc);
        else if (choice.equals("2")) Order.manageOrderUI(conn, sc);
        else System.out.println("Invalid option.");
    }

    // Handles the Pet Health Record management menu and redirects to the appropriate functions in the HealthRecord class.
    private static void handleHealthRecordMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Health Record Management ---");
        System.out.println("1. Add New Record");
        System.out.println("2. Manage Record (Update / Void)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) HealthRecord.createRecordUI(conn, sc);
        else if (choice.equals("2")) HealthRecord.manageRecordUI(conn, sc);
        else System.out.println("Invalid option.");
    }

    // Handles the Adoption Application management menu and redirects to the appropriate functions in the Application class.
    private static void handleApplicationMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Adoption Application Management ---");
        System.out.println("1. Submit New Application");
        System.out.println("2. Manage Application (Status / Withdraw)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) Application.createApplicationUI(conn, sc);
        else if (choice.equals("2")) Application.manageApplicationUI(conn, sc);
        else System.out.println("Invalid option.");
    }

    // Handles the Event Booking management menu and redirects to the appropriate functions in the EventBooking class.
    private static void handleEventMenu(Connection conn, Scanner sc) {
        System.out.println("\n--- Event Booking Management ---");
        System.out.println("1. Register Member for Event");
        System.out.println("2. Manage Booking (Check-In / Cancel)");
        System.out.print("Choice: ");
        
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) EventBooking.createBookingUI(conn, sc);
        else if (choice.equals("2")) EventBooking.manageBookingUI(conn, sc);
        else System.out.println("Invalid option.");
    }
}
