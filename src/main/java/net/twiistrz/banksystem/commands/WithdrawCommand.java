package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand {
    private final BankSystem plugin;

    public WithdrawCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runUserCmd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.command.withdraw")) {
                //check if player is in cooldown
                if (plugin.cooldown.contains(p.getUniqueId())) {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return true;
                }
                Double bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p);
                if (args[1].matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                    Double amount = Double.parseDouble(args[1]);
                    if (amount <= 0) {
                        p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(plugin.getConfigurationHandler().getString("Messages.prefix") + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                        return true;
                    }
                    if (bankBalance >= amount) {
                        if (BankSystem.econ.getBalance(p) + amount > Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.pocketLimit"))) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), true);
                            plugin.getSoundHandler().sendPlingSound(p);
                            return true;
                        }
                        plugin.getMoneyDatabaseInterface().setBalance(p, bankBalance - amount);
                        BankSystem.econ.depositPlayer(p, amount);
                        plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), true);
                        plugin.getSoundHandler().sendClickSound(p);
                        //add player to cooldown
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
                    }
                    plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return true;
                } else {
                    p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(plugin.getConfigurationHandler().getString("Messages.prefix") + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                    return true;
                }
            } else {
                plugin.getSoundHandler().sendPlingSound(p);
                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", p, p.getName(), true);
                return true;
            }
        } else {
            sender.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(plugin.getConfigurationHandler().getString("Messages.prefix") + plugin.getConfigurationHandler().getString("Messages.console")));
        }
        return true;
    }
}
