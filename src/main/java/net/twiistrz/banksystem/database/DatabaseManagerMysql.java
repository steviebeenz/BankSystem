package net.twiistrz.banksystem.database;

import net.twiistrz.banksystem.BankSystem;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public final class DatabaseManagerMysql implements DatabaseManagerInterface {

    private Connection conn = null;
    private final BankSystem plugin;

    public DatabaseManagerMysql(BankSystem pl) {
        this.plugin = pl;
        connectToDatabase();
        setupDatabase();
    }

    private void connectToDatabase() {
        BankSystem.logger.log(Level.INFO, "Connecting to the database...");
        try {
            //Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getConfigurationHandler().getString("DataSource.mySQLUsername"));
            properties.setProperty("password", plugin.getConfigurationHandler().getString("DataSource.mySQLPassword"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", plugin.getConfigurationHandler().getString("DataSource.mySQLUseSSL"));
            properties.setProperty("requireSSL", plugin.getConfigurationHandler().getString("DataSource.mySQLUseSSL"));
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfigurationHandler().getString("DataSource.mySQLHost") + ":" + plugin.getConfigurationHandler().getString("DataSource.mySQLPort") + "/" + plugin.getConfigurationHandler().getString("DataSource.mySQLDatabase"), properties);
        } catch (ClassNotFoundException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not locate drivers for mysql!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            return;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not connect to mysql database!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            return;
        }
        BankSystem.logger.log(Level.INFO, "Database connection successful!");
    }

    @Override
    public boolean setupDatabase() {
        PreparedStatement query = null;
        try {
            String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` (id int(10) AUTO_INCREMENT, player_uuid varchar(50) NOT NULL UNIQUE, player_name varchar(50) NOT NULL, money double(30,2) NOT NULL, last_seen varchar(30) NOT NULL, sync_complete varchar(5) NOT NULL, PRIMARY KEY(id));";
            query = conn.prepareStatement(data);
            query.execute();
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "Error creating table from mysql database!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            return false;
        } finally {
            try {
                if (query != null) {
                    query.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "Error closing mysql database!");
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        updateTables();
        return true;
    }

    @Override
    public Connection getConnection() {
        checkConnection();
        return conn;
    }

    public boolean checkConnection() {
        try {
            if (conn == null) {
                BankSystem.logger.log(Level.WARNING, "Connection failed. Reconnecting...");
                return reConnect() == true;
            }
            if (!conn.isValid(3)) {
                BankSystem.logger.log(Level.WARNING, "Connection is idle or terminated. Reconnecting...");
                return reConnect() == true;
            }
            if (conn.isClosed() == true) {
                BankSystem.logger.log(Level.WARNING, "Connection is closed. Reconnecting...");
                return reConnect() == true;
            }            
            return true;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not reconnect to Database!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return true;
    }

    public boolean reConnect() {
        try {
            long start;
            long end;
            start = System.currentTimeMillis();
            BankSystem.logger.log(Level.INFO, "Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getConfigurationHandler().getString("DataSource.mySQLUsername"));
            properties.setProperty("password", plugin.getConfigurationHandler().getString("DataSource.mySQLPassword"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", plugin.getConfigurationHandler().getString("DataSource.mySQLUseSSL"));
            properties.setProperty("requireSSL", plugin.getConfigurationHandler().getString("DataSource.mySQLUseSSL"));
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfigurationHandler().getString("DataSource.mySQLHost") + ":" + plugin.getConfigurationHandler().getString("DataSource.mySQLPort") + "/" + plugin.getConfigurationHandler().getString("DataSource.mySQLDatabase"), properties);
            end = System.currentTimeMillis();
            BankSystem.logger.log(Level.INFO, "Connection to MySQL server established!");
            BankSystem.logger.log(Level.INFO, "Connection took {0}ms!", end - start);
            return true;
        } catch (ClassNotFoundException e) {
            BankSystem.logger.log(Level.INFO, "Could not connect to MySQL server! because: {0}", e.getMessage());
            return false;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.INFO, "Could not connect to MySQL server! because: {0}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean closeDatabase() {
        try {
            conn.close();
            conn = null;
            return true;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.INFO, "Could not close MySQL server! because: {0}", e.getMessage());
        }
        return false;
    }

    private void updateTables() {
        if (conn != null) {
            DatabaseMetaData md;
            ResultSet rs1 = null;
            ResultSet rs2 = null;
            ResultSet rs3 = null;
            PreparedStatement query1 = null;
            PreparedStatement query2 = null;
            PreparedStatement query3 = null;
            try {
                md = conn.getMetaData();
                rs1 = md.getColumns(null, null, plugin.getConfigurationHandler().getString("DataSource.mySQLTablename"), "sync_complete");
                if (rs1.next()) {
                } else {
                    String data = "ALTER TABLE `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` ADD sync_complete varchar(5) NOT NULL DEFAULT 'true';";
                    query1 = conn.prepareStatement(data);
                    query1.execute();
                }
                rs2 = md.getColumns(null, null, plugin.getConfigurationHandler().getString("DataSource.mySQLTablename"), "player_name");
                if (rs2.next()) {
                } else {
                    String data = "ALTER TABLE `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` ADD player_name varchar(50) NOT NULL DEFAULT 'true';";
                    query2 = conn.prepareStatement(data);
                    query2.execute();
                }
                rs3 = md.getColumns(null, null, plugin.getConfigurationHandler().getString("DataSource.mySQLTablename"), "last_seen");
                if (rs3.next()) {
                } else {
                    String data = "ALTER TABLE `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` ADD last_seen varchar(30) NOT NULL DEFAULT 'true';";
                    query3 = conn.prepareStatement(data);
                    query3.execute();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "Error updating inventory table!");
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            } finally {
                try {
                    if (query1 != null) {
                        query1.close();
                    }
                    if (query2 != null) {
                        query2.close();
                    }
                    if (query3 != null) {
                        query3.close();
                    }
                    if (rs1 != null) {
                        rs1.close();
                    }
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (rs3 != null) {
                        rs3.close();
                    }
                } catch (SQLException e) {
                    BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                }
            }
        }
    }
}
