package org.example.repository;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository class for handling operations related to the clan's gold.
 * This class manages database operations such as updating and retrieving the gold balance for a clan.
 */
public class ClanRepository {
    private static final String URL = "jdbc:h2:~/testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String INIT_DB_SCRIPT = """
        CREATE TABLE IF NOT EXISTS clan (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255),
            gold INT
        );

        INSERT INTO clan (name, gold) SELECT 'Clan', 0 WHERE NOT EXISTS (SELECT 1 FROM clan WHERE name = 'Clan');

        CREATE TABLE IF NOT EXISTS events (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            instant TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
            gold INT,
            current_balance INT
        );
        """;

    private static final String UPDATE_QUERY = "UPDATE clan SET gold = gold + ? WHERE name = 'Clan'";
    private static final String SELECT_QUERY = "SELECT gold FROM clan WHERE name = 'Clan'";

    /**
     * Updates the gold balance for the clan.
     * This method performs a transactional update to the clan's gold balance and returns the updated amount.
     *
     * @param value The amount of gold to be added or subtracted from the clan's balance.
     * @return The updated gold balance of the clan.
     */
    public int updateGold(int value) {
        int currentGoldAmount = 0;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            executeGoldUpdate(conn, value);
            currentGoldAmount = getCurrentGoldBalance(conn);

            conn.commit();
            System.out.println(Thread.currentThread().getName() + " UPDATED GOLD: " + value + ", CURRENT GOLD: " + currentGoldAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return currentGoldAmount;
    }

    /**
     * Initializes the clan database.
     * This method sets up the clan and event tables in the database if they do not already exist.
     */
    @SneakyThrows
    public void initClanDataBase() {
        try (val conn = getConnection()) {
            val statement = conn.createStatement();
            conn.setAutoCommit(false);
            try {
                statement.execute(INIT_DB_SCRIPT);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @SneakyThrows
    private Connection getConnection() {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @SneakyThrows
    private void executeGoldUpdate(Connection conn, int value) {
        try (PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setInt(1, value);
            preparedStatement.executeUpdate();
        }
    }

    @SneakyThrows
    private int getCurrentGoldBalance(Connection conn) {
        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("gold");
            }
        }
        return 0;
    }
}
