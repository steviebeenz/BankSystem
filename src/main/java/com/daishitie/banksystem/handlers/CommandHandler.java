/*  This file is part of BankSystem.

    BankSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BankSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BankSystem. If not, see <http://www.gnu.org/licenses/>.
 */
package com.daishitie.banksystem.handlers;

import java.util.List;

import com.daishitie.banksystem.BankSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    private final BankSystem plugin;

    public CommandHandler(BankSystem plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "reload":
                        this.plugin
                            .getReloadCmd()
                            .runCmd(sender);

                        break;
                    case "balance":
                        this.plugin
                            .getBalanceCmd()
                            .runPlayerCommand(sender);

                        break;
                    case "interest":
                        this.plugin
                            .cmdInterest()
                            .run(sender);

                        break;
                }

//                if (args[0].equalsIgnoreCase("reload")) {
//                    this.plugin.getReloadCmd().runCmd(sender);
//                } else if (args[0].equalsIgnoreCase("balance")) {
//                    this.plugin.getBalanceCmd().runPlayerCommand(sender);
//                } else if (args[0].equalsIgnoreCase("interest")) {
//                    this.plugin.getInterestCmd().runUserCmd(sender);
//                } else {
//                    sendHelp(sender);
//                }

                break;
            case 2:
                if (args[0].equalsIgnoreCase("balance")) {
                    this.plugin.getBalanceCmd().runAdminCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("deposit")) {
                    this.plugin.getDepositCmd().runUserCmd(sender, args);
                } else if (args[0].equalsIgnoreCase("withdraw")) {
                    this.plugin.getWithdrawCmd().runUserCmd(sender, args);
                } else {
                    sendHelp(sender);
                }

                break;
            case 3:
                if (args[0].equalsIgnoreCase("set")) {
                    this.plugin.getSetCmd().runCmd(sender, args);
                } else {
                    sendHelp(sender);
                }

                break;
            default:
                sendHelp(sender);
        }

        return true;
    }

    @SuppressWarnings("unused")
    public void sendHelp(Player player) {
        if (BankSystem.permission.has(player, "banksystem.command.balance")
            || BankSystem.permission.has(player, "banksystem.command.deposit")
            || BankSystem.permission.has(player, "banksystem.command.withdraw")) {
            List<String> helpMessages = this.plugin.config().getStringList("Messages.help");

            for (String help : helpMessages) {
                player.sendMessage(this.plugin.config().parseColorCodes(help));
            }
        }

        if (player.hasPermission("banksystem.admin")) {
            List<String> adminMessages = this.plugin.config().getStringList("Messages.admin");

            for (String admin : adminMessages) {
                player.sendMessage(this.plugin.config().parseColorCodes(admin));
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Author: &e" + this.plugin.getDescription().getAuthors()));
        }
    }

    @SuppressWarnings("unused")
    public void sendHelp(CommandSender sender) {
        List<String> adminMessages = this.plugin.config().getStringList("Messages.admin");

        for (String admin : adminMessages) {
            sender.sendMessage(this.plugin.config().parseColorCodes(admin));
        }

        sender.sendMessage(this.plugin.config().parseColorCodes("&7Author: &e" + this.plugin.getDescription().getAuthors()));
    }
}
