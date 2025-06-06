package com.mycompany.spa2;

public class Employe {
    
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String specialite;
    private String cni;  
     
    
     public Employe() {
    }
    // Constructeur
    public Employe( int id ,String nom, String prenom, String telephone, String specialite,String cni) {
        this.id=id;
        this.cni = cni;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.specialite = specialite;
    }
    
    // Getters
    public int getId() { return id; }
    public String getCni() { return cni; }  // Changé de getCNI() à getCni()
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getSpecialite() { return specialite; }

    // Setters
    public void setId(int id) { this.id=id; }
    public void setCni(String cni) { this.cni = cni; }  // Changé de setCNI() à setCni()
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
}