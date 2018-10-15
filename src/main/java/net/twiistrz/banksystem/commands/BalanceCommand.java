package net.twiistrz.banksystem.commands;

import java.util.List;
import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand {
    private final BankSystem plugin;

    public BalanceCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runUserCmd(CommandSender sender) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.command.balance")) {
                if (plugin.cooldown.contains(p.getUniqueId())) {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return true;
                }
                List<String> balanceMessages = plugin.getConfigurationHandler().getStringList("Messages.balance");
                for (String balance : balanceMessages) {
                    sender.sendMessage(
                        plugin.getConfigurationHandler().parseFormattingCodes(
                            plugin.getConfigurationHandler().printBalance(p, balance, p)
                        )
                    );
                }
                plugin.getSoundHandler().sendClickSound(p);
                // Add player to cooldown
                plugin.cooldown.add(p.getUniqueId());
                Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interactCooldown"));
                int delay = delayCalc.intValue();
                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        //remove player from cooldown
                        plugin.cooldown.remove(p.getUniqueId());
                    }
                }, delay);
                return true;
            } else {
                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
                plugin.getSoundHandler().sendPlingSound(p);
            }
        } else {
            plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", true);
        }
        return true;
    }

    public boolean runAdminCmd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.admin")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (target.isOnline()) {
                        if (plugin.getMoneyDatabaseInterface().hasUserdata(target) == false) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.noBank", "0", target, target.getName(), true);
                            return false;
                        }
                        List<String> balanceMessages = plugin.getConfigurationHandler().getStringList("Messages.balance");
                        for (String balance : balanceMessages) {
                            sender.sendMessage(
                                plugin.getConfigurationHandler().parseFormattingCodes(
                                    plugin.getConfigurationHandler().printBalance(p, balance, target)
                                )
                            );
                        }
                        plugin.getSoundHandler().sendClickSound(p);
                        return true;
                    }
                } else {
                    plugin.getConfigurationHandler().printMessage(sender, "Messages.playerOffline", "0", null, "null", true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return false;
                }
            } else {
                plugin.getSoundHandler().sendPlingSound(p);
                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
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
                    List<String> balanceMessages = plugin.getConfigurationHandler().getStringList("Messages.balance");
                    for (String balance : balanceMessages) {
                        sender.sendMessage(
                            plugin.getConfigurationHandler().parseFormattingCodes(
                                plugin.getConfigurationHandler().printBalance(sender, balance, target)
                            )
                        );
                    }
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
