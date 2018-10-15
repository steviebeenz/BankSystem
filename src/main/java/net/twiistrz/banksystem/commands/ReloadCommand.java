package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ReloadCommand {
    private final BankSystem plugin;

    public ReloadCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runCmd(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BankSystem.perms.has(p, "banksystem.admin")) {
                try {
                    plugin.getConfig().load(new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml"));
                } catch (IOException e) {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.reloadError", "0", null, "null", true);
                    BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                    plugin.getSoundHandler().sendPlingSound(p);
                    return false;
                } catch (InvalidConfigurationException e) {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.reloadError", "0", null, "null", true);
                    BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                    plugin.getSoundHandler().sendPlingSound(p);
                    return false;
                }
                plugin.getInterestHandler().resetTask();
                plugin.getConfigurationHandler().printMessage(p, "Messages.reloadSuccess", "0", null, "null", true);
                plugin.getSoundHandler().sendLevelUpSound(p);
                return true;
            }
            plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
            plugin.getSoundHandler().sendPlingSound(p);
            return false;
        } else {
            try {
                plugin.getConfig().load(new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml"));
            } catch (IOException e) {
                plugin.getConfigurationHandler().printMessage(sender, "Messages.reloadError", "0", null, "null", true);
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                return false;
            } catch (InvalidConfigurationException e) {
                plugin.getConfigurationHandler().printMessage(sender, "Messages.reloadError", "0", null, "null", true);
                BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                return false;
            }
            plugin.getInterestHandler().resetTask();
            plugin.getConfigurationHandler().printMessage(sender, "Messages.reloadSuccess", "0", null, "null", true);
            return true;
        }
    }
}
