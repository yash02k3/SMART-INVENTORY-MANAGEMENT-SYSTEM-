package com.cypheranalytica.inventory.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Manager (Singleton pattern).
 * Manages JDBC connections to MySQL with try-with-resources support,
 * automated schema verification, and connection health checking.
 * 
 * Part of Smart Inventory Management System
 * Developed by: Payal (Roll No. 248036)
 * Cypher Analytica Internship
 */
public class DBConnection {
    private static DBConnection instance;
    private final Properties properties = new Properties();
    private boolean initialized = false;
    private boolean connectionFailed = false;
    private String lastErrorMessage = "";

    private DBConnection() {
        loadProperties();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void loadProperties() {
        File configFile = new File("config/db.properties");
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (Exception e) {
                System.err.println("Notice: Could not load config/db.properties: " + e.getMessage());
            }
        }
        // Set safe defaults if properties missing
        if (!properties.containsKey("db.url")) {
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/inventory_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        }
        if (!properties.containsKey("db.user")) {
            properties.setProperty("db.user", "root");
        }
        if (!properties.containsKey("db.password")) {
            properties.setProperty("db.password", "root");
        }
        if (!properties.containsKey("db.driver")) {
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }

        try {
            Class.forName(properties.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            connectionFailed = true;
            lastErrorMessage = "MySQL JDBC Driver not found in classpath: " + e.getMessage();
        }
    }

    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Tests if MySQL is actively reachable.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            connectionFailed = true;
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Automatically ensures database tables and seed data are initialized
     * if they don't already exist.
     */
    public boolean ensureSchema() {
        try (Connection conn = getConnection()) {
            // Check if items table exists
            boolean tablesExist = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'items'")) {
                if (rs.next()) {
                    tablesExist = true;
                }
            } catch (SQLException ignored) {
            }

            if (!tablesExist) {
                System.out.println(">> Initializing MySQL database schema and seed data...");
                executeSchemaFile(conn, "sql/schema.sql");
                System.out.println(">> Database schema created successfully.");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Notice: Could not verify/create schema in MySQL: " + e.getMessage());
            return false;
        }
    }

    private void executeSchemaFile(Connection conn, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().replace(";", "").trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            // ignore drop table errors if not exists
                        }
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing schema script: " + e.getMessage());
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public Properties getProperties() {
        return properties;
    }

    public void updateCredentials(String url, String user, String pass) {
        if (url != null && !url.trim().isEmpty()) properties.setProperty("db.url", url);
        if (user != null) properties.setProperty("db.user", user);
        if (pass != null) properties.setProperty("db.password", pass);
    }
}
