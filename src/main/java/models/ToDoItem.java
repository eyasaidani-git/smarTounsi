package models;

public class ToDoItem {
    private int id, idPlanning, idDocument, dureeMinutes;
    private String titre, description, statut, heureDebut;
    public ToDoItem() {
    }

    public ToDoItem(int dureeMinutes, String titre, String description, String statut, String heureDebut) {

        this.dureeMinutes = dureeMinutes;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.heureDebut = heureDebut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlanning() {
        return idPlanning;
    }

    public void setIdPlanning(int idPlanning) {
        this.idPlanning = idPlanning;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", idPlanning=" + idPlanning +
                ", idDocument=" + idDocument +
                ", dureeMinutes=" + dureeMinutes +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", heureDebut='" + heureDebut + '\'' +
                '}';
    }
}
