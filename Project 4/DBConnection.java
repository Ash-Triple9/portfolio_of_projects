import java.sql.*;

// This class is responsible for establishing and managing the connection to the Oracle database.
public class DBConnection {
private static final String ORACLE_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
/**
 * Attempts to establish a connection to the Oracle database.
 * * @param username The database user's login name.
 * @param password The database user's password.
 * @return A live Connection object if the connection is successful.
 * @throws SQLException If a database access error occurs (e.g., driver not found, invalid credentials).
 */
public static Connection getConnection(String username, String password) throws SQLException {
	try {
		Class.forName("oracle.jdbc.OracleDriver");
	} catch (ClassNotFoundException e) {
		throw new SQLException("Oracle JDBC Driver not found.");
	}
		return DriverManager.getConnection(ORACLE_URL, username, password);
	}
}