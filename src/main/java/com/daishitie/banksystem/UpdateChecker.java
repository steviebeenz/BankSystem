package com.daishitie.banksystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

public class UpdateChecker {

    private final BankSystem plugin;
    private final String resource;

    public UpdateChecker(BankSystem plugin, int resource) {
        this.plugin = plugin;
        this.resource = "https://api.spigotmc.org/legacy/update.php?resource=" + resource;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                InputStream inputStream = new URL(this.resource).openStream();
                Scanner scanner = new Scanner(inputStream);

                if (scanner.hasNext()) consumer.accept(scanner.next());
            } catch (IOException e) {
                this.plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}