package com.daishitie.banksystem.database;

import java.sql.Connection;

public interface DatabaseManagerInterface {
    boolean setupDatabase();

    boolean closeDatabase();

    Connection getConnection();
}
