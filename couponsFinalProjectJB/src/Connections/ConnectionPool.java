package Connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final int MAX_CONNECTIONS = 20;
    private final String URL ="jdbc:mysql://localhost:3306/coupons_db";
    private final String USER = "root";
    private final String PASSWORD = "1234";
    private List<Connection> connections = new ArrayList<>(MAX_CONNECTIONS);
    private static ConnectionPool instance;
    private ConnectionPool() throws SQLException {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            connections.add(DriverManager.getConnection(URL, USER, PASSWORD));
        }
    }
    public static ConnectionPool getInstance() throws SQLException {
        if (instance == null)
            instance = new ConnectionPool();
        return instance;
    }
    public Connection getConnection() {
        while (connections.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        Connection connection = connections.get(connections.size()-1);
        connections.remove(connection);
        return connection;
    }
    public synchronized void restoreConnection(Connection connection) {
        connections.add(connection);
        notify();
    }
    public void closeConnections() {
        while (connections.size() < MAX_CONNECTIONS) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        for (Connection connection: connections) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
    }
}
