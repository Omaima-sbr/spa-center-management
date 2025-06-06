package com.mycompany.spa2;
import java.time.LocalDate;

public class Abonnement {
    private int id;
    private Client client;      // Relation avec la classe Client
    private Service service;    // Relation avec la classe Service
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String typeAbonnement; // "Mensuel" ou "Annuel"

    // --- Constructeurs ---
    public Abonnement() {}

    public Abonnement(int id, Client client, Service service, LocalDate dateDebut, LocalDate dateFin, String typeAbonnement) {
        this.id = id;
        this.client = client;
        this.service = service;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.typeAbonnement = typeAbonnement;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(String typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    // --- Méthode utilitaire ---
    public boolean estActif() {
        return dateFin != null && !dateFin.isBefore(LocalDate.now());
    }
}
