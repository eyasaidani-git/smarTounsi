package models;
<<<<<<< HEAD
import enums.PlanningType;
import java.time.LocalDateTime;
public class Planning {
    private int id;
    private int idUtilisateur;
    private String titre;
    private LocalDateTime dateRevision;
    private PlanningType typeActivite;
    private LocalDateTime dateCreation;

    public Planning() {
    }

    public Planning(int idUtilisateur, String titre, String description, LocalDateTime dateRevision, PlanningType typeActivite) {
        this.idUtilisateur = idUtilisateur;
        this.titre = titre;
        this.dateRevision = dateRevision;
        this.typeActivite = typeActivite;
=======
import java.time.LocalDate;
public class Planning {
    private int id, idUtilisateur;
    private LocalDate dateRevision;
    private String titre;
    public Planning() {}

    public Planning(int idUtilisateur, LocalDate dateRevision, String titre) {
        this.idUtilisateur = idUtilisateur;
        this.dateRevision = dateRevision;
        this.titre = titre;
>>>>>>> origin/gestionikram
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

<<<<<<< HEAD
=======
    public LocalDate getDateRevision() {
        return dateRevision;
    }

    public void setDateRevision(LocalDate dateRevision) {
        this.dateRevision = dateRevision;
    }

>>>>>>> origin/gestionikram
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
<<<<<<< HEAD

    public LocalDateTime getDateRevision() {
        return dateRevision;
    }

    public void setDateRevision(LocalDateTime dateRevision) {
        this.dateRevision = dateRevision;
    }

    public PlanningType getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(PlanningType typeActivite) {
        this.typeActivite = typeActivite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Planning{" +
                "id=" + id +
                ", idUtilisateur=" + idUtilisateur +
                ", titre='" + titre + '\'' +
                ", dateRevision=" + dateRevision +
                ", typeActivite=" + typeActivite +
                ", dateCreation=" + dateCreation +
                '}';
    }
=======
>>>>>>> origin/gestionikram
}
