package net.twiistrz.banksystem;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ConfigHandler {
	
    private final BankSystem plugin;
    String noConfig = "Could not locate {0} in the config file inside of the {1} folder! (Try generating a new one by deleting the current)";

    /**
     *
     * @param pl Main Class
     */
    public ConfigHandler(BankSystem pl) {
        this.plugin = pl;
        loadConfig();
    }
    
    public void loadConfig() {
        File pluginFolder = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        File configFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml");
        if (!configFile.exists()) {
            BankSystem.logger.log(Level.WARNING, "Config file not found! Creating new one...");
            plugin.saveDefaultConfig();
        }
        try {
            BankSystem.logger.log(Level.INFO, "Loading the config file...");
            plugin.getConfig().load(configFile);
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not load the config file! You need to regenerate the config!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } catch (InvalidConfigurationException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not load the config file! You need to regenerate the config!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
    }
	
    /**
     *
     * @param key Configuration message key
     * @return Return string
     */
    public String getString(String key) {
        if (!plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{key, plugin.getDescription().getName()});
            return null;
        }
        return plugin.getConfig().getString(key);
    }
    
    /**
     *
     * @param key Configuration message key
     * @return Return string list
     */
    public List<String> getStringList(String key) {
        if (!plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{key, plugin.getDescription().getName()});
            return null;
        }
        return plugin.getConfig().getStringList(key);
    }

    /**
     *
     * @param key Configuration message key
     * @return Return string with color
     */
    public String getStringWithColor(String key) {
        if (!plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{key, plugin.getDescription().getName()});
            return null;
        }
        return plugin.getConfig().getString(key).replaceAll("&", "ﾂｧ");
    }
	
    /**
     *
     * @param key Configuration message key
     * @return Return integer
     */
    public Integer getInteger(String key) {
        if (!plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{key, plugin.getDescription().getName()});
            return null;
        }
        return plugin.getConfig().getInt(key);
    }
	
    /**
     *
     * @param key Configuration message key
     * @return Return true or false
     */
    public Boolean getBoolean(String key) {
        if (!plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{key, plugin.getDescription().getName()});
            return null;
        }
        return plugin.getConfig().getBoolean(key);
    }
    
    public String printBalance(Player p1, String message, Player p2) {
        DecimalFormat money = new DecimalFormat("#,##0.00");
        Double bankBalance = 0.00;
        Double totalBalance = BankSystem.econ.getBalance(p2);
        if (plugin.getMoneyDatabaseInterface().hasUserdata(p2)) {
            bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p2);
            totalBalance = BankSystem.econ.getBalance(p2) + bankBalance;
        }
        return message.replaceAll("%balance%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(bankBalance)).replaceAll("%pocket%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(BankSystem.econ.getBalance(p2))).replaceAll("%total%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(totalBalance));
    }
    
    public String printBalance(CommandSender p1, String message, Player p2) {
        DecimalFormat money = new DecimalFormat("#,##0.00");
        Double bankBalance = 0.00;
        Double totalBalance = BankSystem.econ.getBalance(p2);
        if (plugin.getMoneyDatabaseInterface().hasUserdata(p2)) {
            bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p2);
            totalBalance = BankSystem.econ.getBalance(p2) + bankBalance;
        }
        return message.replaceAll("%balance%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(bankBalance)).replaceAll("%pocket%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(BankSystem.econ.getBalance(p2))).replaceAll("%total%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(totalBalance));
    }

    /**
     *
     * @param p1 Player one
     * @param messageKey Configuration message key
     * @param amount Amount of money
     * @param p2 Player two
     * @param p2Name Player two name
     * @param prefix If prefix is enabled or disabled in message
     */
    public void printMessage(Player p1, String messageKey, String amount, Player p2, String p2Name, Boolean prefix) {
        DecimalFormat money = new DecimalFormat("#,##0.00");
        if (plugin.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<String>();
            message.add(plugin.getConfig().getString(messageKey));
            if (getString(messageKey).isEmpty()) {
                return;
            }
            if ((p2 != null) && (!p2Name.isEmpty())) {
              message.set(0, ((String)message.get(0)).replaceAll("%player%", p2Name));
            }
            if (message.get(0).contains("%interestCooldown%")) {
                message.set(0, message.get(0).replaceAll("%interestCooldown%", plugin.getInterestHandler().getNextInterestTime()));
            }
            if (amount != null && !amount.isEmpty()) {
                message.set(0, message.get(0).replaceAll("%amount%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(Double.parseDouble(amount))));
            }
            if (prefix) {
                p1.sendMessage(parseFormattingCodes(getString("Messages.prefix") + message.get(0)));
            } else {
                p1.sendMessage(parseFormattingCodes(message.get(0)));
            }
            for (int i = 1; i < message.size(); i++) {
                p1.sendMessage(parseFormattingCodes(message.get(i)));
            }
        } else {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{messageKey, plugin.getDescription().getName()});
            p1.sendMessage(parseFormattingCodes(getString("Messages.prefix")) + "&cCould not locate " + messageKey + " in the config file inside of the " + plugin.getDescription().getName() + " folder! (Try generating a new one by deleting the current)");
        }
    }
    
    public void printMessage(CommandSender sender, String messageKey, String amount, Player p2, String p2Name, Boolean prefix) {
        DecimalFormat money = new DecimalFormat("#,##0.00");
        if (plugin.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<String>();
            message.add(plugin.getConfig().getString(messageKey));
            if (getString(messageKey).isEmpty()) {
                return;
            }
            if ((p2 != null) && (!p2Name.isEmpty())) {
              message.set(0, ((String)message.get(0)).replaceAll("%player%", p2Name));
            }
            if (message.get(0).contains("%interestCooldown%")) {
                message.set(0, message.get(0).replaceAll("%interestCooldown%", plugin.getInterestHandler().getNextInterestTime()));
            }
            if (amount != null && !amount.isEmpty()) {
                message.set(0, message.get(0).replaceAll("%amount%", this.plugin.getConfigurationHandler().getString("Settings.currency") + money.format(Double.parseDouble(amount))));
            }
            if (prefix) {
                sender.sendMessage(parseFormattingCodes(getString("Messages.prefix") + message.get(0)));
            } else {
                sender.sendMessage(parseFormattingCodes(message.get(0)));
            }
            for (int i = 1; i < message.size(); i++) {
                sender.sendMessage(parseFormattingCodes(message.get(i)));
            }
        } else {
            BankSystem.logger.log(Level.SEVERE, noConfig, new Object[]{messageKey, plugin.getDescription().getName()});
            sender.sendMessage(parseFormattingCodes(getString("Messages.prefix")) + "&cCould not locate " + messageKey + " in the config file inside of the " + plugin.getDescription().getName() + " folder! (Try generating a new one by deleting the current)");
        }
    }

    /**
     *
     * @param message Message itself
     * @return Return string message
     */
    public String parseFormattingCodes(String message) {
        message = message.replaceAll("&0", ChatColor.BLACK + "");
        message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
        message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
        message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
        message = message.replaceAll("&4", ChatColor.DARK_RED + "");
        message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        message = message.replaceAll("&6", ChatColor.GOLD + "");
        message = message.replaceAll("&7", ChatColor.GRAY + "");
        message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
        message = message.replaceAll("&9", ChatColor.BLUE + "");
        message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
        message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
        message = message.replaceAll("(?i)&c", ChatColor.RED + "");
        message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
        message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
        message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
        message = message.replaceAll("(?i)&l", ChatColor.BOLD + "");
        message = message.replaceAll("(?i)&o", ChatColor.ITALIC + "");
        message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "");
        message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE + "");
        message = message.replaceAll("(?i)&k", ChatColor.MAGIC + "");
        message = message.replaceAll("(?i)&r", ChatColor.RESET + "");
        return message;
    }
}
