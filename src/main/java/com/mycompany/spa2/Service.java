/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.spa2;

/**
 *
 * @author pc
 */
public class Service {

    private int id;
    private String nom;
    private String description;
    private int duree;
    private double prix;
    
    
    // Constructeur vide
    public Service() {
    }
    
    // Constructeur
    public Service(int id, String nom, String description,int duree ,double prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.duree = duree;
        this.prix = prix;
    }

    // Getters et Setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public int getDuree() { return duree; }
    public double getPrix() { return prix; }
    
    public void setId(int id) {   this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setDuree(int duree) { this.duree = duree; }
    public void setPrix(double prix) { this.prix = prix; }


}
