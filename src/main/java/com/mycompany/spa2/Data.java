/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.spa2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Data {
    
     private Connection databaseLink;
     
     public Connection getConnexion() {
        String databaseName = "spa_database";
        String databaseUser = "root";
        String databasePassword = "Omixa2004";
        String url = "jdbc:mysql://localhost/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.databaseLink;
    }
}
