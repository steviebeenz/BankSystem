package net.twiistrz.banksystem;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler {
    private final BankSystem plugin;

    public SoundHandler(BankSystem pl) {
        this.plugin = pl;
    }
    
    public void sendItemBreakSound(Player p) {
        if (plugin.is18Server) {
            p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1.0F, 1.0F);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
        }
    }

    public void sendClickSound(Player p) {
        if (plugin.is18Server) {
            p.playSound(p.getLocation(), Sound.valueOf("CLICK"), 1.0F, 1.0F);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1.0F, 1.0F);
        }
    }

    public void sendLevelUpSound(Player p) {
        if (plugin.is18Server) {
            p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0F, 1.0F);
        }
    }

    public void sendPlingSound(Player p) {
        if (plugin.is18Server) {
            p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3.0F, 3.0F);
        } else if (plugin.is113Server) {
            p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 1.0F, 1.0F);
        } else {
            p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 3.0F, 3.0F);
        }
    }
}
