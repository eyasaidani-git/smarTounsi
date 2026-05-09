package models;
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
}
