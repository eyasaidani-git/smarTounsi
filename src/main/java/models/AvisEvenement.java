package models;
import java.time.LocalDateTime;
public class AvisEvenement {
    private int id,note,idEvenement,idUtilisateur;
    private String commentaire;
    private LocalDateTime dateAvis;

    public AvisEvenement() {
    }

    public AvisEvenement(int note, String commentaire, int idEvenement, int idUtilisateur) {
        this.note = note;
        this.commentaire = commentaire;
        this.idEvenement = idEvenement;
        this.idUtilisateur = idUtilisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateAvis() {
        return dateAvis;
    }

    public void setDateAvis(LocalDateTime dateAvis) {
        this.dateAvis = dateAvis;
    }
}
