/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.spa2;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author pc
 */
public class paiement {
  
    private int id;
    private Client client; // Relation avec la classe client
    private RendezVous rendezvous; // Relation avec la classe rendezvous
    private double montant;
    private String modePaiement; // Exemple : "Espèce", "Carte", "Virement"
    private LocalDateTime datePaiement; // Date et heure exactes du paiement
   
    public paiement() {}

    public paiement(int id, Client client,RendezVous rendezvous, double montant, String modePaiement,
                               LocalDateTime datePaiement) {
        this.id = id;
        this.client = client;
        this.montant = montant;
        this.rendezvous = rendezvous;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
       
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

   // Corrigez les setters problématiques
public void setClient(Client client) {
    this.client = client;
}



    public RendezVous getRendezVous() {
        return rendezvous;
    }

  public void setRendezVous(RendezVous rendezvous) {
    this.rendezvous = rendezvous;
}

    
    public double getMontant() {
        return montant;
    }
    
   

    public void setMontant(double montant) {
        this.montant = montant;
    }
    
     

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

}
     



