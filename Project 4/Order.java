import java.sql.*;
import java.util.*;

/**
 * Class Name: Order
 * Author: Cason Vela, Ashiqul Alam, Andrew Huynh
 * Depends on: java.sql.*, java.util.*
 * Purpose: This class manages food and merchandise orders (the 'orders' table header). 
 * It handles the creation of the order, adding items (which triggers total recalculation 
 * factoring in member discounts), updating the status (e.g., payment/completion), and 
 * conditional deletion based on the order status.
 * Public Variables:
 * ordersID: Unique identifier for the order header.
 * memberID: ID of the member who placed the order (optional).
 * employeeID: ID of the staff member who took the order.
 * reservationID: ID of the reservation the order is linked to (optional).
 * orderDate: Date and time the order was placed.
 * amountOwing: The calculated total amount after applying discounts.
 * status: Current state of the order ('pending', 'started', 'complete', 'canceled').
 * Constructors:
 * Order(int, Integer, int, Integer, String, double, String): Initializes a new Order object.
 * Implemented Methods:
 * createOrder(Connection, Order): Inserts the order header, verifies linkage (memberID OR reservationID).
 * addItemToOrder(Connection, int, int, int): Inserts an item into orderLine and recalculates total.
 * recalculateOrderTotal(Connection, int): Calculates total price including member discounts and updates the orders table.
 * updateStatus(Connection, int, String): Changes the processing status of the order.
 * deleteOrder(Connection, int): Deletes the order and its lines only if the status is 'pending'.
 * createOrderUI(Connection, Scanner): Collects input for order creation and item addition loop.
 * manageOrderUI(Connection, Scanner): Presents menu for status update or deletion.
 */
public class Order {
    // Unique identifier for the order header.
    public int ordersID;
    // Optional: ID of the member who placed the order.
    public Integer memberID;
    // ID of the employee who took the order.
    public int employeeID;
    // Optional: ID of the reservation if linked.
    public Integer reservationID;
    // Date and time the order was placed.
    public String orderDate;
    // Final amount due after discounts.
    public double amountOwing;
    // Status ('pending', 'complete', etc.).
    public String status;

    /**
     * Method: Order Constructor
     * Purpose: Initializes a new Order object in memory.
     * Pre-conditions: Valid input for all order details. Nulls acceptable for memberID and reservationID.
     * Post-conditions: A new Order object is created with all fields set.
     * Return: None (Constructor).
     * Parameters:
     * int ordersID (In): The ID for the new order.
     * Integer memberID (In): The member who placed the order (can be null).
     * int employeeID (In): The staff member who took the order.
     * Integer reservationID (In): The reservation linkage (can be null).
     * String orderDate (In): The date of the order.
     * double amountOwing (In): The initial amount owing (usually 0.0).
     * String status (In): The initial status (usually 'pending').
     */
    public Order(int ordersID, Integer memberID, int employeeID, Integer reservationID, 
                 String orderDate, double amountOwing, String status) {
        this.ordersID = ordersID;
        this.memberID = memberID;
        this.employeeID = employeeID;
        this.reservationID = reservationID;
        this.orderDate = orderDate;
        this.amountOwing = amountOwing;
        this.status = status;
    }

    // --- SQL OPERATIONS ---

