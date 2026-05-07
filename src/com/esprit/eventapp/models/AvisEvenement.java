package com.esprit.eventapp.models;

import java.sql.Timestamp;

public class AvisEvenement {
    private int idAvis;
    private int idEvenement;
    private String nomAuteur;
    private String commentaire;
    private int note;
    private Timestamp dateAvis;

    public AvisEvenement() {}

    public AvisEvenement(int idEvenement, String nomAuteur, String commentaire, int note) {
        this.idEvenement = idEvenement;
        this.nomAuteur = nomAuteur;
        this.commentaire = commentaire;
        this.note = note;
    }

    public AvisEvenement(int idAvis, int idEvenement, String nomAuteur, String commentaire, int note, Timestamp dateAvis) {
        this.idAvis = idAvis;
        this.idEvenement = idEvenement;
        this.nomAuteur = nomAuteur;
        this.commentaire = commentaire;
        this.note = note;
        this.dateAvis = dateAvis;
    }

    public int getIdAvis() { return idAvis; }
    public void setIdAvis(int idAvis) { this.idAvis = idAvis; }

    public int getIdEvenement() { return idEvenement; }
    public void setIdEvenement(int idEvenement) { this.idEvenement = idEvenement; }

    public String getNomAuteur() { return nomAuteur; }
    public void setNomAuteur(String nomAuteur) { this.nomAuteur = nomAuteur; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public Timestamp getDateAvis() { return dateAvis; }
    public void setDateAvis(Timestamp dateAvis) { this.dateAvis = dateAvis; }
}
