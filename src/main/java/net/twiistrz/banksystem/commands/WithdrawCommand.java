package net.twiistrz.banksystem.commands;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WithdrawCommand {
  private final BankSystem plugin;
  
  public WithdrawCommand(BankSystem pl) {
    this.plugin = pl;
  }
  
  public boolean runUserCmd(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (BankSystem.perms.has(p, "banksystem.command.withdraw")) {
        if (this.plugin.cooldown.contains(p.getUniqueId())) {
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return true;
        } 
        Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
        if (this.plugin.getConfigurationHandler().getBoolean("Settings.withdrawDepositAllEnabled").booleanValue() && (args[1]
          .equalsIgnoreCase("all") || args[1].equals("*"))) {
          Double amount = Double.valueOf(BankSystem.econ.getBalance((OfflinePlayer)p));
          Double pocketLimit = Double.valueOf(Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.pocketLimit")));
          if (bankBalance.doubleValue() <= 0.0D) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
            return true;
          } 
          if (pocketLimit.doubleValue() - amount.doubleValue() <= 0.0D) {
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedPocketLimit", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
            this.plugin.getSoundHandler().sendPlingSound(p);
            return true;
          } 
          if (bankBalance.doubleValue() >= pocketLimit.doubleValue()) {
            Double double_ = Double.valueOf(pocketLimit.doubleValue() - amount.doubleValue());
            BankSystem.econ.depositPlayer((OfflinePlayer)p, double_.doubleValue());
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - double_.doubleValue()));
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", double_ + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          Double totalWithdraw = Double.valueOf(pocketLimit.doubleValue() - amount.doubleValue());
          if (totalWithdraw.doubleValue() >= bankBalance.doubleValue()) {
            BankSystem.econ.depositPlayer((OfflinePlayer)p, bankBalance.doubleValue());
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - bankBalance.doubleValue()));
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          BankSystem.econ.depositPlayer((OfflinePlayer)p, bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue());
          this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue()));
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", (bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue()) + "", p, p.getName(), Boolean.valueOf(true));
          setCooldown(sender);
          return true;
        } 
        if (args[1].matches("^[0-9]{1,15}([.][0-9]{1,2})?$")) {
          Double amount = Double.valueOf(Double.parseDouble(args[1]));
          if (amount.doubleValue() <= 0.0D) {
            p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(this.plugin.getConfigurationHandler().getString("Messages.prefix") + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
            return true;
          } 
          if (bankBalance.doubleValue() >= amount.doubleValue()) {
            if (BankSystem.econ.getBalance((OfflinePlayer)p) + amount.doubleValue() > Double.parseDouble(this.plugin.getConfigurationHandler().getString("Settings.pocketLimit"))) {
              this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
              this.plugin.getSoundHandler().sendPlingSound(p);
              return true;
            } 
            this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - amount.doubleValue()));
            BankSystem.econ.depositPlayer((OfflinePlayer)p, amount.doubleValue());
            this.plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
            setCooldown(sender);
            return true;
          } 
          this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
          this.plugin.getSoundHandler().sendPlingSound(p);
          return true;
        } 
        p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(this.plugin.getConfigurationHandler().getString("Messages.prefix") + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
        return true;
      } 
      this.plugin.getSoundHandler().sendPlingSound(p);
      this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", p, p.getName(), Boolean.valueOf(true));
      return true;
    } 
    sender.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(this.plugin.getConfigurationHandler().getString("Messages.prefix") + this.plugin.getConfigurationHandler().getString("Messages.console")));
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
            WithdrawCommand.this.plugin.cooldown.remove(p.getUniqueId());
          }
        },  delay);
  }
}
