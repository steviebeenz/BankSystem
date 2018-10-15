package net.twiistrz.banksystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.twiistrz.banksystem.BankSystem;

public class InterestCommand {
    private final BankSystem plugin;

    public InterestCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public void runUserCmd(CommandSender sender) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            if (plugin.getConfigurationHandler().getBoolean("Settings.interestEnabled") == true) {
                if (BankSystem.perms.has(p, "banksystem.command.interest")) {
                    if (plugin.cooldown.contains(p.getUniqueId())) {
                        plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                        plugin.getSoundHandler().sendPlingSound(p);
                        return;
                    }
                    plugin.getConfigurationHandler().printMessage(p, "Messages.interestCommand", "0", null, "null", true);
                    plugin.getSoundHandler().sendLevelUpSound(p);
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
                } else {
                    plugin.getSoundHandler().sendPlingSound(p);
                    plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
                }
            } else {
                plugin.getConfigurationHandler().printMessage(p, "Messages.interestDisabled", "0", null, "null", true);
                plugin.getSoundHandler().sendPlingSound(p);
            }
        } else {
            plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", true);
        }
    }
}
