package models;

import java.io.File;
import java.time.LocalDate;

public class Planning {

    private int id;
    private int idUtilisateur;
    private LocalDate dateRevision;

    private String titre;
    private String type;
    private String heure;
    private String module;
    private String nomFichier;
    private String cheminFichier;

    public Planning(String titre, String type, String heure, String module, LocalDate selectedDate, File selectedFile) {
    }

    public Planning(int idUtilisateur, LocalDate dateRevision, String titre) {
        this.idUtilisateur = idUtilisateur;
        this.dateRevision = dateRevision;
        this.titre = titre;
    }

    public Planning(int idUtilisateur, LocalDate dateRevision, String titre, String type,
                    String heure, String module, String nomFichier, String cheminFichier) {
        this.idUtilisateur = idUtilisateur;
        this.dateRevision = dateRevision;
        this.titre = titre;
        this.type = type;
        this.heure = heure;
        this.module = module;
        this.nomFichier = nomFichier;
        this.cheminFichier = cheminFichier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }


    public LocalDate getDateRevision() {
        return dateRevision;
    }

    public void setDateRevision(LocalDate dateRevision) {
        this.dateRevision = dateRevision;
    }


    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }


    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }


    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }
}