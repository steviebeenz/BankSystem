package net.twiistrz.banksystem.commands;

import java.util.List;
import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BalanceCommand {
  private final BankSystem plugin;
  
  public BalanceCommand(BankSystem pl) {
    this.plugin = pl;
  }
  
  public boolean runUserCmd(CommandSender sender) {
    if (sender instanceof Player) {
      final Player p = (Player)sender;
      if (BankSystem.perms.has(p, "banksystem.command.balance")) {
        if (this.plugin.cooldown.contains(p.getUniqueId())) {
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return true;
        } 
        List<String> balanceMessages = this.plugin.getConfigurationHandler().getStringList("Messages.balance");
        for (String balance : balanceMessages)
          sender.sendMessage(this.plugin
              .getConfigurationHandler().parseFormattingCodes(this.plugin
                .getConfigurationHandler().printBalance(p, balance, p))); 
        this.plugin.getSoundHandler().sendClickSound(p);
        this.plugin.cooldown.add(p.getUniqueId());
        Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
        int delay = delayCalc.intValue();
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, new Runnable() {
              public void run() {
                BalanceCommand.this.plugin.cooldown.remove(p.getUniqueId());
              }
            },  delay);
        return true;
      } 
      this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
      this.plugin.getSoundHandler().sendPlingSound(p);
    } else {
      this.plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", Boolean.valueOf(true));
    } 
    return true;
  }
  
  public boolean runAdminCmd(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (BankSystem.perms.has(p, "banksystem.admin")) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target != null) {
          if (target.isOnline()) {
            if (!this.plugin.getMoneyDatabaseInterface().hasUserdata(target)) {
              this.plugin.getConfigurationHandler().printMessage(p, "Messages.noBank", "0", target, target.getName(), Boolean.valueOf(true));
              return false;
            } 
            List<String> balanceMessages = this.plugin.getConfigurationHandler().getStringList("Messages.balance");
            for (String balance : balanceMessages)
              sender.sendMessage(this.plugin
                  .getConfigurationHandler().parseFormattingCodes(this.plugin
                    .getConfigurationHandler().printBalance(p, balance, target))); 
            this.plugin.getSoundHandler().sendClickSound(p);
            return true;
          } 
        } else {
          this.plugin.getConfigurationHandler().printMessage(sender, "Messages.playerOffline", "0", null, "null", Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return false;
        } 
      } else {
        this.plugin.getSoundHandler().sendPlingSound(p);
        this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
        return false;
      } 
    } else {
      Player target = Bukkit.getPlayer(args[1]);
      if (target != null) {
        if (target.isOnline()) {
          if (!this.plugin.getMoneyDatabaseInterface().hasUserdata(target)) {
            this.plugin.getConfigurationHandler().printMessage(sender, "Messages.noBank", "0", target, target.getName(), Boolean.valueOf(true));
            return false;
          } 
          List<String> balanceMessages = this.plugin.getConfigurationHandler().getStringList("Messages.balance");
          for (String balance : balanceMessages)
            sender.sendMessage(this.plugin
                .getConfigurationHandler().parseFormattingCodes(this.plugin
                  .getConfigurationHandler().printBalance(sender, balance, target))); 
          return true;
        } 
      } else {
        this.plugin.getConfigurationHandler().printMessage(sender, "Messages.playerOffline", "0", null, "null", Boolean.valueOf(true));
        return false;
      } 
    } 
    return true;
  }
}
