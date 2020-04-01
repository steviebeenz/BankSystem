package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InterestCommand {
  private final BankSystem plugin;
  
  public InterestCommand(BankSystem pl) {
    this.plugin = pl;
  }
  
  public void runUserCmd(CommandSender sender) {
    if (sender instanceof Player) {
      final Player p = (Player)sender;
      if (this.plugin.getConfigurationHandler().getBoolean("Settings.interestEnabled").booleanValue() == true) {
        if (BankSystem.perms.has(p, "banksystem.command.interest")) {
          if (this.plugin.cooldown.contains(p.getUniqueId())) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
            this.plugin.getSoundHandler().sendPlingSound(p);
            return;
          } 
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.interestCommand", "0", null, "null", Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendLevelUpSound(p);
          this.plugin.cooldown.add(p.getUniqueId());
          Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
          int delay = delayCalc.intValue();
          Bukkit.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, new Runnable() {
                public void run() {
                  InterestCommand.this.plugin.cooldown.remove(p.getUniqueId());
                }
              },  delay);
        } else {
          this.plugin.getSoundHandler().sendPlingSound(p);
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
        } 
      } else {
        this.plugin.getConfigurationHandler().printMessage(p, "Messages.interestDisabled", "0", null, "null", Boolean.valueOf(true));
        this.plugin.getSoundHandler().sendPlingSound(p);
      } 
    } else {
      this.plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", Boolean.valueOf(true));
    } 
  }
}
