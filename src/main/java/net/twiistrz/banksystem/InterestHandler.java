package net.twiistrz.banksystem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class InterestHandler {
  private final BankSystem plugin;
  
  private int taskID = -1;
  
  private long lastInterestTime;
  
  public InterestHandler(BankSystem pl) {
    this.plugin = pl;
    interestTask();
  }
  
  public String getNextInterestTime() {
    String timeString = "Error";
    long timeLeftMills = System.currentTimeMillis() - this.lastInterestTime;
    long timecountSecPass = timeLeftMills / 1000L;
    long timecountSec = (this.plugin.getConfigurationHandler().getInteger("Settings.interestCooldown").intValue() * 60) - timecountSecPass;
    int days = 0, hours = 0, minutes = 0, seconds = 0;
    if (timecountSec >= 86400L) {
      days = (int)(timecountSec / 86400L);
      timecountSec %= 86400L;
    } 
    if (timecountSec >= 3600L) {
      hours = (int)(timecountSec / 3600L);
      timecountSec %= 3600L;
    } 
    if (timecountSec >= 60L) {
      minutes = (int)(timecountSec / 60L);
      timecountSec %= 60L;
    } 
    if (timecountSec > 0L)
      seconds = (int)timecountSec; 
    if (days != 0) {
      timeString = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
    } else if (days == 0 && hours == 0 && minutes == 0) {
      timeString = seconds + "s";
    } else if (days == 0 && hours == 0) {
      timeString = minutes + "m " + seconds + "s";
    } else {
      timeString = hours + "h " + minutes + "m " + seconds + "s";
    } 
    return timeString;
  }
  
  public void resetTask() {
    if (this.taskID != -1)
      Bukkit.getScheduler().cancelTask(this.taskID); 
    interestTask();
  }
  
  private void interestTask() {
    if (this.plugin.getConfigurationHandler().getBoolean("Settings.interestEnabled").booleanValue()) {
      BankSystem.logger.log(Level.INFO, "Interest will be given every {0} minute(s).", this.plugin.getConfigurationHandler().getInteger("Settings.interestCooldown"));
      BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, new Runnable() {
            public void run() {
              InterestHandler.this.lastInterestTime = System.currentTimeMillis();
              List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
              if (!onlinePlayers.isEmpty()) {
                for (Player p : onlinePlayers) {
                  Double intPercentage = Double.valueOf(Double.parseDouble(InterestHandler.this.plugin.getConfigurationHandler().getString("Settings.interestPercentage").replace("%", "")));
                  Double balance = InterestHandler.this.plugin.getMoneyDatabaseInterface().getBalance(p);
                  if (balance.doubleValue() > 0.0D && balance.doubleValue() < InterestHandler.this.plugin.getConfigurationHandler().getInteger("Settings.bankLimit").intValue()) {
                    Double interest = Double.valueOf(balance.doubleValue() / 100.0D * intPercentage.doubleValue());
                    if (interest.doubleValue() > 0.01D) {
                      InterestHandler.this.plugin.getMoneyDatabaseInterface().setBalance(p, Double.valueOf(balance.doubleValue() + interest.doubleValue()));
                      InterestHandler.this.plugin.getConfigurationHandler().printMessage(p, "Messages.interest", interest.toString(), p, p.getName(), Boolean.valueOf(true));
                    } 
                  } 
                } 
                onlinePlayers.clear();
              } 
            }
          },20L, this.plugin.getConfigurationHandler().getInteger("Settings.interestCooldown").intValue() * 1200L);
      this.taskID = task.getTaskId();
    } else {
      BankSystem.logger.log(Level.INFO, "Interest is currently disabled.");
    } 
  }
}