    /**
     * Method: createOrder
     * Purpose: Inserts a new order header into the database. Enforces that the order must be 
     * associated with either a Member or a Reservation (chkWhoOrdered constraint).
     * Pre-conditions: Active connection (conn) and a valid Order object (o).
     * Post-conditions: A new row is added to the ORDERS table with amountOwing initialized to 0.00.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Order o (In): The object containing data to be inserted.
     * Throws: SQLException for database errors.
     */
    public static void createOrder(Connection conn, Order o) throws SQLException {
        // Enforce business rule: Must be linked to a customer source.
        if (o.memberID == null && o.reservationID == null) {
            System.out.println("Error: Order must be linked to a Member OR a Reservation.");
            return;
        }

        String sql = "INSERT INTO orders (ordersID, memberID, employeeID, reservationID, orderDate, amountOwing, status) " +
                     "VALUES (?, ?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), 0.00, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.ordersID);
            // Handle optional Integer fields by setting NULL if the object field is null.
            if (o.memberID == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, o.memberID);
            ps.setInt(3, o.employeeID);
            if (o.reservationID == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, o.reservationID);
            ps.setString(5, o.orderDate);
            ps.setString(6, o.status); 
            
            ps.executeUpdate();
            System.out.println("Order #" + o.ordersID + " created. You can now add items to it.");
        }
    }

    /**
     * Method: addItemToOrder
     * Purpose: Adds a specific quantity of an item to the order's line items and triggers the 
     * recalculation of the order's total amount, including applicable discounts.
     * Pre-conditions: Active connection (conn) and valid IDs and quantity.
     * Post-conditions: A new row is added to ORDERLINE, and ORDERS.amountOwing is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int orderID (In): The ID of the order header.
     * int itemID (In): The ID of the food item being added.
     * int quantity (In): The quantity of the item.
     * Throws: SQLException for database errors.
     */
    public static void addItemToOrder(Connection conn, int orderID, int itemID, int quantity) throws SQLException {
        // Step A: Get Item Price from FoodItem table.
        double basePrice = 0.0;
        String priceSql = "SELECT itemPrice FROM foodItem WHERE itemID = ?";
        try (PreparedStatement ps = conn.prepareStatement(priceSql)) {
            ps.setInt(1, itemID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) basePrice = rs.getDouble("itemPrice");
            else { System.out.println("Item ID not found."); return; }
        }

        // Step B: Insert into OrderLine, storing the price at the time of purchase.
        String lineSql = "INSERT INTO orderLine (ordersID, itemID, quantity, priceAtPurchase) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(lineSql)) {
            ps.setInt(1, orderID);
            ps.setInt(2, itemID);
            ps.setInt(3, quantity);
            ps.setDouble(4, basePrice);
            ps.executeUpdate();
            System.out.println("Item added.");
        }

        // Step C: Recalculate the entire order total (including any applicable member discount).
        recalculateOrderTotal(conn, orderID);
    }

    /**
     * Method: recalculateOrderTotal
     * Purpose: Calculates the raw sum of all order lines and applies the discount rate 
     * associated with the ordering member's membership tier (if applicable).
     * Pre-conditions: Active connection (conn) and a valid orderID.
     * Post-conditions: The orders.amountOwing field is updated with the final price.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int orderID (In): The ID of the order header to recalculate.
     * Throws: SQLException for database errors.
     */
    private static void recalculateOrderTotal(Connection conn, int orderID) throws SQLException {
        // 1. Calculate Raw Total (Sum of quantity * priceAtPurchase).
        String sumSql = "SELECT SUM(quantity * priceAtPurchase) FROM orderLine WHERE ordersID = ?";
        double rawTotal = 0.0;
        try (PreparedStatement ps = conn.prepareStatement(sumSql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) rawTotal = rs.getDouble(1);
        }

        // 2. Get Discount Rate for the Member (Requires joining Orders -> Member -> MembershipTier).
        String discSql = "SELECT t.discountRate FROM orders o " +
                         "JOIN member m ON o.memberID = m.memberID " +
                         "JOIN membershipTier t ON m.tierID = t.tierID " +
                         "WHERE o.ordersID = ?";
        int discountPercent = 0;
        try (PreparedStatement ps = conn.prepareStatement(discSql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) discountPercent = rs.getInt("discountRate");
        }

        // 3. Apply Discount.
        double discountMultiplier = (100.0 - discountPercent) / 100.0;
        double finalAmount = rawTotal * discountMultiplier;

        // 4. Update Order Table with the new total.
        String updateSql = "UPDATE orders SET amountOwing = ? WHERE ordersID = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, finalAmount);
            ps.setInt(2, orderID);
            ps.executeUpdate();
        }
        
        System.out.printf("Order Total Updated: $%.2f (includes %d%% discount)\n", finalAmount, discountPercent);
    }

    /**
     * Method: updateStatus
     * Purpose: Updates the status field for a specific order ID.
     * Pre-conditions: Active connection (conn) and a valid order ID (orderID).
     * Post-conditions: The status column is updated.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int orderID (In): The ID of the order to modify.
     * String newStatus (In): The status to set (e.g., 'complete').
     * Throws: SQLException for database errors.
     */
    public static void updateStatus(Connection conn, int orderID, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE ordersID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderID);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Order status updated to: " + newStatus);
            else System.out.println("Order ID not found.");
        }
    }

    /**
     * Method: deleteOrder
     * Purpose: Deletes an order and its associated lines, but only if its current status is 'pending' 
     * (meaning no preparation has begun).
     * Pre-conditions: Active connection (conn) and a valid order ID (orderID).
     * Post-conditions: The order and all its lines are deleted, or an error is printed.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * int orderID (In): The ID of the order to delete.
     * Throws: SQLException for database errors.
     */
    public static void deleteOrder(Connection conn, int orderID) throws SQLException {
        // Step A: Check Status (must be 'pending').
        String statusSql = "SELECT status FROM orders WHERE ordersID = ?";
        try (PreparedStatement ps = conn.prepareStatement(statusSql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!status.equalsIgnoreCase("pending")) {
                    System.out.println("CANNOT DELETE: Order is already '" + status + "'. Only 'pending' orders can be deleted.");
                    return;
                }
            } else {
                System.out.println("Order ID not found.");
                return;
            }
        }

        // Step B: Delete Children (OrderLines) first due to FK constraint.
        String delLines = "DELETE FROM orderLine WHERE ordersID = ?";
        try (PreparedStatement ps = conn.prepareStatement(delLines)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
        }

        // Step C: Delete Header (Orders).
        String delHead = "DELETE FROM orders WHERE ordersID = ?";
        try (PreparedStatement ps = conn.prepareStatement(delHead)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
            System.out.println("Order #" + orderID + " deleted successfully.");
        }
    }

    // --- UI INPUT METHODS ---

    /**
     * Method: createOrderUI
     * Purpose: Guides the user through creating a new order header and then loops to add multiple items.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Tries to insert a new order and its line items.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void createOrderUI(Connection conn, Scanner sc) {
        try {
            System.out.println("\n--- New Order ---");
            System.out.print("Order ID: "); 
            int id = Integer.parseInt(sc.nextLine());
            
            // Get Member ID (optional)
            System.out.print("Member ID (or blank): "); 
            String m = sc.nextLine();
            Integer memID = m.isBlank() ? null : Integer.parseInt(m);
            
            System.out.print("Employee ID (Server): "); 
            int empID = Integer.parseInt(sc.nextLine());
            
            // Get Reservation ID (optional)
            System.out.print("Reservation ID (or blank): "); 
            String r = sc.nextLine();
            Integer resID = r.isBlank() ? null : Integer.parseInt(r);
            
            System.out.print("Date (YYYY-MM-DD HH:MM:SS): "); 
            String date = sc.nextLine();

            // Create Header (initial status 'pending', amount 0.0).
            Order o = new Order(id, memID, empID, resID, date, 0.0, "pending");
            createOrder(conn, o);

            // Item addition loop.
            while (true) {
                System.out.print("Add Item ID (or 'q' to finish): ");
                String input = sc.nextLine();
                if (input.equalsIgnoreCase("q")) break;
                
                int itemID = Integer.parseInt(input);
                System.out.print("Quantity: ");
                int qty = Integer.parseInt(sc.nextLine());
                
                addItemToOrder(conn, id, itemID, qty);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Method: manageOrderUI
     * Purpose: Provides a submenu to update order status or delete an order.
     * Pre-conditions: Active connection (conn) and Scanner (sc).
     * Post-conditions: Calls either updateStatus or deleteOrder based on user choice.
     * Return: void.
     * Parameters:
     * Connection conn (In): The active JDBC connection.
     * Scanner sc (In): The scanner object for reading user input.
     */
    public static void manageOrderUI(Connection conn, Scanner sc) {
        System.out.println("\n1. Update Status");
        System.out.println("2. Delete Order");
        System.out.print("Choice: ");
        String choice = sc.nextLine();

        try {
            if (choice.equals("1")) {
                // Option 1: Update status.
                System.out.print("Order ID: "); 
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("New Status (pending/started/complete/canceled): "); 
                String s = sc.nextLine();
                updateStatus(conn, id, s);
            } else if (choice.equals("2")) {
                // Option 2: Delete order (only if pending).
                System.out.print("Order ID to delete: "); 
                int id = Integer.parseInt(sc.nextLine());
                deleteOrder(conn, id);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}