package com.daishitie.banksystem.handlers;

import com.daishitie.banksystem.BankSystem;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler {
    private final BankSystem plugin;

    public SoundHandler(BankSystem pl) {
        this.plugin = pl;
    }

    public void sendItemBreakSound(Player p) {
        if (this.plugin.config().getBoolean("Settings.soundEnabled").booleanValue())
            if (this.plugin.is18Server) {
                p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1.0F, 1.0F);
            } else if (this.plugin.is19Server
                    || this.plugin.is110Server
                    || this.plugin.is111Server
                    || this.plugin.is112Server
                    || this.plugin.is113Server
                    || this.plugin.is114Server
                    || this.plugin.is115Server) {
                p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1.0F, 1.0F);
            }
    }

    public void sendClickSound(Player p) {
        if (this.plugin.config().getBoolean("Settings.soundEnabled").booleanValue())
            if (this.plugin.is18Server) {
                p.playSound(p.getLocation(), Sound.valueOf("CLICK"), 1.0F, 1.0F);
            } else if (this.plugin.is19Server
                    || this.plugin.is110Server
                    || this.plugin.is111Server
                    || this.plugin.is112Server
                    || this.plugin.is113Server
                    || this.plugin.is114Server
                    || this.plugin.is115Server) {
                p.playSound(p.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1.0F, 1.0F);
            }
    }

    public void sendLevelUpSound(Player p) {
        if (this.plugin.config().getBoolean("Settings.soundEnabled").booleanValue())
            if (this.plugin.is18Server) {
                p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0F, 1.0F);
            } else if (this.plugin.is19Server
                    || this.plugin.is110Server
                    || this.plugin.is111Server
                    || this.plugin.is112Server
                    || this.plugin.is113Server
                    || this.plugin.is114Server
                    || this.plugin.is115Server) {
                p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0F, 1.0F);
            }

    }

    public void sendPling(Player p) {
        if (this.plugin.config().getBoolean("Settings.soundEnabled").booleanValue())
            if (this.plugin.is18Server) {
                p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3.0F, 3.0F);
            } else if (this.plugin.is19Server
                    || this.plugin.is110Server
                    || this.plugin.is111Server
                    || this.plugin.is112Server) {
                p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 1.0F, 1.0F);
            } else if (this.plugin.is113Server
                    || this.plugin.is114Server
                    || this.plugin.is115Server) {
                p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 1.0F, 1.0F);
            }
    }
}
