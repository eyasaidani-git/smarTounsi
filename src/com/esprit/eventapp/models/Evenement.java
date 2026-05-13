package com.esprit.eventapp.models;

import java.time.LocalDateTime;

public class Evenement {
    private int idEvenement;
    private int createurId;
    private String titre;
    private String description;
    private String typeEvenement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private String statut;
    private String image;
    private double prix;
    private int capacity;

    public Evenement() {
    }

    public Evenement(String titre, String description, String typeEvenement,
            LocalDateTime dateDebut, LocalDateTime dateFin,
            String lieu, String statut, String image) {
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.statut = statut;
        this.image = image;
        this.prix = 0.0;
        this.capacity = 10;
    }

    public Evenement(int idEvenement, int createurId, String titre, String description, String typeEvenement,
            LocalDateTime dateDebut, LocalDateTime dateFin,
            String lieu, String statut, String image, double prix, int capacity) {
        this.idEvenement = idEvenement;
        this.createurId = createurId;
        this.titre = titre;
        this.description = description;
        this.typeEvenement = typeEvenement;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.statut = statut;
        this.image = image;
        this.prix = prix;
        this.capacity = capacity;
    }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public int getCreateurId() { return createurId; }
    public void setCreateurId(int createurId) { this.createurId = createurId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTypeEvenement() { return typeEvenement; }
    public void setTypeEvenement(String typeEvenement) { this.typeEvenement = typeEvenement; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
