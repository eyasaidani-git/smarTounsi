package models;

public class Quiz {
    private int id, idModule, idCreateur;
    private String titre, description;

    public Quiz() {}
    public Quiz(String titre, String description, int idModule, int idCreateur) {
        this.titre = titre;
        this.description = description;
        this.idModule = idModule;
        this.idCreateur = idCreateur;
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

    public int getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(int idCreateur) {
        this.idCreateur = idCreateur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", idModule=" + idModule +
                ", idCreateur=" + idCreateur +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
