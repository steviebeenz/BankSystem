package net.twiistrz.banksystem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.event.block.Action;

public class PlayerListener implements Listener {
    private final BankSystem plugin;
    public static Economy econ = null;
    private final Set<UUID> safety = new HashSet<UUID>();

    /**
     *
     * @param pl Main Class
     */
    public PlayerListener(BankSystem pl) {
        this.plugin = pl;
    }

    private boolean isEventSafe(final UUID pU) {
        if (safety.contains(pU) == true) {
            return false;
        }
        safety.add(pU);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                safety.remove(pU);
            }
        }, 2L);
        return true;
    }

    /**
     *
     * @param event Interaction event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().equals(Material.WALL_SIGN) ||
                event.getClickedBlock().getType().equals(Material.SIGN) ||
                event.getClickedBlock().getType().equals(Material.SIGN_POST)) {
                if (isEventSafe(event.getPlayer().getUniqueId())) {
                    final Sign sign = (Sign) event.getClickedBlock().getState();
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            if (sign.getLine(0).contains(plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
                                if (BankSystem.perms.has(p, "banksystem.sign.use")) {
                                    if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                                        if (p.isSneaking()) {
                                            plugin.getSoundHandler().sendPlingSound(p);
                                            return;
                                        }
                                        // Balance Signs
                                        if ((sign.getLine(1).equals(plugin.getConfigurationHandler().getString("Sign.balance")))) {
                                            if (plugin.cooldown.contains(p.getUniqueId())) {
                                                plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                                                plugin.getSoundHandler().sendPlingSound(p);
                                                return;
                                            } else {
                                                plugin.cooldown.add(p.getUniqueId());
                                                Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interactCooldown"));
                                                int delay = delayCalc.intValue();
                                                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Remove player cooldown
                                                        plugin.cooldown.remove(p.getUniqueId());
                                                    }
                                                }, delay);
                                            }
                                            List<String> balanceMessages = plugin.getConfigurationHandler().getStringList("Messages.balance");
                                            for (String balance : balanceMessages) {
                                                p.sendMessage(
                                                    plugin.getConfigurationHandler().parseFormattingCodes(
                                                        plugin.getConfigurationHandler().printBalance(p, balance, p)
                                                    )
                                                );
                                            }
                                            plugin.getSoundHandler().sendClickSound(p);
                                            return;
                                        }
                                        // Deposit Signs
                                        if ((sign.getLine(1).equals(plugin.getConfigurationHandler().getString("Sign.deposit")))) {
                                            // Check if player is in cooldown
                                            if (plugin.cooldown.contains(p.getUniqueId())) {
                                                plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                                                plugin.getSoundHandler().sendPlingSound(p);
                                                return;
                                            } else {
                                                // Add player to cooldown
                                                plugin.cooldown.add(p.getUniqueId());
                                                Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interactCooldown"));
                                                int delay = delayCalc.intValue();
                                                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //remove player from cooldown
                                                        plugin.cooldown.remove(p.getUniqueId());
                                                    }
                                                }, delay);
                                            }
                                            Double amount = Double.parseDouble(sign.getLine(2));
                                            if (BankSystem.econ.getBalance(p) >= amount) {
                                                Double bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p);
                                                if (bankBalance + amount > Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.bankLimit"))) {
                                                    plugin.getConfigurationHandler().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), true);
                                                    plugin.getSoundHandler().sendPlingSound(p);
                                                    return;
                                                }
                                                BankSystem.econ.withdrawPlayer(p, amount);
                                                plugin.getMoneyDatabaseInterface().setBalance(p, bankBalance + amount);
                                                plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), true);
                                                plugin.getSoundHandler().sendClickSound(p);
                                                return;
                                            }
                                            plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), true);
                                            plugin.getSoundHandler().sendPlingSound(p);
                                            return;
                                        }
                                        //Withdraw Signs
                                        if ((sign.getLine(1).equals(plugin.getConfigurationHandler().getString("Sign.withdraw")))) {
                                            //check if player is in cooldown
                                            if (plugin.cooldown.contains(p.getUniqueId())) {
                                                plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), true);
                                                plugin.getSoundHandler().sendPlingSound(p);
                                                return;
                                            } else {
                                                //add player to cooldown
                                                plugin.cooldown.add(p.getUniqueId());
                                                Double delayCalc = 20.00 / 1000.00 * Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interactCooldown"));
                                                int delay = delayCalc.intValue();
                                                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //remove player from cooldown
                                                        plugin.cooldown.remove(p.getUniqueId());
                                                    }
                                                }, delay);
                                            }
                                            Double bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p);
                                            Double amount = Double.parseDouble(sign.getLine(2));
                                            if (bankBalance >= amount) {
                                                if (BankSystem.econ.getBalance(p) + amount > Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.pocketLimit"))) {
                                                    plugin.getConfigurationHandler().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), true);
                                                    plugin.getSoundHandler().sendPlingSound(p);
                                                    return;
                                                }
                                                plugin.getMoneyDatabaseInterface().setBalance(p, bankBalance - amount);

                                                BankSystem.econ.depositPlayer(p, amount);
                                                plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), true);
                                                plugin.getSoundHandler().sendClickSound(p);
                                                return;
                                            }
                                            plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), true);
                                            plugin.getSoundHandler().sendPlingSound(p);
                                            return;
                                        }
                                    }
                                    // if right click
                                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                                        plugin.getConfigurationHandler().printMessage(p, "Messages.notSurvival", "0", null, "null", true);
                                        plugin.getSoundHandler().sendPlingSound(p);
                                    }
                                    return;
                                }
                                plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", null, "null", true);
                            }
                        }

                    });
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignPlace(SignChangeEvent event) {
        String prefix = plugin.getConfigurationHandler().getString("Messages.prefix");
        Player p = event.getPlayer();
        if (event.getLine(0).contains("[BS]")) {            
            if (BankSystem.perms.has(p, "banksystem.admin")) {
                // Balance signs
                if (event.getLine(1).toLowerCase().contains("balance")) {
                    if (!event.getLine(2).isEmpty() || !event.getLine(3).isEmpty()) {
                        plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                        plugin.getSoundHandler().sendPlingSound(p);
                        p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 3 and 4 must be empty for balance sign!"));
                        return;
                    }
                    event.setLine(0, "§" + plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                    event.setLine(1, plugin.getConfigurationHandler().getString("Sign.balance"));
                    event.setLine(2, "");
                    event.setLine(3, "");
                    plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), true);
                    plugin.getSoundHandler().sendLevelUpSound(p);
                    return;
                }
                // Deposit signs
                if (event.getLine(1).toLowerCase().contains("deposit")) {
                    //check if number format is ok on line 3
                    if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                        //check if line 3 is not empty
                        if (!event.getLine(3).isEmpty()) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                            p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 4 must be empty for deposit sign."));
                            plugin.getSoundHandler().sendPlingSound(p);
                            return;
                        }
                        //convert from string to double to format the number
                        Double numberProcessing = Double.parseDouble(event.getLine(2));
                        //if processed number = 0 cancel
                        if (numberProcessing == 0) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                            p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                            plugin.getSoundHandler().sendPlingSound(p);
                            return;
                        }
                        DecimalFormat money = new DecimalFormat("#0.00");
                        event.setLine(0, "§" + plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                        event.setLine(1, plugin.getConfigurationHandler().getString("Sign.deposit"));
                        event.setLine(2, money.format(numberProcessing));
                        event.setLine(3, "");
                        plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), true);
                        plugin.getSoundHandler().sendLevelUpSound(p);
                        return;
                    }
                    p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                    return;
                }
                //Withdraw signs
                if (event.getLine(1).toLowerCase().contains("withdraw")) {
                    //check if number format is ok on line 3
                    if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                        //check if line 3 is not empty
                        if (!event.getLine(3).isEmpty()) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                            p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 4 must be empty for withdraw sign."));
                            plugin.getSoundHandler().sendPlingSound(p);
                            return;
                        }
                        //convert from string to double to format the number
                        Double numberProcessing = Double.parseDouble(event.getLine(2));
                        //if processed number = 0 cancel
                        if (numberProcessing == 0) {
                            plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                            p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                            plugin.getSoundHandler().sendPlingSound(p);
                            return;
                        }
                        DecimalFormat money = new DecimalFormat("#0.00");
                        event.setLine(0, "§" + plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                        event.setLine(1, plugin.getConfigurationHandler().getString("Sign.withdraw"));
                        event.setLine(2, money.format(numberProcessing));
                        event.setLine(3, "");
                        plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), true);
                        plugin.getSoundHandler().sendLevelUpSound(p);
                        return;
                    }
                    p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
                    return;
                } else {
                    plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), true);
                    plugin.getSoundHandler().sendItemBreakSound(p);
                    p.sendMessage(plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cValid options on line 2 are: Balance, Deposit, and Withdraw."));
                    return;
                }
            }
            plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", p, p.getName(), true);
            event.setCancelled(true);
            plugin.getSoundHandler().sendPlingSound(p);
        }
    }

    /**
     *
     * @param event Block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignRemove(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (event.getBlock().getType().equals(Material.WALL_SIGN) ||
            event.getBlock().getType().equals(Material.SIGN) ||
            event.getBlock().getType().equals(Material.SIGN_POST)) {
            //check the found sign for the players name.
            Sign sign = (Sign) event.getBlock().getState();
            if (sign.getLine(0).contains(plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
                if (BankSystem.perms.has(p, "banksystem.admin")) {
                    //Check if sneaking
                    if (p.isSneaking() || p.getGameMode().equals(GameMode.CREATIVE)) {
                        plugin.getConfigurationHandler().printMessage(p, "Messages.signRemoveSuccess", "0", p, p.getName(), true);
                        plugin.getSoundHandler().sendItemBreakSound(p);
                        return;
                    }
                    plugin.getConfigurationHandler().printMessage(p, "Messages.sneakBreak", "0", null, "null", true);
                    event.setCancelled(true);
                    plugin.getSoundHandler().sendPlingSound(p);
                    return;
                }
                plugin.getConfigurationHandler().printMessage(p, "Nessages.noPermission", "0", null, "null", true);
                event.setCancelled(true);
                plugin.getSoundHandler().sendPlingSound(p);
            }
        }
    }
}
