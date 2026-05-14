package models;

<<<<<<< HEAD
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String titre;
    private String message;
    private String type;
    private boolean lu;
    private LocalDateTime dateCreation;
    private int idUtilisateur;

    public Notification() {
    }

    public Notification(String titre, String message, String type, int idUtilisateur) {
        this.titre = titre;
        this.message = message;
        this.type = type;
        this.idUtilisateur = idUtilisateur;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
}
=======
public class Notification {
}
>>>>>>> origin/gestionikram
