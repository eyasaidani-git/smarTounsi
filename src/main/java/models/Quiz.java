package models;

import java.time.LocalDateTime;

public class Quiz {

    private int id;
    private String titre;
    private String description;
    private int idModule;
    private String nomModule;
    private int idCreateur;
    private Integer tempsLimite = 30;
    private int scoreTotal = 20;
    private LocalDateTime dateCreation;
    private boolean estActif = true;

    public Quiz() {
    }

    public Quiz(String titre, String description, int idModule, int idCreateur,
                Integer tempsLimite, int scoreTotal) {
        this.titre = titre;
        this.description = description;
        this.idModule = idModule;
        this.idCreateur = idCreateur;
        this.tempsLimite = tempsLimite;
        this.scoreTotal = scoreTotal;
        this.estActif = true;
    }

    public Quiz(int id, String titre, String description, int idModule, int idCreateur,
                Integer tempsLimite, int scoreTotal, LocalDateTime dateCreation, boolean estActif) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.idModule = idModule;
        this.idCreateur = idCreateur;
        this.tempsLimite = tempsLimite;
        this.scoreTotal = scoreTotal;
        this.dateCreation = dateCreation;
        this.estActif = estActif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

    public String getNomModule() {
        return nomModule;
    }

    public void setNomModule(String nomModule) {
        this.nomModule = nomModule;
    }

    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }

    public int getTempsLimite() {
        return tempsLimite == null ? 0 : tempsLimite;
    }

    public void setTempsLimite(int tempsLimite) {
        this.tempsLimite = tempsLimite;
    }

    public void setTempsLimite(Integer tempsLimite) {
        this.tempsLimite = tempsLimite;
    }

    public Integer getTempsMinutes() {
        return tempsLimite;
    }

    public void setTempsMinutes(Integer tempsMinutes) {
        this.tempsLimite = tempsMinutes;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
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

    @Override
    public String toString() {
        return titre == null ? "" : titre;
    }
}
