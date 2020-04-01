package net.twiistrz.banksystem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.twiistrz.banksystem.commands.BalanceCommand;
import net.twiistrz.banksystem.commands.DepositCommand;
import net.twiistrz.banksystem.commands.InterestCommand;
import net.twiistrz.banksystem.commands.ReloadCommand;
import net.twiistrz.banksystem.commands.SetCommand;
import net.twiistrz.banksystem.commands.WithdrawCommand;
import net.twiistrz.banksystem.database.BankSystemFlatFileInterface;
import net.twiistrz.banksystem.database.BankSystemMysqlInterface;
import net.twiistrz.banksystem.database.DatabaseManagerFlatFile;
import net.twiistrz.banksystem.database.DatabaseManagerInterface;
import net.twiistrz.banksystem.database.DatabaseManagerMysql;
import net.twiistrz.banksystem.database.UserdataDatabaseInterface;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BankSystem extends JavaPlugin {
	public static Logger logger;

	public static Economy econ = null;

	public static Permission perms = null;

	public boolean is18Server = false;
	
	public boolean is19Server = false;
	
	public boolean is110Server = false;
	
	public boolean is111Server = false;
	
	public boolean is112Server = false;

	public boolean is113Server = false;

	public boolean is114Server = false;

	public boolean is115Server = false;

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

	public String version = "";

	public void onEnable() {
		logger = getLogger();
		
		// Check Server Version
		if (!getServerVersion()) {
			logger.log(Level.SEVERE, "Not supported version, disabling plugin!");
			getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}
		
		// Check Vault
		if (!setupVault()) {
			logger.log(Level.SEVERE, "Vault not found, disabling plugin!");
			getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}
		
		// Check Economy		
		if (!setupEconomy()) {
			logger.log(Level.SEVERE, "Economy System not found, disabling plugin!");
			getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}
		
		// Check Permissions
		if (!setupPermissions()) {
			logger.log(Level.SEVERE, "Permissions System not found, disabling plugin!");
			getServer().getPluginManager().disablePlugin((Plugin) this);
			return;
		}
		
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
		logger.log(Level.WARNING, "If you found a bug while using this plugin in " + serverVersion[0] + " kindly report it!");
		
		configHandler = new ConfigHandler(this);
		soundHandler = new SoundHandler(this);
		if (configHandler.getString("DataSource.backend").equalsIgnoreCase("mysql")) {
			logger.log(Level.INFO, "Using MySQL as DataSource");
			this.databaseManager = (DatabaseManagerInterface) new DatabaseManagerMysql(this);
			this.moneyDatabaseInterface = (UserdataDatabaseInterface<Double>) new BankSystemMysqlInterface(this);
		} else if (configHandler.getString("DataSource.backend").equalsIgnoreCase("flatfile")) {
			logger.log(Level.INFO, "Using FlatFile as DataSource");
			if (!(new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).exists()) (new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).mkdir();
			logger.log(Level.INFO, "FlatFile Loaded successfully");
			this.databaseManager = (DatabaseManagerInterface) new DatabaseManagerFlatFile(this);
			this.moneyDatabaseInterface = (UserdataDatabaseInterface<Double>) new BankSystemFlatFileInterface(this);
		} else {
			logger.log(Level.SEVERE, "{0} DataSource not found!", configHandler.getString("DataSource.backend"));
			logger.log(Level.SEVERE, "Using FlatFile as DataSource instead");
			if (!(new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).exists()) (new File("plugins" + System.getProperty("file.separator") + getDescription().getName() + System.getProperty("file.separator") + "userdata")).mkdir();
			logger.log(Level.INFO, "FlatFile Loaded successfully");
			this.databaseManager = (DatabaseManagerInterface) new DatabaseManagerFlatFile(this);
			this.moneyDatabaseInterface = (UserdataDatabaseInterface<Double>) new BankSystemFlatFileInterface(this);
		}
		reloadCommand = new ReloadCommand(this);
		balanceCommand = new BalanceCommand(this);
		setCommand = new SetCommand(this);
		depositCommand = new DepositCommand(this);
		withdrawCommand = new WithdrawCommand(this);
		interestHandler = new InterestHandler(this);
		interestCommand = new InterestCommand(this);
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new PlayerListener(this), (Plugin) this);
		CommandHandler commandHandler = new CommandHandler(this);
		getCommand("bank").setExecutor(commandHandler);
		
		// Check PlaceholderAPI
		if (!setupPlaceholderAPI()) {
			logger.log(Level.WARNING, "PlaceholderAPI not found, disabling placeholders!");
		}
		
		this.pluginEnabled = true;
		
		new UpdateChecker(this, 61580).getVersion(version -> {
			logger.log(Level.INFO, "Checking for Updates...");
			if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
				logger.log(Level.INFO, "No new version available");
			} else {
				logger.log(Level.WARNING, "An update for {0} ({1}) is available! You are still running BankSystem {2}.", new Object[] { getDescription().getName(), version, getDescription().getVersion() });
				logger.log(Level.WARNING, "Download it here: https://www.spigotmc.org/resources/1-8-1-12-banksystem.61580/");
			}
		});
	}

	public void onDisable() {
		if (this.pluginEnabled == true) {
			Bukkit.getScheduler().cancelTasks((Plugin) this);
			HandlerList.unregisterAll((Plugin) this);
			if (this.databaseManager.getConnection() != null) {
				logger.log(Level.INFO, "Closing MySQL connection...");
				this.databaseManager.closeDatabase();
			}
		}
		logger.log(Level.INFO, "Disabled {0} {1}!", new Object[] { getDescription().getName(), getDescription().getVersion() });
	}

	private boolean getServerVersion() {
		String[] serverVersion = Bukkit.getBukkitVersion().split("-");
		if (serverVersion[0].matches("^(1.[7-8])+(.[0-9])?$")) {
			this.is18Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.9)+(.[0-9])?$")) {
			this.is19Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.10)+(.[0-9])?$")) {
			this.is110Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.11)+(.[0-9])?$")) {
			this.is111Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.12)+(.[0-9])?$")) {
			this.is112Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.13)+(.[0-9])?$")) {
			this.is113Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.14)+(.[0-9])?$")) {
			this.is114Server = true;
			return true;
		}
		
		if (serverVersion[0].matches("^(1.15)+(.[0-9])?$")) {
			this.is115Server = true;
			return true;
		}
		return false;
	}

	private boolean setupPlaceholderAPI() {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderHandler(this).register();
			logger.log(Level.INFO, "PlaceholderAPI hooked.");
			return true;
		}
		return false;
	}
	
	private boolean setupVault() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		return true;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) return false;
		econ = (Economy) rsp.getProvider();
		logger.log(Level.INFO, "Economy provider: {0}", ((Economy) rsp.getProvider()).getName());
		return (econ != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) return false;
		perms = (Permission) rsp.getProvider();
		logger.log(Level.INFO, "Permission provider: {0}", ((Permission) rsp.getProvider()).getName());
		return (perms != null);
	}

	public UserdataDatabaseInterface<Double> getMoneyDatabaseInterface() {
		return this.moneyDatabaseInterface;
	}

	public ConfigHandler getConfigurationHandler() {
		return configHandler;
	}

	public DatabaseManagerInterface getDatabaseManagerInterface() {
		return this.databaseManager;
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
