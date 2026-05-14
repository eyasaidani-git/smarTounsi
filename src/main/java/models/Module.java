package models;
<<<<<<< HEAD

import java.time.LocalDateTime;

public class Module {

    private int id;
    private String nomModule;
    private String description;
    private Integer idCreateur;
=======
import java.time.LocalDateTime;

public class Module {
    private int id;
    private String nom,description,icone;
>>>>>>> origin/gestionikram
    private LocalDateTime dateCreation;

    public Module() {
    }

<<<<<<< HEAD
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
=======
    public Module(String nom, String description, String icone) {
        this.nom = nom;
        this.description = description;
        this.icone = icone;
>>>>>>> origin/gestionikram
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

<<<<<<< HEAD

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


=======
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

>>>>>>> origin/gestionikram
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

<<<<<<< HEAD

    public Integer getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(Integer idCreateur) {
        this.idCreateur = idCreateur;
    }


=======
    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

>>>>>>> origin/gestionikram
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

<<<<<<< HEAD

=======
>>>>>>> origin/gestionikram
    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
<<<<<<< HEAD
                ", nomModule='" + nomModule + '\'' +
                ", description='" + description + '\'' +
                ", idCreateur=" + idCreateur +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
=======
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", icone='" + icone + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
>>>>>>> origin/gestionikram
