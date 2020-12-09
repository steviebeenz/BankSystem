package com.daishitie.banksystem.handlers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.daishitie.banksystem.BankSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public final class ConfigHandler {
    private final BankSystem plugin;

    String noConfig = "Could not locate {0} in the config file inside of the {1} folder! (Try generating a new one by deleting the current)";

    public ConfigHandler(BankSystem pl) {
        this.plugin = pl;
        loadConfig();
    }

    public void loadConfig() {
        File pluginFolder = new File("plugins" + System.getProperty("file.separator") + this.plugin.getDescription().getName());
        if (!pluginFolder.exists())
            pluginFolder.mkdir();
        File configFile = new File("plugins" + System.getProperty("file.separator") + this.plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml");
        if (!configFile.exists()) {
            BankSystem.logger.log(Level.WARNING, "Config file not found! Creating new one...");
            this.plugin.saveDefaultConfig();
        }
        try {
            BankSystem.logger.log(Level.INFO, "Loading the config file...");
            this.plugin.getConfig().load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not load the config file! You need to regenerate the config!");
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
    }

    public String getString(String key) {
        if (!this.plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{key, this.plugin.getDescription().getName()});
            return null;
        }
        return this.plugin.getConfig().getString(key);
    }

    public List<String> getStringList(String key) {
        if (!this.plugin.getConfig().contains(key) || this.plugin.getConfig().getStringList(key) == null) {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[] { key, this.plugin.getDescription().getName() });
            return null;
        }

        return this.plugin.getConfig().getStringList(key);
    }

    public String getStringWithColor(String key) {
        if (!this.plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{key, this.plugin.getDescription().getName()});
            return null;
        }

        return this.plugin.getConfig().getString(key).replaceAll("&", "");
    }

    public Integer getInteger(String key) {
        if (!this.plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{key, this.plugin.getDescription().getName()});
            return null;
        }
        return Integer.valueOf(this.plugin.getConfig().getInt(key));
    }

    public Boolean getBoolean(String key) {
        if (!this.plugin.getConfig().contains(key)) {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{key, this.plugin.getDescription().getName()});
            return null;
        }
        return Boolean.valueOf(this.plugin.getConfig().getBoolean(key));
    }

    public String printBalance(Player p1, String message, Player p2) {
        String format = "#,##0.00";
        if (this.plugin.config().getString("Settings.moneyFormat").equals("#,###.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,###") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0.00") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0") || this.plugin
                .config().getString("Settings.moneyFormat").equals("0.00"))
            format = this.plugin.config().getString("Settings.moneyFormat");
        DecimalFormat money = new DecimalFormat(format);
        Double bankBalance = Double.valueOf(0.0D);
        Double totalBalance = Double.valueOf(BankSystem.economy.getBalance(p2));
        if (this.plugin.getMoneyDatabaseInterface().hasUserdata(p2)) {
            bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p2);
            totalBalance = Double.valueOf(BankSystem.economy.getBalance(p2) + bankBalance.doubleValue());
        }
        return message
                .replace("%balance%", this.plugin.config().getString("Settings.currency") + money.format(bankBalance))
                .replace("%pocket%", this.plugin.config().getString("Settings.currency") + money.format(BankSystem.economy.getBalance(p2)))
                .replace("%total%", this.plugin.config().getString("Settings.currency") + money.format(totalBalance));
    }

    public String printBalance(CommandSender p1, String message, Player p2) {
        String format = "#,##0.00";
        if (this.plugin.config().getString("Settings.moneyFormat").equals("#,###.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,###") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0.00") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0") || this.plugin
                .config().getString("Settings.moneyFormat").equals("0.00"))
            format = this.plugin.config().getString("Settings.moneyFormat");
        DecimalFormat money = new DecimalFormat(format);
        Double bankBalance = Double.valueOf(0.0D);
        Double totalBalance = Double.valueOf(BankSystem.economy.getBalance(p2));
        if (this.plugin.getMoneyDatabaseInterface().hasUserdata(p2)) {
            bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p2);
            totalBalance = Double.valueOf(BankSystem.economy.getBalance(p2) + bankBalance.doubleValue());
        }
        return message
                .replace("%balance%", this.plugin.config().getString("Settings.currency") + money.format(bankBalance))
                .replace("%pocket%", this.plugin.config().getString("Settings.currency") + money.format(BankSystem.economy.getBalance(p2)))
                .replace("%total%", this.plugin.config().getString("Settings.currency") + money.format(totalBalance));
    }

    public void printMessage(Player p1, String messageKey, String amount, Player p2, String p2Name, Boolean prefix) {
        String format = "#,##0.00";
        if (this.plugin.config().getString("Settings.moneyFormat").equals("#,###.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,###") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0.00") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0") || this.plugin
                .config().getString("Settings.moneyFormat").equals("0.00"))
            format = this.plugin.config().getString("Settings.moneyFormat");
        DecimalFormat money = new DecimalFormat(format);
        if (this.plugin.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<String>();
            message.add(this.plugin.getConfig().getString(messageKey));
            if (getString(messageKey).isEmpty())
                return;
            if (p2 != null && !p2Name.isEmpty())
                message.set(0, message.get(0).replaceAll("%player%", p2Name));
            if (message.get(0).contains("%interestCooldown%"))
                message.set(0, message.get(0).replaceAll("%interestCooldown%", this.plugin.getInterestHandler().getNextInterestTime()));
            if (amount != null && !amount.isEmpty())
                message.set(0, message.get(0).replace("%amount%", this.plugin.config().getString("Settings.currency") + money.format(Double.parseDouble(amount))));
            if (prefix.booleanValue()) {
                p1.sendMessage(parseColorCodes(getString("Messages.prefix") + message.get(0)));
            } else {
                p1.sendMessage(parseColorCodes(message.get(0)));
            }
            for (int i = 1; i < message.size(); i++)
                p1.sendMessage(parseColorCodes(message.get(i)));
        } else {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{messageKey, this.plugin.getDescription().getName()});
            p1.sendMessage(parseColorCodes(getString("Messages.prefix")) + "&cCould not locate " + messageKey + " in the config file inside of the " + this.plugin.getDescription().getName() + " folder! (Try generating a new one by deleting the current)");
        }
    }

    public void printMessage(CommandSender sender, String messageKey, String amount, Player p2, String p2Name, Boolean prefix) {
        String format = "#,##0.00";
        if (this.plugin.config().getString("Settings.moneyFormat").equals("#,###.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,###") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#.##") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0.00") || this.plugin
                .config().getString("Settings.moneyFormat").equals("#,##0") || this.plugin
                .config().getString("Settings.moneyFormat").equals("0.00"))
            format = this.plugin.config().getString("Settings.moneyFormat");
        DecimalFormat money = new DecimalFormat(format);
        if (this.plugin.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<String>();
            message.add(this.plugin.getConfig().getString(messageKey));
            if (getString(messageKey).isEmpty())
                return;
            if (message.get(0).contains("%interestCooldown%"))
                message.set(0, message.get(0).replaceAll("%interestCooldown%", this.plugin.getInterestHandler().getNextInterestTime()));
            if (amount != null && !amount.isEmpty())
                message.set(0, message.get(0).replace("%amount%", this.plugin.config().getString("Settings.currency") + money.format(Double.parseDouble(amount))));
            if (!p2Name.isEmpty())
                message.set(0, message.get(0).replaceAll("%player%", p2Name));
            if (prefix.booleanValue()) {
                sender.sendMessage(parseColorCodes(getString("Messages.prefix") + message.get(0)));
            } else {
                sender.sendMessage(parseColorCodes(message.get(0)));
            }
            for (int i = 1; i < message.size(); i++)
                sender.sendMessage(parseColorCodes(message.get(i)));
        } else {
            BankSystem.logger.log(Level.SEVERE, this.noConfig, new Object[]{messageKey, this.plugin.getDescription().getName()});
            sender.sendMessage(parseColorCodes(getString("Messages.prefix")) + "&cCould not locate " + messageKey + " in the config file inside of the " + this.plugin.getDescription().getName() + " folder! (Try generating a new one by deleting the current)");
        }
    }

    public String parseColorCodes(String message) {
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
