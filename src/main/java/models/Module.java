package models;
import java.time.LocalDateTime;

public class Module {
    private int id;
<<<<<<< HEAD
    private String nom,description;
=======
    private String nomModule,description;
>>>>>>> origin/GestionNour
    private Integer idCreateur;
    private LocalDateTime dateCreation;

    public Module() {
    }

    public Module(String nomModule, String description, Integer idCreateur) {
<<<<<<< HEAD
        this.nom = nomModule;
=======
        this.nomModule = nomModule;
>>>>>>> origin/GestionNour
        this.description = description;
        this.idCreateur = idCreateur;
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

<<<<<<< HEAD
    public void setNom(String nomModule) {
        this.nom = nomModule;
=======
    public void setNomModule(String nomModule) {
        this.nomModule = nomModule;
>>>>>>> origin/GestionNour
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
<<<<<<< HEAD
                ", nomModule='" + nom + '\'' +
=======
                ", nomModule='" + nomModule + '\'' +
>>>>>>> origin/GestionNour
                ", description='" + description + '\'' +
                ", idCreateur=" + idCreateur +
                ", dateCreation=" + dateCreation +
                '}';
    }
}