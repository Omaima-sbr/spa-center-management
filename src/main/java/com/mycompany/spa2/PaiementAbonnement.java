package com.mycompany.spa2;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaiementAbonnement {
    private int id;
    private Abonnement abonnement; // Relation avec la classe Abonnement
    private double montant;
    private double montantRestant;
    private String modePaiement; // Exemple : "Espèce", "Carte", "Virement"
    private LocalDateTime datePaiement; // Date et heure exactes du paiement
    private LocalDate periode; // Mois ou période couverte
    private String statut; // "Payé", "En attente", "En retard"

    public PaiementAbonnement() {}

    public PaiementAbonnement(int id, Abonnement abonnement, double montant,double montantRestant, String modePaiement,
                               LocalDateTime datePaiement, LocalDate periode, String statut) {
        this.id = id;
        this.abonnement = abonnement;
        this.montant = montant;
        this.montantRestant = montantRestant;
        this.modePaiement = modePaiement;
        this.datePaiement = datePaiement;
        this.periode = periode;
        this.statut = statut;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Abonnement getAbonnement() {
        return abonnement;
    }

    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
    }

    public double getMontant() {
        return montant;
    }
    
   

    public void setMontant(double montant) {
        this.montant = montant;
    }
    
     public double getMontantRestant() {
        return montantRestant;
    }
     
     public void setMontantRestant(double montantRestant) {
        this.montantRestant = montantRestant;
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

    public LocalDate getPeriode() {
        return periode;
    }

    public void setPeriode(LocalDate periode) {
        this.periode = periode;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    
     public String calculerStatutAutomatique() {
        LocalDate aujourdhui = LocalDate.now();
        
        if (montantRestant <= 0) {
            return "Payé";
        } else if (periode.isBefore(aujourdhui)) {
            return "En retard";
        } else {
            return "En attente";
        }
    }
     
}
