package net.twiistrz.banksystem.database;

import org.bukkit.entity.Player;
import java.util.UUID;

public interface UserdataDatabaseInterface<X> {
	public boolean hasUserdata(Player player);
	public boolean hasUserdata(UUID player);
	public boolean createUserdata(Player player);
	public Double getBalance(Player player);
	public Double getBalance(UUID player);
	public boolean setBalance(Player player, Double amount);
	public boolean setBalance(UUID player, Double amount);
}