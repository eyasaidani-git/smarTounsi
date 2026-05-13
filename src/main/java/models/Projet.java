package models;

import java.time.LocalDateTime;

public class Projet {

    private int id;
    private String nomProjet;
    private String description;
    private int idCreateur;

    private boolean contientCode;
    private boolean contientPresentation;
    private boolean contientRapport;

    private String fichierCode;
    private String fichierPresentation;
    private String fichierRapport;

    private String statut;
    private LocalDateTime dateCreation;

    public Projet() {
    }

    public Projet(String nomProjet, String description, int idCreateur,
                  boolean contientCode, boolean contientPresentation, boolean contientRapport) {
        this.nomProjet = nomProjet;
        this.description = description;
        this.idCreateur = idCreateur;
        this.contientCode = contientCode;
        this.contientPresentation = contientPresentation;
        this.contientRapport = contientRapport;
        this.statut = "en_cours";
    }

    public Projet(int id, String nomProjet, String description, int idCreateur,
                  boolean contientCode, boolean contientPresentation, boolean contientRapport,
                  String fichierCode, String fichierPresentation, String fichierRapport,
                  String statut, LocalDateTime dateCreation) {
        this.id = id;
        this.nomProjet = nomProjet;
        this.description = description;
        this.idCreateur = idCreateur;
        this.contientCode = contientCode;
        this.contientPresentation = contientPresentation;
        this.contientRapport = contientRapport;
        this.fichierCode = fichierCode;
        this.fichierPresentation = fichierPresentation;
        this.fichierRapport = fichierRapport;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getNom() {
        return nomProjet;
    }

    public void setNom(String nom) {
        this.nomProjet = nom;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }


    public boolean isContientCode() {
        return contientCode;
    }

    public void setContientCode(boolean contientCode) {
        this.contientCode = contientCode;
    }

    public boolean isContientPresentation() {
        return contientPresentation;
    }

    public void setContientPresentation(boolean contientPresentation) {
        this.contientPresentation = contientPresentation;
    }

    public boolean isContientRapport() {
        return contientRapport;
    }

    public void setContientRapport(boolean contientRapport) {
        this.contientRapport = contientRapport;
    }


    public String getFichierCode() {
        return fichierCode;
    }

    public void setFichierCode(String fichierCode) {
        this.fichierCode = fichierCode;
    }

    public String getFichierPresentation() {
        return fichierPresentation;
    }

    public void setFichierPresentation(String fichierPresentation) {
        this.fichierPresentation = fichierPresentation;
    }

    public String getFichierRapport() {
        return fichierRapport;
    }

    public void setFichierRapport(String fichierRapport) {
        this.fichierRapport = fichierRapport;
    }


    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }


    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", nomProjet='" + nomProjet + '\'' +
                ", description='" + description + '\'' +
                ", idCreateur=" + idCreateur +
                ", contientCode=" + contientCode +
                ", contientPresentation=" + contientPresentation +
                ", contientRapport=" + contientRapport +
                ", fichierCode='" + fichierCode + '\'' +
                ", fichierPresentation='" + fichierPresentation + '\'' +
                ", fichierRapport='" + fichierRapport + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}