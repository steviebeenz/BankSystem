package com.daishitie.banksystem.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.daishitie.banksystem.BankSystem;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class ReloadCommand {
    private final BankSystem plugin;

    public ReloadCommand(BankSystem pl) {
        this.plugin = pl;
    }

    public boolean runCmd(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (BankSystem.permission.has(p, "banksystem.admin")) {
                try {
                    this.plugin.getConfig().load(new File("plugins" + System.getProperty("file.separator") + this.plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml"));
                } catch (IOException e) {
                    this.plugin.config().printMessage(p, "Messages.reloadError", "0", null, "null", Boolean.valueOf(true));
                    BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                    this.plugin.sound().sendPling(p);
                    return false;
                } catch (InvalidConfigurationException e) {
                    this.plugin.config().printMessage(p, "Messages.reloadError", "0", null, "null", Boolean.valueOf(true));
                    BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
                    this.plugin.sound().sendPling(p);
                    return false;
                }
                this.plugin.getInterestHandler().resetTask();
                this.plugin.config().printMessage(p, "Messages.reloadSuccess", "0", null, "null", Boolean.valueOf(true));
                this.plugin.sound().sendLevelUpSound(p);
                return true;
            }
            this.plugin.config().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
            this.plugin.sound().sendPling(p);
            return false;
        }
        try {
            this.plugin.getConfig().load(new File("plugins" + System.getProperty("file.separator") + this.plugin.getDescription().getName() + System.getProperty("file.separator") + "config.yml"));
        } catch (IOException e) {
            this.plugin.config().printMessage(sender, "Messages.reloadError", "0", null, "null", Boolean.valueOf(true));
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            return false;
        } catch (InvalidConfigurationException e) {
            this.plugin.config().printMessage(sender, "Messages.reloadError", "0", null, "null", Boolean.valueOf(true));
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
            return false;
        }
        this.plugin.getInterestHandler().resetTask();
        this.plugin.config().printMessage(sender, "Messages.reloadSuccess", "0", null, "null", Boolean.valueOf(true));
        return true;
    }
}
