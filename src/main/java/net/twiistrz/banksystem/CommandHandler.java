package net.twiistrz.banksystem;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	private final BankSystem plugin;

	public CommandHandler(BankSystem pl) {
		this.plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (args.length) {
		case 0:
			if (sender instanceof Player) {
				Player p = (Player) sender;
				sendHelp(p);
				return true;
			}
			sendConsoleHelp(sender);
			return false;
		case 1:
			if (args[0].equalsIgnoreCase("reload")) {
				this.plugin.getReloadCmd().runCmd(sender);
			} else if (args[0].equalsIgnoreCase("balance")) {
				this.plugin.getBalanceCmd().runUserCmd(sender);
			} else if (args[0].equalsIgnoreCase("interest")) {
				this.plugin.getInterestCmd().runUserCmd(sender);
			} else {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					sendHelp(p);
					return false;
				}
				sendConsoleHelp(sender);
				return false;
			}
			return false;
		case 2:
			if (args[0].equalsIgnoreCase("balance")) {
				this.plugin.getBalanceCmd().runAdminCmd(sender, args);
			} else if (args[0].equalsIgnoreCase("deposit")) {
				this.plugin.getDepositCmd().runUserCmd(sender, args);
			} else if (args[0].equalsIgnoreCase("withdraw")) {
				this.plugin.getWithdrawCmd().runUserCmd(sender, args);
			} else {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					sendHelp(p);
					return true;
				}
				sendConsoleHelp(sender);
				return false;
			}
			return false;
		case 3:
			if (args[0].equalsIgnoreCase("set")) {
				this.plugin.getSetCmd().runCmd(sender, args);
			} else {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					sendHelp(p);
					return true;
				}
				sendConsoleHelp(sender);
				return false;
			}
			return false;
		}
		if (sender instanceof Player) {
			Player p = (Player) sender;
			sendHelp(p);
			return true;
		}
		sendConsoleHelp(sender);
		return false;
	}

	public void sendHelp(Player p) {
		if (BankSystem.perms.has(p, "banksystem.command.balance")
				|| BankSystem.perms.has(p, "banksystem.command.deposit")
				|| BankSystem.perms.has(p, "banksystem.command.withdraw")) {
			List<String> helpMessages = this.plugin.getConfigurationHandler().getStringList("Messages.help");
			for (String help : helpMessages)
				p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(help));
		}
		if (p.hasPermission("banksystem.admin")) {
			List<String> adminMessages = this.plugin.getConfigurationHandler().getStringList("Messages.admin");
			for (String admin : adminMessages)
				p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(admin));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&7Author: &e" + this.plugin.getDescription().getAuthors()));
		}
	}

	public void sendConsoleHelp(CommandSender sender) {
		List<String> adminMessages = this.plugin.getConfigurationHandler().getStringList("Messages.admin");
		for (String admin : adminMessages)
			sender.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(admin));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&7Author: &e" + this.plugin.getDescription().getAuthors()));
	}
}
