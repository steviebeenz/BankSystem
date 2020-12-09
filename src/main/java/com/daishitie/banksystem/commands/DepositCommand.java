package com.daishitie.banksystem.commands;

import com.daishitie.banksystem.BankSystem;
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
            Player p = (Player) sender;
            if (BankSystem.permission.has(p, "banksystem.command.deposit")) {
                if (this.plugin.cooldown.contains(p.getUniqueId())) {
                    this.plugin.config().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
                    this.plugin.sound().sendPling(p);
                    return true;
                }
                if (this.plugin.config().getBoolean("Settings.withdrawDepositAllEnabled").booleanValue() && (args[1]
                        .equalsIgnoreCase("all") || args[1].equals("*"))) {
                    Double amount = Double.valueOf(BankSystem.economy.getBalance(p));
                    Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
                    Double bankLimit = Double.valueOf(Double.parseDouble(this.plugin.config().getString("Settings.bankLimit")));
                    if (amount.doubleValue() <= 0.0D) {
                        this.plugin.config().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
                        return true;
                    }
                    if (bankLimit.doubleValue() - bankBalance.doubleValue() <= 0.0D) {
                        this.plugin.config().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
                        this.plugin.sound().sendPling(p);
                        return true;
                    }
                    if (amount.doubleValue() >= bankLimit.doubleValue()) {
                        Double double_ = Double.valueOf(bankLimit.doubleValue() - bankBalance.doubleValue());
                        BankSystem.economy.withdrawPlayer(p, double_.doubleValue());
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + double_.doubleValue()));
                        this.plugin.config().printMessage(p, "Messages.depositSuccess", double_ + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    Double totalDeposit = Double.valueOf(bankLimit.doubleValue() - bankBalance.doubleValue());
                    if (totalDeposit.doubleValue() >= amount.doubleValue()) {
                        BankSystem.economy.withdrawPlayer(p, amount.doubleValue());
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
                        this.plugin.config().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    BankSystem.economy.withdrawPlayer(p, amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue());
                    this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue()));
                    this.plugin.config().printMessage(p, "Messages.depositSuccess", (amount.doubleValue() - amount.doubleValue() - totalDeposit.doubleValue()) + "", p, p.getName(), Boolean.valueOf(true));
                    setCooldown(sender);
                    return true;
                }
                if (args[1].matches("^[0-9]{1,15}([.][0-9]{1,2})?$")) {
                    Double amount = Double.valueOf(Double.parseDouble(args[1]));
                    if (amount.doubleValue() <= 0.0D) {
                        this.plugin.config().printMessage(p, "Messages.invalidAmount", amount + "", p, p.getName(), Boolean.valueOf(true));
                        return true;
                    }
                    if (BankSystem.economy.getBalance(p) >= amount.doubleValue()) {
                        Double bankBalance = this.plugin.getMoneyDatabaseInterface().getBalance(p);
                        if (bankBalance.doubleValue() + amount.doubleValue() > Double.parseDouble(this.plugin.config().getString("Settings.bankLimit"))) {
                            this.plugin.config().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
                            this.plugin.sound().sendPling(p);
                            return true;
                        }
                        BankSystem.economy.withdrawPlayer(p, amount.doubleValue());
                        this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
                        this.plugin.config().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
                        setCooldown(sender);
                        return true;
                    }
                    this.plugin.config().printMessage(p, "Messages.notEnoughMoney", "0", null, "null", Boolean.valueOf(true));
                    this.plugin.sound().sendPling(p);
                    return true;
                }
                this.plugin.config().printMessage(p, "Messages.invalidAmount", "0", null, "null", Boolean.valueOf(true));
                return true;
            }
            this.plugin.config().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
            this.plugin.sound().sendPling(p);
            return true;
        }
        this.plugin.config().printMessage(sender, "Messages.console", "0", null, "null", Boolean.valueOf(true));
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
                DepositCommand.this.plugin.cooldown.remove(p.getUniqueId());
            }
        }, delay);
    }
}
