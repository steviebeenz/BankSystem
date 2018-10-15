package net.twiistrz.banksystem;

import net.twiistrz.banksystem.commands.*;
import net.twiistrz.banksystem.database.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BankSystem extends JavaPlugin {

    public static Logger logger;
    public static Economy econ = null;
    public static Permission perms = null;
    public boolean is18Server = false;
    public Set<UUID> cooldown = new HashSet<UUID>();

    private static ConfigHandler configHandler;
    private DatabaseManagerInterface databaseManager;
    private UserdataDatabaseInterface<Double> moneyDatabaseInterface;
    private boolean pluginEnabled = false;
    private static SoundHandler soundHandler;
    private static ReloadCommand reloadCommand;
    private static BalanceCommand balanceCommand;
    private static SetCommand setCommand;
    private static DepositCommand depositCommand;
    private static WithdrawCommand withdrawCommand;
    private static InterestHandler interestHandler;
    private static InterestCommand interestCommand;

    @Override
    public void onEnable() {
        logger = getLogger();
        getServerVersion();
        // Setup Vault for economy and permissions
        if (!setupEconomy()) {
            logger.log(Level.SEVERE, "WARNING! Vault or Economy System not found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        // Load Configuration
        configHandler = new ConfigHandler(this);
        soundHandler = new SoundHandler(this);
        // Setup Database
        if (configHandler.getString("DataSource.backend").equalsIgnoreCase("mysql")) {
            // MySQL Datasource
            logger.log(Level.INFO, "Using MySQL as DataSource!");
            databaseManager = new DatabaseManagerMysql(this);
            moneyDatabaseInterface = new BankSystemMysqlInterface(this);
        } else if (configHandler.getString("DataSource.backend").equalsIgnoreCase("flatfile")) {
            // FlatFile
            logger.log(Level.INFO, "Using FlatFile as DataSource!");
            if (!new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata").exists()) {
                (new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).mkdir();
            }
            logger.log(Level.INFO, "Loaded successfully!");
            databaseManager = new DatabaseManagerFlatFile(this);
            moneyDatabaseInterface = new BankSystemFlatFileInterface(this);
        } else {
            // FlatFile
            logger.log(Level.WARNING, "{0} DataSource not found!", configHandler.getString("DataSource.backend"));
            logger.log(Level.INFO, "Using FlatFile as DataSource instead!");
            if (!new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata").exists()) {
                (new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).mkdir();
            }
            logger.log(Level.INFO, "Loaded successfully!");
            databaseManager = new DatabaseManagerFlatFile(this);
            moneyDatabaseInterface = new BankSystemFlatFileInterface(this);
        }
        reloadCommand = new ReloadCommand(this);
        balanceCommand = new BalanceCommand(this);
        setCommand = new SetCommand(this);
        depositCommand = new DepositCommand(this);
        withdrawCommand = new WithdrawCommand(this);
        interestHandler = new InterestHandler(this);
        interestCommand = new InterestCommand(this);
        // Register Listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        getCommand("bank").setExecutor(commandHandler);
        pluginEnabled = true;
        logger.log(Level.INFO, "Enabled {0} {1}!", new Object[]{getDescription().getName(), getDescription().getVersion()});
    }

    @Override
    public void onDisable() {
        if (pluginEnabled == true) {
            Bukkit.getScheduler().cancelTasks(this);
            HandlerList.unregisterAll(this);
            if (databaseManager.getConnection() != null) {
                logger.log(Level.INFO, "Closing MySQL connection...");
                databaseManager.closeDatabase();
            }
        }
        logger.log(Level.INFO, "Disabled {0} {1}!", new Object[]{getDescription().getName(), getDescription().getVersion()});
    }

    private boolean getServerVersion() {
        String[] serverVersion = Bukkit.getBukkitVersion().split("-");
        if (serverVersion[0].matches("1.7.10")
                || serverVersion[0].matches("1.7.9")
                || serverVersion[0].matches("1.7.8")
                || serverVersion[0].matches("1.7.5")
                || serverVersion[0].matches("1.7.2")
                || serverVersion[0].matches("1.8.8")
                || serverVersion[0].matches("1.8.7")
                || serverVersion[0].matches("1.8.6")
                || serverVersion[0].matches("1.8.5")
                || serverVersion[0].matches("1.8.4")
                || serverVersion[0].matches("1.8.3")
                || serverVersion[0].matches("1.8")) {
            is18Server = true;
            return true;
        } else if (serverVersion[0].matches("1.13") || serverVersion[0].matches("1.13.1")) {
            logger.log(Level.SEVERE, "Does not supports 1.13");
            getServer().getPluginManager().disablePlugin(this);
        }
        return false;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        logger.log(Level.INFO, "Economy provider: {0}", rsp.getProvider().getName());
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        logger.log(Level.INFO, "Permission provider: {0}", rsp.getProvider().getName());
        return perms != null;
    }

    public UserdataDatabaseInterface<Double> getMoneyDatabaseInterface() {
        return moneyDatabaseInterface;
    }

    public ConfigHandler getConfigurationHandler() {
        return configHandler;
    }

    public DatabaseManagerInterface getDatabaseManagerInterface() {
        return databaseManager;
    }

    public SoundHandler getSoundHandler() {
        return soundHandler;
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

    public InterestHandler getInterestHandler() {
        return interestHandler;
    }

    public InterestCommand getInterestCmd() {
        return interestCommand;
    }
}
