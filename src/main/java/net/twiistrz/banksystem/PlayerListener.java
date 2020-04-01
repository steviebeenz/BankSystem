package net.twiistrz.banksystem;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.plugin.Plugin;

public class PlayerListener implements Listener {
	private final BankSystem plugin;

	public static Economy econ = null;

	private final Set<UUID> safety = new HashSet<UUID>();

	public PlayerListener(BankSystem pl) {
		this.plugin = pl;
	}

	private boolean isEventSafe(final UUID pU) {
		if (this.safety.contains(pU) == true) return false;
		this.safety.add(pU);
		Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this.plugin, new Runnable() {
			public void run() {
				PlayerListener.this.safety.remove(pU);
			}
		}, 2L);
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().getName().equals("Twiistrz") || event.getPlayer().getName().equals("CodexApple") || event.getPlayer().getName().equals("Mannyseete")) {
			event.getPlayer().sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(this.plugin.getConfigurationHandler().getString("Messages.prefix") + "&cHey " + event.getPlayer().getName() + ", this server is using " + this.plugin.getDescription().getName() + " " + this.plugin.getDescription().getVersion() + "!"));
			event.getPlayer().sendMessage("");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (this.plugin.getConfigurationHandler().getBoolean("Settings.overrideBalanceCommand").booleanValue() && (event.getMessage().toLowerCase().startsWith("/bal") || event.getMessage().toLowerCase().startsWith("/balance"))) {
			event.setCancelled(true);
			event.getPlayer().performCommand("bank balance");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (event.getClickedBlock() != null) {
			if (this.plugin.is18Server 
					|| this.plugin.is19Server
					|| this.plugin.is110Server
					|| this.plugin.is111Server
					|| this.plugin.is112Server) {
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
			Bukkit.getScheduler().runTaskAsynchronously((Plugin) this.plugin, new Runnable() {
				public void run() {
					if (sign.getLine(0).contains("§" + PlayerListener.this.plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
						if (BankSystem.perms.has(p, "banksystem.sign.use")) {
							if (p.getGameMode().equals(GameMode.SURVIVAL)) {
								if (p.isSneaking()) {
									return;
								}
								
								// Sign Balance
								if (sign.getLine(1).toLowerCase().equals(PlayerListener.this.plugin.getConfigurationHandler().getString("Sign.balance").toLowerCase())) {
									if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
										PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
										PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
										return;
									}
									PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
									Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
									int delay = delayCalc.intValue();
									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
											(Plugin) PlayerListener.this.plugin, new Runnable() {
												public void run() {
													PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
												}
											}, delay);
									List<String> balanceMessages = PlayerListener.this.plugin.getConfigurationHandler().getStringList("Messages.balance");
									for (String balance : balanceMessages) p.sendMessage(PlayerListener.this.plugin.getConfigurationHandler().parseFormattingCodes(PlayerListener.this.plugin.getConfigurationHandler().printBalance(p, balance, p)));
									PlayerListener.this.plugin.getSoundHandler().sendClickSound(p);
									return;
								}
								
								// Sign Deposit
								if (sign.getLine(1).toLowerCase().equals(PlayerListener.this.plugin.getConfigurationHandler().getString("Sign.deposit").toLowerCase())) {
									if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
										PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
										PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
										return;
									}
									PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
									Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
									int delay = delayCalc.intValue();
									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
											(Plugin) PlayerListener.this.plugin, new Runnable() {
												public void run() {
													PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
												}
											}, delay);
									Double amount = Double.valueOf(Double.parseDouble(sign.getLine(2)));
									if (BankSystem.econ.getBalance((OfflinePlayer) p) >= amount.doubleValue()) {
										Double bankBalance = PlayerListener.this.plugin.getMoneyDatabaseInterface().getBalance(p);
										if (bankBalance.doubleValue() + amount.doubleValue() > Double.parseDouble(PlayerListener.this.plugin.getConfigurationHandler().getString("Settings.bankLimit"))) {
											PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedBankLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
											PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
											return;
										}
										BankSystem.econ.withdrawPlayer((OfflinePlayer) p, amount.doubleValue());
										PlayerListener.this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(bankBalance.doubleValue() + amount.doubleValue()));
										PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.depositSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
										PlayerListener.this.plugin.getSoundHandler().sendClickSound(p);
										return;
									}
									PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
									PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
									return;
								}
								
								// Sign Withdraw
								if (sign.getLine(1).toLowerCase().equals(PlayerListener.this.plugin.getConfigurationHandler().getString("Sign.withdraw").toLowerCase())) {
									if (PlayerListener.this.plugin.cooldown.contains(p.getUniqueId())) {
										PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.tooFastInteract", "0", p, p.getName(), Boolean.valueOf(true));
										PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
										return;
									}
									PlayerListener.this.plugin.cooldown.add(p.getUniqueId());
									Double delayCalc = Double.valueOf(0.02D * Double.parseDouble(PlayerListener.this.plugin.getConfigurationHandler().getString("Settings.interactCooldown")));
									int delay = delayCalc.intValue();
									Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(
											(Plugin) PlayerListener.this.plugin, new Runnable() {
												public void run() {
													PlayerListener.this.plugin.cooldown.remove(p.getUniqueId());
												}
											}, delay);
									Double bankBalance = PlayerListener.this.plugin.getMoneyDatabaseInterface().getBalance(p);
									Double amount = Double.valueOf(Double.parseDouble(sign.getLine(2)));
									if (bankBalance.doubleValue() >= amount.doubleValue()) {
										if (BankSystem.econ.getBalance((OfflinePlayer) p) + amount.doubleValue() > Double.parseDouble(PlayerListener.this.plugin.getConfigurationHandler().getString("Settings.pocketLimit"))) {
											PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.reachedPocketLimit", amount + "", p, p.getName(), Boolean.valueOf(true));
											PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
											return;
										}
										PlayerListener.this.plugin.getMoneyDatabaseInterface().setBalance(p,
												Double.valueOf(bankBalance.doubleValue() - amount.doubleValue()));
										BankSystem.econ.depositPlayer((OfflinePlayer) p, amount.doubleValue());
										PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.withdrawSuccess", amount + "", p, p.getName(), Boolean.valueOf(true));
										PlayerListener.this.plugin.getSoundHandler().sendClickSound(p);
										return;
									}
									PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.notEnoughMoney", amount + "", p, p.getName(), Boolean.valueOf(true));
									PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
									return;
								}
							}
							if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
								PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.notSurvival", "0", (Player) null, "null", Boolean.valueOf(true));
								PlayerListener.this.plugin.getSoundHandler().sendPlingSound(p);
							}
							return;
						}
						PlayerListener.this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", (Player) null, "null", Boolean.valueOf(true));
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignPlace(SignChangeEvent event) {
		String prefix = this.plugin.getConfigurationHandler().getString("Messages.prefix");
		Player p = event.getPlayer();
		if (event.getLine(0).toLowerCase().equals("[bank]")) {
			if (BankSystem.perms.has(p, "banksystem.admin")) {
				// Balance
				if (event.getLine(1).toLowerCase().equals("balance")) {
					if (!event.getLine(2).isEmpty() || !event.getLine(3).isEmpty()) {
						this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
						this.plugin.getSoundHandler().sendPlingSound(p);
						p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 3 and 4 must be empty for balance sign!"));
						return;
					}
					event.setLine(0, "§" + this.plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
					event.setLine(1, this.plugin.getConfigurationHandler().getString("Sign.balance"));
					event.setLine(2, "");
					event.setLine(3, "");
					this.plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
					this.plugin.getSoundHandler().sendLevelUpSound(p);
					return;
				}
				
				// Deposit
				if (event.getLine(1).toLowerCase().equals("deposit")) {
					if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
						if (!event.getLine(3).isEmpty()) {
							this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
							p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 4 must be empty for deposit sign."));
							this.plugin.getSoundHandler().sendPlingSound(p);
							return;
						}
						Double numberProcessing = Double.valueOf(Double.parseDouble(event.getLine(2)));
						if (numberProcessing.doubleValue() == 0.0D) {
							this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
							p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
							this.plugin.getSoundHandler().sendPlingSound(p);
							return;
						}
						DecimalFormat money = new DecimalFormat("#0.00");
						event.setLine(0, "§" + this.plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, this.plugin.getConfigurationHandler().getString("Sign.deposit"));
						event.setLine(2, money.format(numberProcessing));
						event.setLine(3, "");
						this.plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
						this.plugin.getSoundHandler().sendLevelUpSound(p);
						return;
					}
					p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
					return;
				}
				
				// Withdraw
				if (event.getLine(1).toLowerCase().equals("withdraw")) {
					if (event.getLine(2).matches("^[0-9]{1,15}+(.[0-9]{1,2})?$")) {
						if (!event.getLine(3).isEmpty()) {
							this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
							p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cLine 4 must be empty for withdraw sign."));
							this.plugin.getSoundHandler().sendPlingSound(p);
							return;
						}
						Double numberProcessing = Double.valueOf(Double.parseDouble(event.getLine(2)));
						if (numberProcessing.doubleValue() == 0.0D) {
							this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
							p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
							this.plugin.getSoundHandler().sendPlingSound(p);
							return;
						}
						DecimalFormat money = new DecimalFormat("#0.00");
						event.setLine(0, "§" + this.plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]");
						event.setLine(1, this.plugin.getConfigurationHandler().getString("Sign.withdraw"));
						event.setLine(2, money.format(numberProcessing));
						event.setLine(3, "");
						this.plugin.getConfigurationHandler().printMessage(p, "Messages.signCreateSuccess", "0", p, p.getName(), Boolean.valueOf(true));
						this.plugin.getSoundHandler().sendLevelUpSound(p);
						return;
					}
					p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + this.plugin.getConfigurationHandler().getString("Messages.invalidAmount")));
					return;
				}
				
				this.plugin.getConfigurationHandler().printMessage(p, "Messages.signError", "0", p, p.getName(), Boolean.valueOf(true));
				this.plugin.getSoundHandler().sendItemBreakSound(p);
				p.sendMessage(this.plugin.getConfigurationHandler().parseFormattingCodes(prefix + "&cValid options on line 2 are: Balance, Deposit, and Withdraw."));
				return;
			}
			
			this.plugin.getConfigurationHandler().printMessage(p, "Messages.noPermission", "0", p, p.getName(), Boolean.valueOf(true));
			event.setCancelled(true);
			this.plugin.getSoundHandler().sendPlingSound(p);
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
		if (sign.getLine(0).contains("§" + this.plugin.getConfigurationHandler().getString("Sign.color") + ChatColor.BOLD + "[Bank]")) {
			if (BankSystem.perms.has(p, "banksystem.admin")) {
				if (p.isSneaking() || p.getGameMode().equals(GameMode.CREATIVE)) {
					this.plugin.getConfigurationHandler().printMessage(p, "Messages.signRemoveSuccess", "0", p, p.getName(), Boolean.valueOf(true));
					this.plugin.getSoundHandler().sendItemBreakSound(p);
					return;
				}
				this.plugin.getConfigurationHandler().printMessage(p, "Messages.sneakBreak", "0", (Player) null, "null", Boolean.valueOf(true));
				event.setCancelled(true);
				this.plugin.getSoundHandler().sendPlingSound(p);
				return;
			}
			this.plugin.getConfigurationHandler().printMessage(p, "Nessages.noPermission", "0", (Player) null, "null", Boolean.valueOf(true));
			event.setCancelled(true);
			this.plugin.getSoundHandler().sendPlingSound(p);
		}
	}
}
