package net.twiistrz.banksystem;

import java.text.DecimalFormat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 *
 * @author Twiistrz
 */
public class PlaceholderHandler extends PlaceholderExpansion {
    private final BankSystem plugin;

    /**
     *
     * @param pl Main Class
     */
    public PlaceholderHandler(BankSystem pl) {
        this.plugin = pl;
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String getIdentifier() {
        return "banksystem";
    }

    @Override
    public String getAuthor() {
        return "Mannyseete (Twisstrz)";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {    
        DecimalFormat money = new DecimalFormat("#,##0.00");
        Double bankBalance = 0.00;
        Double totalBalance = BankSystem.econ.getBalance(p);
        if (p == null) {
            return "";
        }
        if (plugin.getMoneyDatabaseInterface().hasUserdata(p)) {
            bankBalance = plugin.getMoneyDatabaseInterface().getBalance(p);
            totalBalance = BankSystem.econ.getBalance(p) + bankBalance;
        }
        if (identifier.equalsIgnoreCase("name")) {
            return p.getName();
        }
        if (identifier.equalsIgnoreCase("balance")) {
            return money.format(bankBalance);
        }
        if (identifier.equalsIgnoreCase("pocket")) {
            return money.format(BankSystem.econ.getBalance(p));
        }
        if (identifier.equalsIgnoreCase("total")) {
            return money.format(totalBalance);
        }
        return null;
    }
}
