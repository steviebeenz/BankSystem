package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DepositCommand {
  private final BankSystem plugin;
  
  public DepositCommand(BankSystem pl) {
    this.plugin = pl;
  }
  
  public boolean runUserCmd(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (BankSystem.perms.has(p, "banksystem.command.deposit")) {
        if (this.plugin.cooldown.contains(p.getUniqueId())) {
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return true;
        } 
        if (this.plugin.getConfigurationHandler().getBoolean("Settings.withdrawDepositAllEnabled").booleanValue() && (args[1]
          .equalsIgnoreCase("all") || args[1].equals("*"))) {
          Double amount = Double.valueOf(BankSystem.econ.getBalance((OfflinePlayer)p));
          Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
          Double bankLimit = Double.valueOf(Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.bankLimit")));
          if (amount.doubleValue() <= 0.0D) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
            return true;
          } 
          if (bankLimit.doubleValue() - bankBalance.doubleValue() <= 0.0D) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
            this.plugin.getSoundHandler().sendPlingSound(p);
            return true;
          } 
          if (amount.doubleValue() >= bankLimit.doubleValue()) {
            Double double_ = Double.valueOf(bankLimit.doubleValue() - bankBalance.doubleValue());
            BankSystem.econ.withdrawPlayer((OfflinePlayer)p, double_.doubleValue());
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + double_.doubleValue()));
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", double_ + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          Double totalDeposit = Double.valueOf(bankLimit.doubleValue() - bankBalance.doubleValue());
          if (totalDeposit.doubleValue() >= amount.doubleValue()) {
            BankSystem.econ.withdrawPlayer((OfflinePlayer)p, amount.doubleValue());
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          BankSystem.econ.withdrawPlayer((OfflinePlayer)p, amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue());
          this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue()));
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", (amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue()) + "", p, p.getName(), Boolean.valueOf(true));
          setCooldown(sender);
          return true;
        } 
        if (args[1].matches("^[0-9]{1,15}([.][0-9]{1,2})?$")) {
          Double amount = Double.valueOf(Double.parseDouble(args[1]));
          if (amount.doubleValue() <= 0.0D) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.invalidAmount", amount + "", p, p.getName(), Boolean.valueOf(true));
            return true;
          } 
          if (BankSystem.econ.getBalance((OfflinePlayer)p) >= amount.doubleValue()) {
            Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
            if (bankBalance.doubleValue() + amount.doubleValue() > Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.bankLimit"))) {
              this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
              this.plugin.getSoundHandler().sendPlingSound(p);
              return true;
            } 
            BankSystem.econ.withdrawPlayer((OfflinePlayer)p, amount.doubleValue());
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", "0", null, "null", Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return true;
        } 
        this.plugin.getConfigurationHandler().printMessage(p, "Messages.invalidAmount", "0", null, "null", Boolean.valueOf(true));
        return true;
      } 
      this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
      this.plugin.getSoundHandler().sendPlingSound(p);
      return true;
    } 
    this.plugin.getConfigurationHandler().printMessage(sender, "Messages.console", "0", null, "null", Boolean.valueOf(true));
    return true;
  }
  
  public void setCooldown(CommandSender sender) {
    final Player p = (Player)sender;
    this.plugin.getSoundHandler().sendClickSound(p);
    this.plugin.cooldown.add(p.getUniqueId());
    Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
    int delay = delayCalc.intValue();
    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously((Plugin)this.plugin, new Runnable() {
          public void run() {
            DepositCommand.this.plugin.cooldown.remove(p.getUniqueId());
          }
        },  delay);
  }
}
