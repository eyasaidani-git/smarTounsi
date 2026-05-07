package models;
import java.time.LocalDateTime;

public class Module {
    private int id;
    private String nom,description,icone;
    private LocalDateTime dateCreation;

    public Module() {
    }

    public Module(String nom, String description, String icone) {
        this.nom = nom;
        this.description = description;
        this.icone = icone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
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
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", icone='" + icone + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
