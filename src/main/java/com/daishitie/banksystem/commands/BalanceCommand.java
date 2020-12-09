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
package com.daishitie.banksystem.commands;

import java.util.List;

import com.daishitie.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand {
    private final BankSystem plugin;

    public BalanceCommand(BankSystem plugin) {
        this.plugin = plugin;
    }

    public boolean runPlayerCommand(CommandSender sender) {
        // Check if sender is player.
        if (!(sender instanceof Player)) {
            this.plugin
                .config()
                .printMessage(sender, "Messages.console", "0", null, "null", true);

            return true;
        }

        Player player = (Player) sender;

        // Check if player has "banksystem.command.balance" permission.
        if (!BankSystem.permission.has(player, "banksystem.command.balance")) {
            this.plugin
                .sound()
                .sendPling(player);

            this.plugin
                .config()
                .printMessage(player, "Messages.noPermission", "0", null, "null", true);

            return true;
        }

        // Check if command is in cooldown and if the player has cooldown bypass permission.
        if (
            !BankSystem.permission.has(player, "banksystem.bypass.cooldown")
                && this.plugin.cooldown.contains(player.getUniqueId())
        ) {
            this.plugin
                .sound()
                .sendPling(player);

            this.plugin
                .config()
                .printMessage(player, "Messages.tooFastInteract", null, player, player.getName(), true);

            return true;
        } else {
            this.plugin
                .cooldown
                .add(player.getUniqueId());

            String settingsCooldown = this.plugin
                .config()
                .getString("Settings.interactCooldown");

            if (settingsCooldown == null) {
                settingsCooldown = "0";
            }

            double cooldownFormula = 0.02D * Double.parseDouble(settingsCooldown);
            int cooldown = (int) cooldownFormula;

            Bukkit.getServer()
                .getScheduler()
                .runTaskLaterAsynchronously(this.plugin, () ->
                    BalanceCommand.this.plugin
                        .cooldown
                        .remove(player.getUniqueId()), cooldown);
        }

        List<String> messagesBalance = this.plugin.config().getStringList("Messages.balance");

        assert messagesBalance != null;

        for (String balance : messagesBalance) {
            player.sendMessage(
                this.plugin
                    .config()
                    .parseColorCodes(
                        this.plugin
                            .config()
                            .printBalance(player, balance, player)
                    )
            );
        }

        this.plugin
            .sound()
            .sendClickSound(player);

        return true;
    }

    public boolean runAdminCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;

            if (!BankSystem.permission.has(playerSender, "banksystem.admin")) {
                this.plugin
                    .sound()
                    .sendPling(playerSender);

                this.plugin
                    .config()
                    .printMessage(playerSender, "Messages.noPermission", null, playerSender, playerSender.getName(), true);

                return true;
            }

            final Player playerTarget = Bukkit.getPlayer(args[1]);

            if (playerTarget == null) {
                this.plugin
                    .config()
                    .printMessage(playerSender, "Messages.playerOffline", null, playerSender, args[1], true);

                this.plugin
                    .sound()
                    .sendPling(playerSender);

                return true;
            }

            if (playerTarget.isOnline()) {
                if (!this.plugin.getMoneyDatabaseInterface().hasUserdata(playerTarget)) {
                    this.plugin
                        .config()
                        .printMessage(playerSender, "Messages.noBank", null, playerTarget, playerTarget.getName(), true);

                    return true;
                }

                List<String> messagesBalance = this.plugin.config().getStringList("Messages.balance");

                assert messagesBalance != null;

                for (String balance : messagesBalance) {
                    sender.sendMessage(
                        this.plugin
                            .config()
                            .parseColorCodes(
                                this.plugin
                                    .config()
                                    .printBalance(playerSender, balance, playerTarget)
                            )
                    );
                }

                this.plugin
                    .sound()
                    .sendClickSound(playerSender);

                return true;
            }
        } else {
            Player playerTarget = Bukkit.getPlayer(args[1]);

            if (playerTarget == null) {
                this.plugin
                    .config()
                    .printMessage(sender, "Messages.playerOffline", null, null, args[1], true);

                return true;
            }

            if (playerTarget.isOnline()) {
                if (!this.plugin.getMoneyDatabaseInterface().hasUserdata(playerTarget)) {
                    this.plugin
                        .config()
                        .printMessage(sender, "Messages.noBank", null, playerTarget, playerTarget.getName(), true);

                    return true;
                }

                List<String> messagesBalance = this.plugin.config().getStringList("Messages.balance");

                assert messagesBalance != null;

                for (String balance : messagesBalance) {
                    sender.sendMessage(
                        this.plugin
                            .config()
                            .parseColorCodes(
                                this.plugin
                                    .config()
                                    .printBalance(sender, balance, playerTarget)
                            )
                    );
                }

                return true;
            }
        }

        return true;
    }
}
