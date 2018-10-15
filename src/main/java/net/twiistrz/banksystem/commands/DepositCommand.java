package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DepositCommand {
    private final BankSystem plugin;

    public DepositCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runUserCmd(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.command.deposit")) {
                if (plugin.cooldown.contains(p.getUniqueId())) {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return true;
                }
                if (args[1].matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                    Double amount = Double.parseDouble(args[1]);
                    if (amount <= 0) {
                        plugin.getConfigurationHandler().printMessage(p, "Messages.invalidAmount", amount + "", p, p.getName(), true);
                        return true;
                    }
                    if (BankSystem.econ.getBalance(p) >= amount) {
                        Double bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p);
                        if (bankBalance + amount > Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.bankLimit"))) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), true);
                            plugin.getSoundHandler().sendPlingSound(p);
                            return true;
                        }
                        BankSystem.econ.withdrawPlayer(p, amount);
                        plugin.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
                        plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), true);
                        plugin.getSoundHandler().sendClickSound(p);
                        plugin.cooldown.add(p.getUniqueId());
                        Double delayCalc = 0.02D * Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interactCooldown"));
                        int delay = delayCalc.intValue();
                        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                            @Override
                            public void run() {
                                plugin.cooldown.remove(p.getUniqueId());
                            }
                        }, delay);
                        return true;
                    } else {
                        plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", "0", null, "null", true);
                        plugin.getSoundHandler().sendPlingSound(p);
                        return true;
                    }
                } else {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.invalidAmount", "0", null, "null", true);
                    return true;
                }
            } else {
                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
                plugin.getSoundHandler().sendPlingSound(p);
                return true;
            }
        } else {
            plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", true);
        }
        return true;
    }
}
