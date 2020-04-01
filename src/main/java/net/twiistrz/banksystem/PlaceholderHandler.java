package net.twiistrz.banksystem;

import java.text.DecimalFormat;
import java.util.logging.Level;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderHandler extends PlaceholderExpansion {
  private final BankSystem plugin;
  
  public PlaceholderHandler(BankSystem pl) {
    this.plugin = pl;
  }
  
  public boolean persist() {
    return true;
  }
  
  public String getIdentifier() {
    return "banksystem";
  }
  
  public String getAuthor() {
    return "Mannyseete (Twiistrz)";
  }
  
  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }
  
  public String onPlaceholderRequest(Player p, String identifier) {
    try {
      DecimalFormat money;
      if (!this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("#,###.##") || 
        !this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("#,###") || 
        !this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("#.##") || 
        !this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("#,##0.00") || 
        !this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("#,##00") || 
        !this.plugin.getConfigurationHandler().getString("Settings.moneyFormat").equals("0.00")) {
        money = new DecimalFormat("#,##0.00");
      } else {
        money = new DecimalFormat(this.plugin.getConfigurationHandler().getString("Settings.moneyFormat"));
      } 
      Double bankBalance = Double.valueOf(0.0D);
      Double totalBalance = Double.valueOf(BankSystem.econ.getBalance((OfflinePlayer)p));
      if (p == null)
        return ""; 
      if (this.plugin.getMoneyDatabaseInterface().hasUserdata(p)) {
        bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
        totalBalance = Double.valueOf(BankSystem.econ.getBalance((OfflinePlayer)p) + bankBalance.doubleValue());
      } 
      if (identifier.equalsIgnoreCase("name"))
        return p.getName(); 
      if (identifier.equalsIgnoreCase("currency"))
        return this.plugin.getConfigurationHandler().getString("Settings.currency"); 
      if (identifier.equalsIgnoreCase("balance"))
        return money.format(bankBalance); 
      if (identifier.equalsIgnoreCase("pocket"))
        return money.format(BankSystem.econ.getBalance((OfflinePlayer)p)); 
      if (identifier.equalsIgnoreCase("total"))
        return money.format(totalBalance); 
      return null;
    } catch (Exception e) {
      BankSystem.logger.log(Level.WARNING, "Something went wrong with the placeholder.");
      BankSystem.logger.log(Level.WARNING, "Error: {0}", e.getMessage());
      return null;
    } 
  }
}
