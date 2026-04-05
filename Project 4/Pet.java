import java.sql.*;
import java.util.*;


/**
 * Class Name: Pet
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages the data for individual pets in the animal care facility. 
 * It includes CRUD operations with strict business logic, particularly for deletion, 
 * which requires checking for pending applications and verifying the pet's final status 
 * ('adopted' or 'deceased') before removal.
 * Public Variables:
 * petID: Unique identifier for the pet.
 * roomID: The room where the pet is housed (can be null).
 * breedID: Reference to the pet's breed.
 * name: Pet's name.
 * age: Pet's age (can be null if unknown).
 * dateOfArrival: Date the pet arrived at the facility.
 * specialNeeds: Notes on any special medical or behavioral needs.
 * temperament: Description of the pet's nature (e.g., friendly, shy).
 * status: Current state ('in care', 'available', 'adoption', 'adopted', 'deceased').
 * Constructors:
 * Pet(...): Initializes a new Pet object.
 * Implemented Methods:
 * insertPet(Connection, Pet): Inserts a new pet record.
 * updatePet(Connection, int, String, Integer, String, String): Updates core details like name, age, temperament, and status.
 * deletePet(Connection, int): Deletes a pet only if all safety checks (status and no pending applications) pass.
 * insertPetUI(Connection, Scanner): Collects user input for new pet registration.
 * updatePetUI(Connection, Scanner): Collects user input for pet updates.
 * deletePetUI(Connection, Scanner): Collects user input for pet deletion.
 */
public class Pet {
    // Unique pet identifier.
    public int petID; 
    // Room where the pet is currently located (optional).
    public Integer roomID; 
    // Reference to breed.
    public int breedID; 
    // Pet name.
    public String name; 
    // Pet age (optional).
    public Integer age; 
    // Arrival date.
    public String dateOfArrival; 
    // Special requirements.
    public String specialNeeds; 
    // Friendly, shy, loud, etc.
    public String temperament; 
    // Pet status.
    public String status; 

    /**
     * Method: Pet Constructor
     * Purpose: Initializes a new Pet object in memory.
     * Pre-conditions: Valid input for all pet details. Nulls acceptable for roomID and age.
     * Post-conditions: A new Pet object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int petID (In): The ID for the new pet.
     * Integer roomID (In): The room ID.
     * int breedID (In): The breed ID.
     * String name (In): The pet's name.
     * Integer age (In): The pet's age.
     * String dateOfArrival (In): The date the pet arrived.
     * String specialNeeds (In): Notes on special needs.
     * String temperament (In): Description of temperament.
     * String status (In): Initial status.
     */
    public Pet(int petID, Integer roomID, int breedID, String name, Integer age,
               String dateOfArrival, String specialNeeds, String temperament, String status) {
        this.petID = petID;
        this.roomID = roomID;
        this.breedID = breedID;
        this.name = name;
        this.age = age;
        this.dateOfArrival = dateOfArrival;
        this.specialNeeds = specialNeeds;
        this.temperament = temperament;
        this.status = status;
    }

    // ----------------------
    // SQL CRUD Operations
    // ----------------------

