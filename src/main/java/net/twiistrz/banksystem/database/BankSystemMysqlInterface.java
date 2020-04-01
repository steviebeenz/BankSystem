package net.twiistrz.banksystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import net.twiistrz.banksystem.BankSystem;
import org.bukkit.entity.Player;

public class BankSystemMysqlInterface implements UserdataDatabaseInterface<Double> {
  private final BankSystem plugin;
  
  public BankSystemMysqlInterface(BankSystem pl) {
    this.plugin = pl;
  }
  
  public boolean hasUserdata(Player player) {
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    ResultSet result = null;
    try {
      String sql = "SELECT `player_uuid` FROM `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
      preparedUpdateStatement = conn.prepareStatement(sql);
      preparedUpdateStatement.setString(1, player.getUniqueId().toString());
      result = preparedUpdateStatement.executeQuery();
      if (result.next())
        return true; 
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
    } finally {
      try {
        if (result != null)
          result.close(); 
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return false;
  }
  
  public boolean hasUserdata(UUID playerUUID) {
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    ResultSet result = null;
    try {
      String sql = "SELECT `player_uuid` FROM `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
      preparedUpdateStatement = conn.prepareStatement(sql);
      preparedUpdateStatement.setString(1, playerUUID.toString());
      result = preparedUpdateStatement.executeQuery();
      if (result.next())
        return true; 
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
    } finally {
      try {
        if (result != null)
          result.close(); 
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return false;
  }
  
  public boolean createUserdata(Player player) {
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedStatement = null;
    try {
      String sql = "INSERT INTO `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "`(`player_uuid`, `player_name`, `money`, `last_seen`, `sync_complete`) VALUES(?, ?, ?, ?, ?)";
      preparedStatement = conn.prepareStatement(sql);
      preparedStatement.setString(1, player.getUniqueId().toString());
      preparedStatement.setString(2, player.getName());
      preparedStatement.setDouble(3, 0.0D);
      preparedStatement.setString(4, System.currentTimeMillis() + "");
      preparedStatement.setString(5, "true");
      preparedStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
    } finally {
      try {
        if (preparedStatement != null)
          preparedStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return false;
  }
  
  public Double getBalance(Player player) {
    if (!hasUserdata(player))
      createUserdata(player); 
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    ResultSet result = null;
    try {
      String sql = "SELECT `money` FROM `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
      preparedUpdateStatement = conn.prepareStatement(sql);
      preparedUpdateStatement.setString(1, player.getUniqueId().toString());
      result = preparedUpdateStatement.executeQuery();
      if (result.next())
        return Double.valueOf(Double.parseDouble(result.getString("money"))); 
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
    } finally {
      try {
        if (result != null)
          result.close(); 
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return null;
  }
  
  public Double getBalance(UUID playerUUID) {
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    ResultSet result = null;
    try {
      String sql = "SELECT `money` FROM `" + this.plugin.getConfigurationHandler().getString("DataBase.mySQLTablename") + "` WHERE `player_uuid` = ? LIMIT 1";
      preparedUpdateStatement = conn.prepareStatement(sql);
      preparedUpdateStatement.setString(1, playerUUID.toString());
      result = preparedUpdateStatement.executeQuery();
      if (result.next())
        return Double.valueOf(Double.parseDouble(result.getString("money"))); 
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
    } finally {
      try {
        if (result != null)
          result.close(); 
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return null;
  }
  
  public boolean setBalance(Player player, Double amount) {
    if (!hasUserdata(player))
      createUserdata(player); 
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    try {
      String updateSql = "UPDATE `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` SET `money` = ?WHERE `player_uuid` = ?";
      preparedUpdateStatement = conn.prepareStatement(updateSql);
      preparedUpdateStatement.setDouble(1, amount.doubleValue());
      preparedUpdateStatement.setString(2, player.getUniqueId().toString());
      preparedUpdateStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
    } finally {
      try {
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
      } 
    } 
    return false;
  }
  
  public boolean setBalance(UUID playerUUID, Double amount) {
    Connection conn = this.plugin.getDatabaseManagerInterface().getConnection();
    PreparedStatement preparedUpdateStatement = null;
    try {
      String updateSql = "UPDATE `" + this.plugin.getConfigurationHandler().getString("DataSource.mySQLTablename") + "` SET `money` = ?WHERE `player_uuid` = ?";
      preparedUpdateStatement = conn.prepareStatement(updateSql);
      preparedUpdateStatement.setDouble(1, amount.doubleValue());
      preparedUpdateStatement.setString(2, playerUUID.toString());
      preparedUpdateStatement.executeUpdate();
      return true;
    } catch (SQLException e) {
      BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
    } finally {
      try {
        if (preparedUpdateStatement != null)
          preparedUpdateStatement.close(); 
      } catch (SQLException e) {
        BankSystem.logger.log(Level.SEVERE, "Error: {0}", e.getMessage());
      } 
    } 
    return false;
  }
}
