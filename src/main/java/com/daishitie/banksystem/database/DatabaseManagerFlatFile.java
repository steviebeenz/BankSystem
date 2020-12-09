package com.daishitie.banksystem.database;

import java.sql.Connection;

import com.daishitie.banksystem.BankSystem;

public final class DatabaseManagerFlatFile implements DatabaseManagerInterface {
    public DatabaseManagerFlatFile(BankSystem pl) {
        setupDatabase();
    }

    public boolean setupDatabase() {
        return true;
    }

    public boolean closeDatabase() {
        return true;
    }

    public Connection getConnection() {
        return null;
    }
}
