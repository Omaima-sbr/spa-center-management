/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.spa2;

import java.time.LocalDateTime;

public class RendezVous {
    private int id;
    private int clientId;
    private int serviceId;
    private int employeId;
    private LocalDateTime dateHeure;
    private String statut;
    
    // Objets associés
    private Client client;
    private Service service;
    private Employe employe;
    
    // Constructeur par défaut
    public RendezVous() {
        this.statut = "planifié";
    }
    
    // Constructeur avec paramètres (sans id car auto-incrémenté)
    public RendezVous(int clientId, int serviceId, int employeId, LocalDateTime dateHeure) {
        this.clientId = clientId;
        this.serviceId = serviceId;
        this.employeId = employeId;
        this.dateHeure = dateHeure;
        this.statut = "planifié";
    }
    
    // Constructeur complet avec objets associés
    public RendezVous(Client client, Service service, Employe employe, LocalDateTime dateHeure) {
        this.client = client;
        this.clientId = client.getId();
        this.service = service;
        this.serviceId = service.getId();
        this.employe = employe;
        this.employeId = employe.getId();
        this.dateHeure = dateHeure;
        this.statut = "planifié";
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClientId() {
        return clientId;
    }
    
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    
    public int getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
    public int getEmployeId() {
        return employeId;
    }
    
    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    // Getters et Setters pour les objets associés
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
        this.clientId = client.getId();
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
        this.serviceId = service.getId();
    }
    
    public Employe getEmploye() {
        return employe;
    }
    
    public void setEmploye(Employe employe) {
        this.employe = employe;
        this.employeId = employe.getId();
    }
    
    @Override
    public String toString() {
        return "RendezVous [id=" + id + ", client=" + (client != null ? client.getNom() + " " + client.getPrenom() : "ID:" + clientId) 
            + ", service=" + (service != null ? service.getNom() : "ID:" + serviceId) 
            + ", employé=" + (employe != null ? employe.getNom() + " " + employe.getPrenom() : "ID:" + employeId) 
            + ", date et heure=" + dateHeure + ", statut=" + statut + "]";
    }
}