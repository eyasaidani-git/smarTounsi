package com.esprit.eventapp.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {
    private int idEvenement;
    private String titre;
    private String description;
    private String typeEvenement;
    private LocalDate dateEvenement;
    private LocalTime heureEvenement;
    private String lieu;
    private String statut;

    public Evenement() {}

    public Evenement(String titre, String description, String typeEvenement,
                     LocalDate dateEvenement, LocalTime heureEvenement,
                     String lieu, String statut) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateEvenement = dateEvenement;
        this.heureEvenement = heureEvenement;
        this.lieu = lieu;
        this.statut = statut;
    }

    public Evenement(int idEvenement, String titre, String description, String typeEvenement,
                     LocalDate dateEvenement, LocalTime heureEvenement,
                     String lieu, String statut) {
        this.idEvenement = idEvenement;
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateEvenement = dateEvenement;
        this.heureEvenement = heureEvenement;
        this.lieu = lieu;
        this.statut = statut;
    }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTypeEvenement() { return typeEvenement; }
    public void setTypeEvenement(String typeEvenement) { this.typeEvenement = typeEvenement; }

    public LocalDate getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDate dateEvenement) { this.dateEvenement = dateEvenement; }

    public LocalTime getHeureEvenement() { return heureEvenement; }
    public void setHeureEvenement(LocalTime heureEvenement) { this.heureEvenement = heureEvenement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
