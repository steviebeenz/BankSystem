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

import com.daishitie.banksystem.BankSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InterestCommand {
    private final BankSystem plugin;

    public InterestCommand(BankSystem plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    public void run(Player player) {
        boolean enabled = this.plugin.config().getBoolean("Settings.interestEnabled");

        if (!enabled) {
            this.plugin
                .config()
                .printMessage(player, "Messages.interestDisabled", "0", player, player.getName(), true);

            this.plugin
                .sound()
                .sendPling(player);

            return;
        }

        if (!BankSystem.permission.has(player, "banksystem.command.interest")) {
            this.plugin
                .sound()
                .sendPling(player);

            this.plugin
                .config()
                .printMessage(player, "Messages.noPermission", "0", player, player.getName(), true);

            return;
        }

        if (BankSystem.permission.has(player, "banksystem.command.interest")) {
            if (this.plugin.cooldown.contains(player.getUniqueId())) {
                this.plugin
                    .config()
                    .printMessage(player, "Messages.tooFastInteract", "0", player, player.getName(), true);

                this.plugin
                    .sound()
                    .sendPling(player);

                return;
            }

            this.plugin.config().printMessage(player, "Messages.interestCommand", "0", null, "null", Boolean.valueOf(true));
            this.plugin.sound().sendLevelUpSound(player);
            this.plugin.cooldown.add(player.getUniqueId());
            double delayCalc = 0.02D * Double.parseDouble(this.plugin.config().getString("Settings.interactCooldown"));
            int delay = (int) delayCalc;
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
                public void run() {
                    InterestCommand.this.plugin.cooldown.remove(player.getUniqueId());
                }
            }, delay);
        }
    }

    @SuppressWarnings("unused")
    public void run(CommandSender sender) {
        this.plugin
            .config()
            .printMessage(sender, "Messages.console", "0", null, null, true);
    }
}
