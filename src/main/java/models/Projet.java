package models;

public class Projet {
    private int id, idCreateur;
    private String nom, description, fichierCode, fichierPresentation, fichierRapport;
    private boolean aCode, aPresentation, aRapport;

    public Projet() {}
    public Projet(String nom, String description, boolean aCode,
                  boolean aPresentation, boolean aRapport, int idCreateur) {
        this.nom = nom;
        this.description = description;
        this.aCode = aCode;
        this.aPresentation = aPresentation;
        this.aRapport = aRapport;
        this.idCreateur = idCreateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isaCode() {
        return aCode;
    }

    public void setaCode(boolean aCode) {
        this.aCode = aCode;
    }

    public boolean isaPresentation() {
        return aPresentation;
    }

    public void setaPresentation(boolean aPresentation) {
        this.aPresentation = aPresentation;
    }

    public boolean isaRapport() {
        return aRapport;
    }

    public void setaRapport(boolean aRapport) {
        this.aRapport = aRapport;
    }
}
