package net.twiistrz.banksystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
        long timeLeftMills = System.currentTimeMillis() - lastInterestTime;
        long timecountSecPass = timeLeftMills / 1000;
        long timecountSec = plugin.getConfigurationHandler().getInteger("Settings.interestCooldown") * 60 - timecountSecPass;
        int days = 0, hours = 0, minutes = 0, seconds = 0;
        if (timecountSec >= 86400) { 
            days = (int) (timecountSec / 86400);
            timecountSec = timecountSec % 86400;
        }
        if (timecountSec >= 3600) {
            hours = (int) (timecountSec / 3600);
            timecountSec = timecountSec % 3600;
        }
        if (timecountSec >= 60) {
            minutes = (int) (timecountSec / 60);
            timecountSec = timecountSec % 60;
        }
        if (timecountSec > 0) {
            seconds = (int) timecountSec;
        }

        if (days != 0) {
            timeString = days + " d " + hours + " h " + minutes + " m " + seconds + " s";
        } else if (days == 0 && hours == 0 && minutes == 0) {
            timeString = seconds + " s";
        } else if (days == 0 && hours == 0) {
            timeString = minutes + " m " + seconds + " s";
        } else if (days == 0) {
            timeString = hours + " h " + minutes + " m " + seconds + " s";
        }
        return timeString;
    }

    public void resetTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
        interestTask();
    }

    // Interest task
    private void interestTask() {
        if (plugin.getConfigurationHandler().getBoolean("Settings.interestEnabled")) {
            BankSystem.logger.log(Level.INFO, "Interest will be given every {0} minute(s).", plugin.getConfigurationHandler().getInteger("Settings.interestCooldown"));
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    lastInterestTime = System.currentTimeMillis();
                    List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
                    if (!onlinePlayers.isEmpty()) {
                        for (Player p : onlinePlayers) {
                            Double intPercentage = Double.parseDouble(plugin.getConfigurationHandler().getString("Settings.interestPercentage").replace("%", ""));
                            Double balance = plugin.getMoneyDatabaseInterface().getBalance(p);
                            if (balance < plugin.getConfigurationHandler().getInteger("Settings.bankLimit")) {
                                Double interest = (balance / 100) * intPercentage;
                                plugin.getMoneyDatabaseInterface().setBalance(p, balance + interest);
                                plugin.getConfigurationHandler().printMessage(p, "Messages.interest", interest.toString(), p, p.getName(), true);
                            }
                        }
                        onlinePlayers.clear();
                    }
                }

            }, 20L, plugin.getConfigurationHandler().getInteger("Settings.interestCooldown") * (60 * 20L));
            taskID = task.getTaskId();
        } else {
            BankSystem.logger.log(Level.INFO, "Interest is currently disabled.");
        }
    }
}
