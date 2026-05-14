package models;
import java.time.LocalDateTime;
public class ProjetCollaborateur {
    private int id,idProjet,idUtilisateur;
    private String roleProjet;
    private LocalDateTime dateAjout;
    public ProjetCollaborateur() {
    }

    public ProjetCollaborateur(int idProjet, int idUtilisateur, String roleProjet) {
        this.idProjet = idProjet;
        this.idUtilisateur = idUtilisateur;
        this.roleProjet = roleProjet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getRoleProjet() {
        return roleProjet;
    }

    public void setRoleProjet(String roleProjet) {
        this.roleProjet = roleProjet;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

}