    /**
     * Method: insertPet
     * Purpose: Inserts a new pet record into the PET table.
     * Pre-conditions: Active database connection (conn) and a fully populated Pet object (p).
     * Post-conditions: A new row is added to the PET table.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Pet p (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void insertPet(Connection conn, Pet p) throws SQLException {
        String sql = "INSERT INTO pet VALUES (?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD'), ?, ?, ?)";
        // Using try-with-resources for automatic PreparedStatement closing.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.petID);
            // Handle optional Integer fields (roomID, age).
            if (p.roomID == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, p.roomID);
            ps.setInt(3, p.breedID);
            ps.setString(4, p.name);
            if (p.age == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.age);
            ps.setString(6, p.dateOfArrival);
            ps.setString(7, p.specialNeeds);
            ps.setString(8, p.temperament);
            ps.setString(9, p.status);
            
            ps.executeUpdate();
            System.out.println("Pet inserted successfully.");
        }
    }

    /**
     * Method: updatePet
     * Purpose: Updates key descriptive and status information for an existing pet.
     * Pre-conditions: Active connection (conn) and a valid petID.
     * Post-conditions: The specified pet's details are updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int petID (In): The ID of the pet to modify.
     * String name (In): New name.
     * Integer age (In): New age.
     * String temperament (In): New temperament notes.
     * String status (In): New status.
     * Throws: SQLException for database errors.
     */
    public static void updatePet(Connection conn, int petID, String name, Integer age, String temperament, String status) throws SQLException {
        String sql = "UPDATE pet SET name = ?, age = ?, temperament = ?, status = ? WHERE petID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            if (age == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, age);
            ps.setString(3, temperament);
            ps.setString(4, status);
            ps.setInt(5, petID);
            
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No pet found with that ID.");
            else System.out.println("Pet updated successfully.");
        }
    }

    /**
     * Method: deletePet
     * Purpose: Deletes a pet record only if two critical conditions are met:
     * 1. The pet has no pending (non-withdrawn) adoption applications.
     * 2. The pet's status is permanently concluded ('adopted' or 'deceased').
     * Pre-conditions: Active connection (conn) and a valid petID.
     * Post-conditions: The pet record is deleted, or a business rule violation message is printed.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int petID (In): The ID of the pet to delete.
     * Throws: SQLException for database errors.
     */
    public static void deletePet(Connection conn, int petID) throws SQLException {
        
        // 1. Check for pending adoption applications.
        String checkAppSql = "SELECT COUNT(*) FROM application WHERE petID = ? AND status != 'withdrawn'";
        int pendingApps = 0;
        try (PreparedStatement checkStmt = conn.prepareStatement(checkAppSql)) {
            checkStmt.setInt(1, petID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) pendingApps = rs.getInt(1);
            }
        }

        if (pendingApps > 0) {
            System.out.println("Cannot delete pet: " + pendingApps + " pending adoption application(s) exist.");
            return;
        }

        // 2. Check current pet status (must be 'adopted' or 'deceased').
        String currentStatus = null;
        String statusCheckSql = "SELECT status FROM pet WHERE petID = ?";
        try (PreparedStatement statusStmt = conn.prepareStatement(statusCheckSql)) {
            statusStmt.setInt(1, petID);
            try (ResultSet statusRS = statusStmt.executeQuery()) {
                if (statusRS.next()) {
                    currentStatus = statusRS.getString("status");
                } else {
                    System.out.println("No pet found with that ID.");
                    return;
                }
            }
        }
        
        if (currentStatus != null && !currentStatus.equals("adopted") && !currentStatus.equals("deceased")) {
            System.out.println("Cannot delete pet: status must be 'adopted' or 'deceased'. Current status is '" + currentStatus + "'.");
            return;
        }

        // 3. Delete pet if checks pass.
        String sql = "DELETE FROM pet WHERE petID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, petID);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("No pet deleted (or not found).");
            else System.out.println("Pet deleted successfully.");
        }
    }

    // ----------------------
    // UI Methods
    // ----------------------

    /**
     * Method: insertPetUI
     * Purpose: Guides the user through collecting all necessary information to create a new pet record.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new pet record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void insertPetUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- Register New Pet ---");
            System.out.print("Pet ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.print("Room ID (or blank): "); 
            String r = sc.nextLine(); 
            Integer room = r.isBlank() ? null : Integer.parseInt(r);
            
            System.out.print("Breed ID: "); 
            int breed = Integer.parseInt(sc.nextLine());
            
            System.out.print("Name: "); 
            String name = sc.nextLine();
            
            System.out.print("Age (or blank): "); 
            String a = sc.nextLine(); 
            Integer age = a.isBlank() ? null : Integer.parseInt(a);
            
            System.out.print("Date of Arrival (YYYY-MM-DD) or blank for today: "); 
            String doa = sc.nextLine();
            if (doa.isBlank()) doa = java.time.LocalDate.now().toString();
            
            System.out.print("Special Needs: "); 
            String sn = sc.nextLine();
            
            System.out.print("Temperament: "); 
            String temp = sc.nextLine();
            
            System.out.print("Status (in care/available/adoption/adopted/deceased): "); 
            String status = sc.nextLine();

            Pet p = new Pet(id, room, breed, name, age, doa, sn, temp, status);
            insertPet(conn, p);
        } catch (Exception e) {
            System.out.println("Input or SQL error: " + e.getMessage());
        }
    }

    /**
     * Method: updatePetUI
     * Purpose: Guides the user through collecting update information for an existing pet.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to update the pet record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void updatePetUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- Update Pet Details ---");
            System.out.print("Pet ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.print("New Name: "); 
            String name = sc.nextLine();
            
            System.out.print("New Age (or blank): "); 
            String a = sc.nextLine(); 
            Integer age = a.isBlank() ? null : Integer.parseInt(a);
            
            System.out.print("New Temperament: "); 
            String temp = sc.nextLine();
            
            System.out.print("New Status (in care/available/adoption/adopted/deceased): "); 
            String status = sc.nextLine();
            
            updatePet(conn, id, name, age, temp, status);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Method: deletePetUI
     * Purpose: Guides the user through deleting a pet, triggering the safety checks.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to delete the pet record.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void deletePetUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- Delete Pet Record ---");
            System.out.print("Pet ID to delete: "); 
            int id = Integer.parseInt(sc.nextLine());
            deletePet(conn, id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}