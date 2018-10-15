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

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        Player p;
        switch (args.length) {
            case 0:
                if (sender instanceof Player) {
                    p = (Player) sender;
                    sendHelp(p);
                    return true;
                } else {
                    sendConsoleHelp(sender);
                    return false;
                }
            case 1:
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.getReloadCmd().runCmd(sender);
                } else if (args[0].equalsIgnoreCase("balance")) {
                    plugin.getBalanceCmd().runUserCmd(sender);
                } else if (args[0].equalsIgnoreCase("interest")) {
                    plugin.getInterestCmd().runUserCmd(sender);
                } else {
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        sendHelp(p);
                        return false;
                    } else {
                        sendConsoleHelp(sender);
                        return false;
                    }
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("balance")) {
                    plugin.getBalanceCmd().runAdminCmd(sender, args);
                } else if (args[0].equalsIgnoreCase("deposit")) {
                    plugin.getDepositCmd().runUserCmd(sender, args);
                } else if (args[0].equalsIgnoreCase("withdraw")) {
                    plugin.getWithdrawCmd().runUserCmd(sender, args);
                } else {
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        sendHelp(p);
                        return true;
                    } else {
                        sendConsoleHelp(sender);
                        return false;
                    }
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("set")) {
                    plugin.getSetCmd().runCmd(sender, args);
                } else {
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        sendHelp(p);
                        return true;
                    } else {
                        sendConsoleHelp(sender);
                        return false;
                    }
                }
                break;
            default:
                if (sender instanceof Player) {
                    p = (Player) sender;
                    sendHelp(p);
                    return true;
                } else {
                    sendConsoleHelp(sender);
                    return false;
                }
        }
        return false;
    }

    public void sendHelp(Player p) {
        if (BankSystem.perms.has(p, "banksystem.command.balance") ||
            BankSystem.perms.has(p, "banksystem.command.deposit") ||
            BankSystem.perms.has(p, "banksystem.command.withdraw")) {
            List<String> helpMessages = plugin.getConfigurationHandler().getStringList("Messages.help");
            for (String help : helpMessages) {
                p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(help));
            }
        }
        if (p.hasPermission("banksystem.admin")) {
            List<String> adminMessages = plugin.getConfigurationHandler().getStringList("Messages.admin");
            for (String admin : adminMessages) {
                p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(admin));
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Author: &eTwiistrz"));
        }
    }

    public void sendConsoleHelp(CommandSender sender) {    
        List<String> adminMessages = plugin.getConfigurationHandler().getStringList("Messages.admin");
        for (String admin : adminMessages) {
            sender.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(admin));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Author: &eTwiistrz"));
    }
}
