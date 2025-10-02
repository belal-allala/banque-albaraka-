package com.albaraka.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/abonnement";
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "belal"; 
  
    public static Connection getConnection() throws SQLException {
        try{
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(ClassNotFoundException e){
            throw new SQLException("Driver Postgresql non trouve", e);
        }
    }
}