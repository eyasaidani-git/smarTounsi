package models;

import java.time.LocalDateTime;

public class Module {

    private int id;
    private String nomModule;
    private String description;
    private Integer idCreateur;
    private LocalDateTime dateCreation;

    public Module() {
    }

    public Module(String nomModule, String description, Integer idCreateur) {
        this.nomModule = nomModule;
        this.description = description;
        this.idCreateur = idCreateur;
    }

    public Module(int id, String nomModule, String description, Integer idCreateur, LocalDateTime dateCreation) {
        this.id = id;
        this.nomModule = nomModule;
        this.description = description;
        this.idCreateur = idCreateur;
        this.dateCreation = dateCreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNomModule() {
        return nomModule;
    }

    public void setNomModule(String nomModule) {
        this.nomModule = nomModule;
    }

    /*
     * Méthodes alias pour compatibilité avec les anciens services/controllers.
     * Si un ancien code utilise getNom() ou setNom(), il ne sera pas cassé.
     */
    public String getNom() {
        return nomModule;
    }

    public void setNom(String nom) {
        this.nomModule = nom;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(Integer idCreateur) {
        this.idCreateur = idCreateur;
    }


    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }


    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", nomModule='" + nomModule + '\'' +
                ", description='" + description + '\'' +
                ", idCreateur=" + idCreateur +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
