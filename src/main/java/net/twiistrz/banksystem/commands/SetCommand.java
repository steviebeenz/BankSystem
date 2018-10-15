package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand {

    private final BankSystem plugin;

    public SetCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runCmd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.admin")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (target.isOnline()) {
                        if (plugin.getMoneyDatabaseInterface().hasUserdata(target) == false) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.noBank", "0", target, target.getName(), true);
                            plugin.getSoundHandler().sendPlingSound(p);
                            return false;
                        }
                        if (args[2].matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                            Double amount = Double.parseDouble(args[2]);
                            if (amount < 0) {
                                plugin.getConfigurationHandler().printMessage(sender, "Messages.invalidAmount", "0", null, "null", true);
                                return true;
                            }
                            plugin.getMoneyDatabaseInterface().setBalance(target, amount);
                            plugin.getConfigurationHandler().printMessage(p, "Messages.setCommand", amount.toString(), target, target.getName(), true);
                            plugin.getConfigurationHandler().printMessage(target, "Messages.setCommand", amount.toString(), target, target.getName(), true);
                            plugin.getSoundHandler().sendClickSound(p);
                            return true;
                        }
                        plugin.getConfigurationHandler().printMessage(p, "Messages.invalidAmount", "0", null, "null", true);
                        plugin.getSoundHandler().sendPlingSound(p);
                        return true;
                    }
                } else {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.playerOffline", "0", null, "null", true);
                    return false;
                }
            } else {
                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
                plugin.getSoundHandler().sendPlingSound(p);
                return false;
            }
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                if (target.isOnline()) {
                    if (plugin.getMoneyDatabaseInterface().hasUserdata(target) == false) {
                        plugin.getConfigurationHandler().printMessage(sender, "Messages.noBank", "0", target, target.getName(), true);
                        return false;
                    }
                    if (args[2].matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                        Double amount = Double.parseDouble(args[2]);
                        if (amount < 0) {
                            plugin.getConfigurationHandler().printMessage(sender, "Messages.invalidAmount", "0", null, "null", true);
                            return true;
                        }
                        plugin.getMoneyDatabaseInterface().setBalance(target, amount);
                        plugin.getConfigurationHandler().printMessage(sender, "Messages.setCommand", amount.toString(), target, target.getName(), true);
                        plugin.getConfigurationHandler().printMessage(target, "Messages.setCommand", amount.toString(), target, target.getName(), true);
                        return true;
                    }
                    plugin.getConfigurationHandler().printMessage(sender, "Messages.invalidAmount", "0", null, "null", true);
                    return true;
                }
            } else {
                plugin.getConfigurationHandler().printMessage(sender, "Messages.playerOffline", "0", null, "null", true);
                return false;
            }
        }
        return true;
    }
}
