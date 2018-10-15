package net.twiistrz.banksystem.database;

import net.twiistrz.banksystem.BankSystem;
import java.sql.Connection;

public final class DatabaseManagerFlatFile implements DatabaseManagerInterface {

    @SuppressWarnings("unused")
    private final BankSystem plugin;

    public DatabaseManagerFlatFile(BankSystem pl) {
        this.plugin = pl;
        setupDatabase();
    }

    @Override
    public boolean setupDatabase() {
        return true;
    }

    @Override
    public boolean closeDatabase() {
        return true;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

}
