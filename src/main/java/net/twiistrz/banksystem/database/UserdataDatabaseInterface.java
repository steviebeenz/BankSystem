package net.twiistrz.banksystem.database;

import java.util.UUID;
import org.bukkit.entity.Player;

public interface UserdataDatabaseInterface<X> {
  boolean hasUserdata(Player paramPlayer);
  
  boolean hasUserdata(UUID paramUUID);
  
  boolean createUserdata(Player paramPlayer);
  
  Double getBalance(Player paramPlayer);
  
  Double getBalance(UUID paramUUID);
  
  boolean setBalance(Player paramPlayer, Double paramDouble);
  
  boolean setBalance(UUID paramUUID, Double paramDouble);
}
