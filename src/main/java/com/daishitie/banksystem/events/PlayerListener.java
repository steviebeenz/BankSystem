package com.daishitie.banksystem.events;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.daishitie.banksystem.BankSystem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final BankSystem plugin;
    private final Set<UUID> safety = new HashSet<>();

    public static Economy economy = null;

    public PlayerListener(BankSystem plugin) {
        this.plugin = plugin;
    }

    private boolean isEventSafe(final UUID uuid) {
        if (this.safety.contains(uuid)) return false;

        this.safety.add(uuid);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, new Runnable() {
            public void run() {
                PlayerListener.this.safety.remove(uuid);
            }
        }, 2L);

        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equalsIgnoreCase("daishitie") || player.getName().equalsIgnoreCase("twiistrz")) {
            player.sendMessage(
                this.plugin.config().parseColorCodes("&cHey " + player.getName() + ", this server is using " + this.plugin.getDescription().getName() + " v" + this.plugin.getDescription().getVersion() + "!")
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        boolean overrideBalanceCommand = this.plugin.config().getBoolean("Settings.overrideBalanceCommand").booleanValue();

        if (overrideBalanceCommand && (event.getMessage().toLowerCase().startsWith("/bal") || event.getMessage().toLowerCase().startsWith("/balance"))) {
            event.setCancelled(true);
            event.getPlayer().performCommand("bank balance");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (event.getClickedBlock() != null) {
            if (
                this.plugin.is18Server
                    || this.plugin.is19Server
                    || this.plugin.is110Server
                    || this.plugin.is111Server
                    || this.plugin.is112Server
            ) {
                if (event.getClickedBlock().getType().equals(Material.valueOf("SIGN_POST"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("WALL_SIGN"))) {
                    this.signClick(event, p);
                }
            }

            if (this.plugin.is113Server) {
                if (event.getClickedBlock().getType().equals(Material.valueOf("SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("WALL_SIGN"))) {
                    this.signClick(event, p);
                }
            }

            if (this.plugin.is114Server || this.plugin.is115Server) {
                if (event.getClickedBlock().getType().equals(Material.valueOf("ACACIA_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("BIRCH_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("DARK_OAK_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("JUNGLE_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("LEGACY_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("OAK_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("SPRUCE_WALL_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("ACACIA_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("BIRCH_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("DARK_OAK_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("JUNGLE_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("LEGACY_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("OAK_SIGN"))
                    || event.getClickedBlock().getType().equals(Material.valueOf("SPRUCE_SIGN"))) {
                    this.signClick(event, p);
                }
            }
        }
    }

    public void signClick(PlayerInteractEvent event, Player p) {
        if (isEventSafe(event.getPlayer().getUniqueId())) {
            final Sign sign = (Sign) event.getClickedBlock().getState();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                public void run() {
                    if (sign.getLine(0).contains("§" + PlayerListener.this.plugin.config().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
                        if (BankSystem.permission.has(p, "banksystem.sign.use")) {
                            if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                                if (p.isSneaking()) {
                                    return;
                                }

                                // Sign Balance
                                if (sign.getLine(1).equalsIgnoreCase(PlayerListener.this.plugin.config().getString("Sign.balance"))) {
                                    if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
                                        PlayerListener.this.plugin.config().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
                                        PlayerListener.this.plugin.sound().sendPling(p);
                                        return;
                                    }
                                    PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
                                    Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.config().getString("Settings.interactCooldown")));
                                    int delay = delayCalc.intValue();
                                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
                                        PlayerListener.this.plugin, new Runnable() {
                                            public void run() {
                                                PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
                                            }
                                        }, delay);
                                    List<String> balanceMessages = PlayerListener.this.plugin.config().getStringList("Messages.balance");
                                    for (String balance : balanceMessages)
                                        p.sendMessage(PlayerListener.this.plugin.config().parseColorCodes(PlayerListener.this.plugin.config().printBalance(p, balance, p)));
                                    PlayerListener.this.plugin.sound().sendClickSound(p);
                                    return;
                                }

                                // Sign Deposit
                                if (sign.getLine(1).equalsIgnoreCase(PlayerListener.this.plugin.config().getString("Sign.deposit"))) {
                                    if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
                                        PlayerListener.this.plugin.config().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
                                        PlayerListener.this.plugin.sound().sendPling(p);
                                        return;
                                    }
                                    PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
                                    Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.config().getString("Settings.interactCooldown")));
                                    int delay = delayCalc.intValue();
                                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
                                        PlayerListener.this.plugin, new Runnable() {
                                            public void run() {
                                                PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
                                            }
                                        }, delay);
                                    Double amount = Double.valueOf(Double.parseDouble(sign.getLine(2)));
                                    if (BankSystem.economy.getBalance(p) >= amount.doubleValue()) {
                                        Double bankBalance = PlayerListener.this.plugin.getMoneyDatabaseInterface().getBalance(p);
                                        if (bankBalance.doubleValue() + amount.doubleValue() > Double.parseDouble(PlayerListener.this.plugin.config().getString("Settings.bankLimit"))) {
                                            PlayerListener.this.plugin.config().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
                                            PlayerListener.this.plugin.sound().sendPling(p);
                                            return;
                                        }
                                        BankSystem.economy.withdrawPlayer(p, amount.doubleValue());
                                        PlayerListener.this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
                                        PlayerListener.this.plugin.config().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
                                        PlayerListener.this.plugin.sound().sendClickSound(p);
                                        return;
                                    }
                                    PlayerListener.this.plugin.config().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
                                    PlayerListener.this.plugin.sound().sendPling(p);
                                    return;
                                }

                                // Sign Withdraw
                                if (sign.getLine(1).equalsIgnoreCase(PlayerListener.this.plugin.config().getString("Sign.withdraw"))) {
                                    if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
                                        PlayerListener.this.plugin.config().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
                                        PlayerListener.this.plugin.sound().sendPling(p);
                                        return;
                                    }
                                    PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
                                    Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.config().getString("Settings.interactCooldown")));
                                    int delay = delayCalc.intValue();
                                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
                                        PlayerListener.this.plugin, new Runnable() {
                                            public void run() {
                                                PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
                                            }
                                        }, delay);
                                    Double bankBalance = PlayerListener.this.plugin.getMoneyDatabaseInterface().getBalance(p);
                                    Double amount = Double.valueOf(Double.parseDouble(sign.getLine(2)));
                                    if (bankBalance.doubleValue() >= amount.doubleValue()) {
                                        if (BankSystem.economy.getBalance(p) + amount.doubleValue() > Double.parseDouble(PlayerListener.this.plugin.config().getString("Settings.pocketLimit"))) {
                                            PlayerListener.this.plugin.config().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
                                            PlayerListener.this.plugin.sound().sendPling(p);
                                            return;
                                        }
                                        PlayerListener.this.plugin.getMoneyDatabaseInterface().setBalance(p,
                                            Double.valueOf(bankBalance.doubleValue() - amount.doubleValue()));
                                        BankSystem.economy.depositPlayer(p, amount.doubleValue());
                                        PlayerListener.this.plugin.config().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
                                        PlayerListener.this.plugin.sound().sendClickSound(p);
                                        return;
                                    }
                                    PlayerListener.this.plugin.config().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
                                    PlayerListener.this.plugin.sound().sendPling(p);
                                    return;
                                }
                            }
                            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                                PlayerListener.this.plugin.config().printMessage(p, "Messages.notSurvival", "0", null, "null", Boolean.valueOf(true));
                                PlayerListener.this.plugin.sound().sendPling(p);
                            }
                            return;
                        }
                        PlayerListener.this.plugin.config().printMessage(p, "Messages.noPermission", "0", null, "null", Boolean.valueOf(true));
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignPlace(SignChangeEvent event) {
        String prefix = this.plugin.config().getString("Messages.prefix");
        Player p = event.getPlayer();
        if (event.getLine(0).equalsIgnoreCase("[bank]")) {
            if (BankSystem.permission.has(p, "banksystem.admin")) {
                // Balance
                if (event.getLine(1).equalsIgnoreCase("balance")) {
                    if (!event.getLine(2).isEmpty() || !event.getLine(3).isEmpty()) {
                        this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                        this.plugin.sound().sendPling(p);
                        p.sendMessage(this.plugin.config().parseColorCodes(prefix + "&cLine 3 and 4 must be empty for balance sign!"));
                        return;
                    }
                    event.setLine(0, "§" + this.plugin.config().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                    event.setLine(1, this.plugin.config().getString("Sign.balance"));
                    event.setLine(2, "");
                    event.setLine(3, "");
                    this.plugin.config().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
                    this.plugin.sound().sendLevelUpSound(p);
                    return;
                }

                // Deposit
                if (event.getLine(1).equalsIgnoreCase("deposit")) {
                    if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                        if (!event.getLine(3).isEmpty()) {
                            this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                            p.sendMessage(this.plugin.config().parseColorCodes(prefix + "&cLine 4 must be empty for deposit sign."));
                            this.plugin.sound().sendPling(p);
                            return;
                        }
                        Double numberProcessing = Double.valueOf(Double.parseDouble(event.getLine(2)));
                        if (numberProcessing.doubleValue() == 0.0D) {
                            this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                            p.sendMessage(this.plugin.config().parseColorCodes(prefix + this.plugin.config().getString("Messages.invalidAmount")));
                            this.plugin.sound().sendPling(p);
                            return;
                        }
                        DecimalFormat money = new DecimalFormat("#0.00");
                        event.setLine(0, "§" + this.plugin.config().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                        event.setLine(1, this.plugin.config().getString("Sign.deposit"));
                        event.setLine(2, money.format(numberProcessing));
                        event.setLine(3, "");
                        this.plugin.config().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
                        this.plugin.sound().sendLevelUpSound(p);
                        return;
                    }
                    p.sendMessage(this.plugin.config().parseColorCodes(prefix + this.plugin.config().getString("Messages.invalidAmount")));
                    return;
                }

                // Withdraw
                if (event.getLine(1).equalsIgnoreCase("withdraw")) {
                    if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
                        if (!event.getLine(3).isEmpty()) {
                            this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                            p.sendMessage(this.plugin.config().parseColorCodes(prefix + "&cLine 4 must be empty for withdraw sign."));
                            this.plugin.sound().sendPling(p);
                            return;
                        }
                        Double numberProcessing = Double.valueOf(Double.parseDouble(event.getLine(2)));
                        if (numberProcessing.doubleValue() == 0.0D) {
                            this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                            p.sendMessage(this.plugin.config().parseColorCodes(prefix + this.plugin.config().getString("Messages.invalidAmount")));
                            this.plugin.sound().sendPling(p);
                            return;
                        }
                        DecimalFormat money = new DecimalFormat("#0.00");
                        event.setLine(0, "§" + this.plugin.config().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
                        event.setLine(1, this.plugin.config().getString("Sign.withdraw"));
                        event.setLine(2, money.format(numberProcessing));
                        event.setLine(3, "");
                        this.plugin.config().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
                        this.plugin.sound().sendLevelUpSound(p);
                        return;
                    }
                    p.sendMessage(this.plugin.config().parseColorCodes(prefix + this.plugin.config().getString("Messages.invalidAmount")));
                    return;
                }

                this.plugin.config().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
                this.plugin.sound().sendItemBreakSound(p);
                p.sendMessage(this.plugin.config().parseColorCodes(prefix + "&cValid options on line 2 are: Balance, Deposit, and Withdraw."));
                return;
            }

            this.plugin.config().printMessage(p, "Messages.noPermission", "0", p, p.getName(), Boolean.valueOf(true));
            event.setCancelled(true);
            this.plugin.sound().sendPling(p);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignRemove(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (this.plugin.is18Server
            || this.plugin.is19Server
            || this.plugin.is110Server
            || this.plugin.is111Server
            || this.plugin.is112Server) {
            if (event.getBlock().getType().equals(Material.valueOf("SIGN_POST"))
                || event.getBlock().getType().equals(Material.valueOf("WALL_SIGN"))) {
                this.signRemove(event, p);
            }
        }

        if (this.plugin.is113Server) {
            if (event.getBlock().getType().equals(Material.valueOf("SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("WALL_SIGN"))) {
                this.signRemove(event, p);
            }
        }

        if (this.plugin.is114Server || this.plugin.is115Server) {
            if (event.getBlock().getType().equals(Material.valueOf("ACACIA_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("BIRCH_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("DARK_OAK_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("JUNGLE_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("LEGACY_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("OAK_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("SPRUCE_WALL_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("ACACIA_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("BIRCH_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("DARK_OAK_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("JUNGLE_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("LEGACY_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("OAK_SIGN"))
                || event.getBlock().getType().equals(Material.valueOf("SPRUCE_SIGN"))) {
                this.signRemove(event, p);
            }
        }
    }


    public void signRemove(BlockBreakEvent event, Player p) {
        Sign sign = (Sign) event.getBlock().getState();
        if (sign.getLine(0).contains("§" + this.plugin.config().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
            if (BankSystem.permission.has(p, "banksystem.admin")) {
                if (p.isSneaking() || p.getGameMode().equals(GameMode.CREATIVE)) {
                    this.plugin.config().printMessage(p, "Messages.signRemoveSuccess", "0", p, p.getName(), Boolean.valueOf(true));
                    this.plugin.sound().sendItemBreakSound(p);
                    return;
                }
                this.plugin.config().printMessage(p, "Messages.sneakBreak", "0", null, "null", Boolean.valueOf(true));
                event.setCancelled(true);
                this.plugin.sound().sendPling(p);
                return;
            }
            this.plugin.config().printMessage(p, "Nessages.noPermission", "0", null, "null", Boolean.valueOf(true));
            event.setCancelled(true);
            this.plugin.sound().sendPling(p);
        }
    }
}
