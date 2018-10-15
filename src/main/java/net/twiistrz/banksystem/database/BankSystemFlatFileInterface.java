package net.twiistrz.banksystem.database;

import net.twiistrz.banksystem.BankSystem;
import org.bukkit.entity.Player;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class BankSystemFlatFileInterface implements UserdataDatabaseInterface<Double> {
    private final BankSystem plugin;

    public BankSystemFlatFileInterface(BankSystem pl) {
        this.plugin = pl;
    }

    @Override
    public boolean hasUserdata(Player player) {
        return (new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml")).exists();
    }

    @Override
    public boolean hasUserdata(UUID playerUUID) {
        return (new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + playerUUID.toString() + ".yml")).exists();
    }

    @Override
    public boolean createUserdata(Player player) {
        try {
            File userdataFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
            userdataFile.createNewFile();
            FileWriter fw = new FileWriter(userdataFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Balance: 0");
            bw.close();
            return true;
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not create '{0}' userdata file!", player);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return false;
    }

    @Override
    public Double getBalance(Player player) {
        if (!hasUserdata(player)) {
            createUserdata(player);
        }
        try {
            File userdataFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
            FileReader fr = new FileReader(userdataFile);
            BufferedReader br = new BufferedReader(fr);
            Double balance = Double.parseDouble(br.readLine().split(":")[1]);
            br.close();
            fr.close();
            return balance;
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not get '{0}' balance!", player);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } catch (NumberFormatException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not get '{0}' balance!", player);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return null;
    }

    @Override
    public Double getBalance(UUID playerUUID) {
        try {
            File userdataFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + playerUUID.toString() + ".yml");
            FileReader fr = new FileReader(userdataFile);
            BufferedReader br = new BufferedReader(fr);
            Double balance = Double.parseDouble(br.readLine().split(":")[1]);
            br.close();
            fr.close();
            return balance;
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not get '{0}' balance!", playerUUID);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        } catch (NumberFormatException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not get '{0}' balance!", playerUUID);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean setBalance(Player player, Double amount) {
        if (!hasUserdata(player)) {
            createUserdata(player);
        }
        try {
            File userdataFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + player.getUniqueId().toString() + ".yml");
            FileReader fr = new FileReader(userdataFile);
            BufferedReader br = new BufferedReader(fr);
            String balances = br.readLine();
            br.close();
            fr.close();
            FileWriter fw = new FileWriter(userdataFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(balances.split(":")[0] + ": " + amount);
            bw.close();
            fw.close();
            return true;
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not set '{0}' balance!", player);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean setBalance(UUID playerUUID, Double amount) {
        try {
            File userdataFile = new File("plugins" + System.getProperty("file.separator") + plugin.getDescription().getName() + System.getProperty("file.separator") + "userdata" + System.getProperty("file.separator") + playerUUID.toString() + ".yml");
            FileReader fr = new FileReader(userdataFile);
            BufferedReader br = new BufferedReader(fr);
            String balances = br.readLine();
            br.close();
            fr.close();
            FileWriter fw = new FileWriter(userdataFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(balances.split(":")[0] + ": " + amount);
            bw.close();
            fw.close();
            return true;
        } catch (IOException e) {
            BankSystem.logger.log(Level.SEVERE, "Could not set '{0}' balance!", playerUUID);
            BankSystem.logger.log(Level.SEVERE, "{0}", e.getMessage());
        }
        return false;
    }
}
