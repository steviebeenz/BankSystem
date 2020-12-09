/*  This file is part of BankSystem.

    BankSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BankSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BankSystem. If not, see <http://www.gnu.org/licenses/>.
 */
package com.daishitie.banksystem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.daishitie.banksystem.events.PlayerListener;
import com.daishitie.banksystem.handlers.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import com.daishitie.banksystem.commands.BalanceCommand;
import com.daishitie.banksystem.commands.DepositCommand;
import com.daishitie.banksystem.commands.InterestCommand;
import com.daishitie.banksystem.commands.ReloadCommand;
import com.daishitie.banksystem.commands.SetCommand;
import com.daishitie.banksystem.commands.WithdrawCommand;
import com.daishitie.banksystem.database.BankSystemFlatFileInterface;
import com.daishitie.banksystem.database.BankSystemMysqlInterface;
import com.daishitie.banksystem.database.DatabaseManagerFlatFile;
import com.daishitie.banksystem.database.DatabaseManagerInterface;
import com.daishitie.banksystem.database.DatabaseManagerMysql;
import com.daishitie.banksystem.database.UserdataDatabaseInterface;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BankSystem extends JavaPlugin {
    private boolean pluginEnabled = false;
    private static ConfigHandler configHandler;
    private static SoundHandler soundHandler;
    private static ReloadCommand reloadCommand;
    private static BalanceCommand balanceCommand;
    private static SetCommand setCommand;
    private static DepositCommand depositCommand;
    private static WithdrawCommand withdrawCommand;
    private static InterestHandler interestHandler;
    private static InterestCommand interestCommand;
    private DatabaseManagerInterface databaseManager;
    private UserdataDatabaseInterface<Double> moneyDatabaseInterface;

    public static Logger logger = Logger.getLogger("BankSystem");
    public static Economy economy = null;
    public static Permission permission = null;

    public String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
    public boolean is18Server = false;
    public boolean is19Server = false;
    public boolean is110Server = false;
    public boolean is111Server = false;
    public boolean is112Server = false;
    public boolean is113Server = false;
    public boolean is114Server = false;
    public boolean is115Server = false;
    public boolean is116Server = false;

    public Set<UUID> cooldown = new HashSet<>();

    @Override
    public void onEnable() {
        // Check server version.
        verifyServerVersion();

        // Check dependencies.
        setupVault();
        setupEconomy();
        setupPermissions();

        // Set handlers.
        configHandler = new ConfigHandler(this);
        soundHandler = new SoundHandler(this);

        String flatfilePath = "plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata";

        switch (configHandler.getString("DataSource.backend").toLowerCase()) {
            case "flatfile":
                logger.log(Level.INFO, "Using FlatFile as DataSource Backend");

                if (!(new File(flatfilePath)).exists()) {
                    (new File(flatfilePath)).mkdir();
                }

                logger.log(Level.INFO, "FlatFile Loaded successfully");

                this.databaseManager = new DatabaseManagerFlatFile(this);
                this.moneyDatabaseInterface = new BankSystemFlatFileInterface(this);
                break;
            case "mysql":
                logger.log(Level.INFO, "Using MySQL as DataSource Backend");

                this.databaseManager = new DatabaseManagerMysql(this);
                this.moneyDatabaseInterface = new BankSystemMysqlInterface(this);
                break;
            default:
                logger.log(Level.SEVERE, "{0} DataSource Backend not supported!", configHandler.getString("DataSource.backend"));
                logger.log(Level.SEVERE, "Using FlatFile as DataSource Backend instead");

                if (!(new File(flatfilePath)).exists()) {
                    (new File(flatfilePath)).mkdir();
                }

                logger.log(Level.INFO, "FlatFile Loaded successfully");

                this.databaseManager = new DatabaseManagerFlatFile(this);
                this.moneyDatabaseInterface = new BankSystemFlatFileInterface(this);
        }

        // Register commands.
        reloadCommand = new ReloadCommand(this);
        balanceCommand = new BalanceCommand(this);
        setCommand = new SetCommand(this);
        depositCommand = new DepositCommand(this);
        withdrawCommand = new WithdrawCommand(this);
        interestHandler = new InterestHandler(this);
        interestCommand = new InterestCommand(this);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("bank").setExecutor(commandHandler);

        // Check PlaceholderAPI (Soft depend).
        setupPlaceholderAPI();

        new UpdateChecker(this, 61580).getVersion(version -> {
            logger.log(Level.INFO, "Checking for Updates...");

            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.log(Level.INFO, "No new version available");
            } else {
                logger.log(Level.WARNING, "An update for {0} v{1} is available! You are still running BankSystem {2}", new Object[]{getDescription().getName(), version, getDescription().getVersion()});
                logger.log(Level.WARNING, "Download it here: https://www.spigotmc.org/resources/banksystem.61580");
            }
        });

        logger.log(Level.WARNING, "If you found a bug while using this plugin in {0} kindly report it!", new Object[]{this.serverVersion});
        this.pluginEnabled = true;
    }

    @Override
    public void onDisable() {
        if (this.pluginEnabled) {
            Bukkit.getScheduler().cancelTasks(this);
            HandlerList.unregisterAll(this);

            // Check if connected to mysql.
            if (this.databaseManager.getConnection() != null) {
                logger.log(Level.INFO, "Closing MySQL connection...");
                this.databaseManager.closeDatabase();
            }
        }
    }

    private boolean verifyServerVersion() {
        if (serverVersion.matches("^(1.[7-8])+(.[0-9])?$")) {
            this.is18Server = true;
            return true;
        } else if (serverVersion.matches("^(1.9)+(.[0-9])?$")) {
            this.is19Server = true;
            return true;
        } else if (serverVersion.matches("^(1.10)+(.[0-9])?$")) {
            this.is110Server = true;
            return true;
        } else if (serverVersion.matches("^(1.11)+(.[0-9])?$")) {
            this.is111Server = true;
            return true;
        } else if (serverVersion.matches("^(1.12)+(.[0-9])?$")) {
            this.is112Server = true;
            return true;
        } else if (serverVersion.matches("^(1.13)+(.[0-9])?$")) {
            this.is113Server = true;
            return true;
        } else if (serverVersion.matches("^(1.14)+(.[0-9])?$")) {
            this.is114Server = true;
            return true;
        } else if (serverVersion.matches("^(1.15)+(.[0-9])?$")) {
            this.is115Server = true;
            return true;
        } else if (serverVersion.matches("^(1.16)+(.[0-9])?$")) {
            this.is116Server = true;
            return true;
        }

        logger.log(Level.SEVERE, "Not supported version, disabling plugin!");
        getServer().getPluginManager().disablePlugin(this);
        return false;
    }

    private boolean setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHandler(this).register();
            logger.log(Level.INFO, "PlaceholderAPI hooked.");
            return true;
        }

        logger.log(Level.WARNING, "PlaceholderAPI not found, disabling placeholders!");
        return false;
    }

    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.log(Level.SEVERE, "Vault not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            logger.log(Level.SEVERE, "Economy System not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        economy = rsp.getProvider();
        logger.log(Level.INFO, "Economy provider: {0}", rsp.getProvider().getName());
        return (economy != null);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp == null) {
            logger.log(Level.SEVERE, "Permissions Plugin not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        permission = rsp.getProvider();
        logger.log(Level.INFO, "Permission provider: {0}", rsp.getProvider().getName());
        return (permission != null);
    }

    public ConfigHandler config() { return configHandler; }

    public SoundHandler sound() {
        return soundHandler;
    }

    public InterestHandler getInterestHandler() {
        return interestHandler;
    }

    public UserdataDatabaseInterface<Double> getMoneyDatabaseInterface() {
        return this.moneyDatabaseInterface;
    }

    public DatabaseManagerInterface getDatabaseManagerInterface() {
        return this.databaseManager;
    }

    public ReloadCommand getReloadCmd() {
        return reloadCommand;
    }

    public BalanceCommand getBalanceCmd() {
        return balanceCommand;
    }

    public SetCommand getSetCmd() {
        return setCommand;
    }

    public DepositCommand getDepositCmd() {
        return depositCommand;
    }

    public WithdrawCommand getWithdrawCmd() {
        return withdrawCommand;
    }

    public InterestCommand cmdInterest() { return interestCommand; }
}
