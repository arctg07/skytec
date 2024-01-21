package org.example.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository class for logging event data related to gold transactions.
 * This class handles the operations of storing event information in a database.
 */
public class EventRepository {
    private static final String URL = "jdbc:h2:~/testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String INSERT_EVENT_QUERY = "INSERT INTO events (gold, current_balance) VALUES (?, ?)";

    /**
     * Logs an event related to a gold transaction in the database.
     * This method records details about a change in gold balance including the amount changed and the resulting balance.
     *
     * @param goldDelta The change in gold (positive for addition, negative for subtraction).
     * @param currentBalance The current gold balance after the transaction.
     */
    public void logEvent(int goldDelta, int currentBalance) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_EVENT_QUERY)) {
            preparedStatement.setInt(1, goldDelta);
            preparedStatement.setInt(2, currentBalance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
