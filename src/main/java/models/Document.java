package models;
import java.time.LocalDateTime;

public class Document {
    private int id, idModule, idUtilisateur;
    private String titre, type, contenu;
    private LocalDateTime dateAjout;
    private boolean approuve;
    public Document() {}

    public Document(String titre, String type, String contenu, int idModule, int idUtilisateur) {
        this.titre = titre;
        this.type = type;
        this.contenu = contenu;
        this.idModule = idModule;
        this.idUtilisateur = idUtilisateur;
        this.approuve = false;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdModule() {
        return idModule;
    }

    public void setIdModule(int idModule) {
        this.idModule = idModule;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    public boolean isApprouve() {
        return approuve;
    }

    public void setApprouve(boolean approuve) {
        this.approuve = approuve;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", idModule=" + idModule +
                ", idUtilisateur=" + idUtilisateur +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", contenu='" + contenu + '\'' +
                ", dateAjout=" + dateAjout +
                ", approuve=" + approuve +
                '}';
    }
}
