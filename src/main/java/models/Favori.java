package models;
import java.time.LocalDateTime;

public class Favori {
    private int id,idUtilisateur,idDocument;
    private LocalDateTime dateAjout;

    public Favori() {
    }

    public Favori(int idUtilisateur, int idDocument) {
        this.idUtilisateur = idUtilisateur;
        this.idDocument = idDocument;
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

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    @Override
    public String toString() {
        return "Favori{" +
                "id=" + id +
                ", idUtilisateur=" + idUtilisateur +
                ", idDocument=" + idDocument +
                ", dateAjout=" + dateAjout +
                '}';
    }
}
