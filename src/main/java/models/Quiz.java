package models;

<<<<<<< HEAD
import java.time.LocalDateTime;

public class Quiz {

    private int id;
    private String titre;
    private String description;
    private int idModule;
    private int idCreateur;
    private Integer tempsLimite;
    private int scoreTotal;
    private LocalDateTime dateCreation;
    private boolean estActif;

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


    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }


    public Integer getTempsLimite() {
        return tempsLimite;
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
        return "Quiz{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", idModule=" + idModule +
                ", idCreateur=" + idCreateur +
                ", tempsLimite=" + tempsLimite +
                ", scoreTotal=" + scoreTotal +
                ", dateCreation=" + dateCreation +
                ", estActif=" + estActif +
                '}';
=======
/**
 * Modèle Quiz — SmarTounsi
 * Ajout du champ nomModule pour l'affichage dans l'interface.
 */
public class Quiz {

    private int     id;
    private String  titre;
    private String  description;
    private int     idModule;
    private String  nomModule;     // ← NOUVEAU : nom lisible de la matière
    private int     idCreateur;
    private int     tempsLimite;
    private int     scoreTotal;
    private boolean estActif;

    // ===== Constructeurs =====

    public Quiz() {
        this.estActif   = true;
        this.tempsLimite = 30;
        this.scoreTotal  = 20;
    }

    // ===== Getters / Setters =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIdModule() { return idModule; }
    public void setIdModule(int idModule) { this.idModule = idModule; }

    public String getNomModule() { return nomModule; }
    public void setNomModule(String nomModule) { this.nomModule = nomModule; }

    public int getIdCreateur() { return idCreateur; }
    public void setIdCreateur(int idCreateur) { this.idCreateur = idCreateur; }

    public int getTempsLimite() { return tempsLimite; }
    public void setTempsLimite(int tempsLimite) { this.tempsLimite = tempsLimite; }

    public int getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(int scoreTotal) { this.scoreTotal = scoreTotal; }

    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    @Override
    public String toString() {
        return titre;
>>>>>>> origin/gestionikram
    }
}