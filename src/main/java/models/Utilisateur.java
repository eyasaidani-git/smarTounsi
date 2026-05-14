package models;

import java.time.LocalDateTime;

public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private String photoProfil;
    private LocalDateTime dateInscription;
    private boolean estActif;

    private String filiere;
    private String annee;
    private String universite;
    private String numeroEtudiant;

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.estActif = true;
    }

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String role,
                       String filiere, String annee, String universite, String numeroEtudiant) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.filiere = filiere;
        this.annee = annee;
        this.universite = universite;
        this.numeroEtudiant = numeroEtudiant;
        this.estActif = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }


    public String getNomComplet() {
        String p = prenom == null ? "" : prenom;
        String n = nom == null ? "" : nom;
        return (p + " " + n).trim();
    }

    public void setNomComplet(String nomComplet) {
        if (nomComplet == null || nomComplet.isBlank()) {
            this.prenom = "";
            this.nom = "";
            return;
        }

        String[] parts = nomComplet.trim().split("\\s+", 2);

        if (parts.length == 1) {
            this.prenom = parts[0];
            this.nom = "";
        } else {
            this.prenom = parts[0];
            this.nom = parts[1];
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getPhotoProfil() {
        return photoProfil;
    }

    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }


    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }


    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public boolean isActif() {
        return estActif;
    }

    public void setActif(boolean actif) {
        this.estActif = actif;
    }


    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }


    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }


    public String getUniversite() {
        return universite;
    }

    public void setUniversite(String universite) {
        this.universite = universite;
    }


    public String getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(String numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }


    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", photoProfil='" + photoProfil + '\'' +
                ", dateInscription=" + dateInscription +
                ", estActif=" + estActif +
                ", filiere='" + filiere + '\'' +
                ", annee='" + annee + '\'' +
                ", universite='" + universite + '\'' +
                ", numeroEtudiant='" + numeroEtudiant + '\'' +
                '}';
    }
}