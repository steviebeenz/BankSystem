package com.daishitie.banksystem.commands;

import com.daishitie.banksystem.BankSystem;
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
            Player p = (Player) sender;
            if (BankSystem.permission.has(p, "banksystem.command.withdraw")) {
                if (this.plugin.cooldown.contains(p.getUniqueId())) {
                    this.plugin.config().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
                    this.plugin.sound().sendPling(p);
                    return true;
                }
                Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
                if (this.plugin.config().getBoolean("Settings.withdrawDepositAllEnabled").booleanValue() && (args[1]
                        .equalsIgnoreCase("all") || args[1].equals("*"))) {
                    Double amount = Double.valueOf(BankSystem.economy.getBalance(p));
                    Double pocketLimit = Double.valueOf(Double.parseDouble(this.plugin.config().getString("Settings.pocketLimit")));
                    if (bankBalance.doubleValue() <= 0.0D) {
                        this.plugin.config().printMessage(p, "Messages.notEnoughMoney", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
                        return true;
                    }
                    if (pocketLimit.doubleValue() - amount.doubleValue() <= 0.0D) {
                        this.plugin.config().printMessage(p, "Messages.reachedPocketLimit", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
                        this.plugin.sound().sendPling(p);
                        return true;
                    }
                    if (bankBalance.doubleValue() >= pocketLimit.doubleValue()) {
                        Double double_ = Double.valueOf(pocketLimit.doubleValue() - amount.doubleValue());
                        BankSystem.economy.depositPlayer(p, double_.doubleValue());
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - double_.doubleValue()));
                        this.plugin.config().printMessage(p, "Messages.withdrawSuccess", double_ + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    Double totalWithdraw = Double.valueOf(pocketLimit.doubleValue() - amount.doubleValue());
                    if (totalWithdraw.doubleValue() >= bankBalance.doubleValue()) {
                        BankSystem.economy.depositPlayer(p, bankBalance.doubleValue());
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - bankBalance.doubleValue()));
                        this.plugin.config().printMessage(p, "Messages.withdrawSuccess", bankBalance + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    BankSystem.economy.depositPlayer(p, bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue());
                    this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue()));
                    this.plugin.config().printMessage(p, "Messages.withdrawSuccess", (bankBalance.doubleValue() + bankBalance.doubleValue() + totalWithdraw.doubleValue()) + "", p, p.getName(), Boolean.valueOf(true));
                    setCooldown(sender);
                    return true;
                }
                if (args[1].matches("^[0-9]{1,15}([.][0-9]{1,2})?$")) {
                    Double amount = Double.valueOf(Double.parseDouble(args[1]));
                    if (amount.doubleValue() <= 0.0D) {
                        p.sendMessage(this.plugin.config().parseColorCodes(this.plugin.config().getString("Messages.prefix") + this.plugin.config().getString("Messages.invalidAmount")));
                        return true;
                    }
                    if (bankBalance.doubleValue() >= amount.doubleValue()) {
                        if (BankSystem.economy.getBalance(p) + amount.doubleValue() > Double.parseDouble(this.plugin.config().getString("Settings.pocketLimit"))) {
                            this.plugin.config().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
                            this.plugin.sound().sendPling(p);
                            return true;
                        }
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() - amount.doubleValue()));
                        BankSystem.economy.depositPlayer(p, amount.doubleValue());
                        this.plugin.config().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    this.plugin.config().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
                    this.plugin.sound().sendPling(p);
                    return true;
                }
                p.sendMessage(this.plugin.config().parseColorCodes(this.plugin.config().getString("Messages.prefix") + this.plugin.config().getString("Messages.invalidAmount")));
                return true;
            }
            this.plugin.sound().sendPling(p);
            this.plugin.config().printMessage(p, "Messages.noPermission", "0", p, p.getName(), Boolean.valueOf(true));
            return true;
        }
        sender.sendMessage(this.plugin.config().parseColorCodes(this.plugin.config().getString("Messages.prefix") + this.plugin.config().getString("Messages.console")));
        return true;
    }

    public void setCooldown(CommandSender sender) {
        final Player p = (Player) sender;
        this.plugin.sound().sendClickSound(p);
        this.plugin.cooldown.add(p.getUniqueId());
        Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(this.plugin.config().getString("Settings.interactCooldown")));
        int delay = delayCalc.intValue();
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
            public void run() {
                WithdrawCommand.this.plugin.cooldown.remove(p.getUniqueId());
            }
        }, delay);
    }
}
