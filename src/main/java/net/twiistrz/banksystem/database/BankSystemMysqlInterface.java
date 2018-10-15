package net.twiistrz.banksystem.database;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class BankSystemMysqlInterface implements UserdataDatabaseInterface<Double> {
    private final BankSystem plugin;

    public BankSystemMysqlInterface(BankSystem pl) {
        this.plugin = pl;
    }

    @Override
    public boolean hasUserdata(Player player) {
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            String sql = "SELECT `player_uuid` FROM `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            while (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean hasUserdata(UUID playerUUID) {
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            String sql = "SELECT `player_uuid` FROM `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, playerUUID.toString());
            result = preparedUpdateStatement.executeQuery();
            while (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean createUserdata(Player player) {
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedStatement = null;
        try {
            String sql = "INSERT INTO `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "`(`player_uuid`, `player_name`, `money`, `last_seen`, `sync_complete`) " + "VALUES(?, ?, ?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setDouble(3, 0.00);
            preparedStatement.setString(4, System.currentTimeMillis() + "");
            preparedStatement.setString(5, "true");
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public Double getBalance(Player player) {
        if (!hasUserdata(player)) {
            createUserdata(player);
        }
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            String sql = "SELECT `money` FROM `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            while (result.next()) {
                return Double.parseDouble(result.getString("money"));
            }
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Double getBalance(UUID playerUUID) {
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            String sql = "SELECT `money` FROM `" + plugin.getConfigurationHandler().getString("DataBase.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, playerUUID.toString());
            result = preparedUpdateStatement.executeQuery();
            while (result.next()) {
                return Double.parseDouble(result.getString("money"));
            }
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean setBalance(Player player, Double amount) {
        if (!hasUserdata(player)) {
            createUserdata(player);
        }
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        try {
            String updateSql = "UPDATE `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` " + "SET `money` = ?" + "WHERE `player_uuid` = ?";
            preparedUpdateStatement = conn.prepareStatement(updateSql);
            preparedUpdateStatement.setDouble(1, amount);
            preparedUpdateStatement.setString(2, player.getUniqueId().toString());
            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } finally {
            try {
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean setBalance(UUID playerUUID, Double amount) {
        Connection conn = plugin.getDatabaseManagerInterface().getConnection();
        PreparedStatement preparedUpdateStatement = null;
        try {
            String updateSql = "UPDATE `" + plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` " + "SET `money` = ?" + "WHERE `player_uuid` = ?";
            preparedUpdateStatement = conn.prepareStatement(updateSql);
            preparedUpdateStatement.setDouble(1, amount);
            preparedUpdateStatement.setString(2, playerUUID.toString());

            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
        } finally {
            try {
                if (preparedUpdateStatement != null) {
                    preparedUpdateStatement.close();
                }
            } catch (SQLException e) {
                BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
            }
        }
        return false;
    }
}
